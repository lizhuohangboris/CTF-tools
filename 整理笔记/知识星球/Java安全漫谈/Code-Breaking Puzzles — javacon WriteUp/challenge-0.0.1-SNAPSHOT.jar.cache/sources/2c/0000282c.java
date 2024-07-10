package org.thymeleaf.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.LoggingUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ThrottledTemplateProcessor.class */
public final class ThrottledTemplateProcessor implements IThrottledTemplateProcessor {
    private static final int NANOS_IN_SECOND = 1000000;
    private static final String OUTPUT_TYPE_CHARS = "chars";
    private static final String OUTPUT_TYPE_BYTES = "bytes";
    private final TemplateSpec templateSpec;
    private final IEngineContext context;
    private final TemplateModel templateModel;
    private final ITemplateHandler templateHandler;
    private final ProcessorTemplateHandler processorTemplateHandler;
    private final TemplateFlowController flowController;
    private final ThrottledTemplateWriter writer;
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TemplateEngine.TIMER_LOGGER_NAME);
    private static final AtomicLong identifierGenerator = new AtomicLong(0);
    private final String identifier = Long.toString(identifierGenerator.getAndIncrement());
    private int offset = 0;
    private boolean eventProcessingFinished = false;
    private volatile boolean allProcessingFinished = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThrottledTemplateProcessor(TemplateSpec templateSpec, IEngineContext context, TemplateModel templateModel, ITemplateHandler templateHandler, ProcessorTemplateHandler processorTemplateHandler, TemplateFlowController flowController, ThrottledTemplateWriter writer) {
        this.templateSpec = templateSpec;
        this.context = context;
        this.templateModel = templateModel;
        this.templateHandler = templateHandler;
        this.processorTemplateHandler = processorTemplateHandler;
        this.flowController = flowController;
        this.writer = writer;
    }

    public IThrottledTemplateWriterControl getThrottledTemplateWriterControl() {
        return this.writer;
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public boolean isFinished() {
        return this.allProcessingFinished;
    }

    private boolean computeFinish() throws IOException {
        if (this.allProcessingFinished) {
            return true;
        }
        boolean finished = (!this.eventProcessingFinished || this.flowController.processorTemplateHandlerPending || this.writer.isOverflown()) ? false : true;
        if (finished) {
            this.allProcessingFinished = finished;
        }
        return finished;
    }

    private void reportFinish(String outputType) {
        if (this.allProcessingFinished && logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished throttled processing of template \"{}\" with locale {}. Maximum overflow was {} {} (overflow buffer grown {} times).", TemplateEngine.threadIndex(), this.templateSpec, this.context.getLocale(), Integer.valueOf(this.writer.getMaxOverflowSize()), outputType, Integer.valueOf(this.writer.getOverflowGrowCount()));
        }
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public String getProcessorIdentifier() {
        return this.identifier;
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public TemplateSpec getTemplateSpec() {
        return this.templateSpec;
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public int processAll(Writer writer) {
        this.writer.setOutput(writer);
        return process(Integer.MAX_VALUE, OUTPUT_TYPE_CHARS);
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public int processAll(OutputStream outputStream, Charset charset) {
        this.writer.setOutput(outputStream, charset, Integer.MAX_VALUE);
        return process(Integer.MAX_VALUE, OUTPUT_TYPE_BYTES);
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public int process(int maxOutputInChars, Writer writer) {
        this.writer.setOutput(writer);
        return process(maxOutputInChars, OUTPUT_TYPE_CHARS);
    }

    @Override // org.thymeleaf.IThrottledTemplateProcessor
    public int process(int maxOutputInBytes, OutputStream outputStream, Charset charset) {
        this.writer.setOutput(outputStream, charset, maxOutputInBytes);
        return process(maxOutputInBytes, OUTPUT_TYPE_BYTES);
    }

    private int process(int maxOutput, String outputType) {
        try {
            if (this.allProcessingFinished || maxOutput == 0) {
                return 0;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Starting throttled process (limit:{} {}) of template \"{}\" with locale {}", TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, this.templateSpec, this.context.getLocale());
            }
            long startNanos = System.nanoTime();
            int initialWrittenCount = this.writer.getWrittenCount();
            this.writer.allow(maxOutput);
            if (!computeFinish() && !this.writer.isStopped()) {
                if (this.flowController.processorTemplateHandlerPending) {
                    this.processorTemplateHandler.handlePending();
                }
                if (!computeFinish() && !this.writer.isStopped()) {
                    this.offset += this.templateModel.process(this.templateHandler, this.offset, this.flowController);
                    if (this.offset == this.templateModel.size()) {
                        EngineContextManager.disposeEngineContext(this.context);
                        this.eventProcessingFinished = true;
                        computeFinish();
                    }
                }
            }
            long endNanos = System.nanoTime();
            try {
                this.writer.flush();
                int writtenCount = this.writer.getWrittenCount() - initialWrittenCount;
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Finished throttled process (limit:{} {}, output: {} {}) of template \"{}\" with locale {}", TemplateEngine.threadIndex(), Integer.valueOf(maxOutput), outputType, Integer.valueOf(writtenCount), outputType, this.templateSpec, this.context.getLocale());
                }
                if (timerLogger.isTraceEnabled()) {
                    BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
                    BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf((long) Time.APR_USEC_PER_SEC), RoundingMode.HALF_UP);
                    timerLogger.trace("[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED (THROTTLED, LIMIT:{} {}, OUTPUT: {} {}) IN {} nanoseconds (approx. {}ms)", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(this.templateSpec.getTemplate()), this.context.getLocale(), elapsed, elapsedMs, this.templateSpec, this.context.getLocale(), Integer.valueOf(maxOutput), outputType, Integer.valueOf(writtenCount), outputType, elapsed, elapsedMs);
                }
                reportFinish(outputType);
                return writtenCount;
            } catch (IOException e) {
                throw new TemplateOutputException("An error happened while flushing output writer", this.templateSpec.getTemplate(), -1, -1, e);
            }
        } catch (TemplateOutputException e2) {
            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", TemplateEngine.threadIndex(), this.templateSpec, e2.getMessage()), (Throwable) e2);
            throw e2;
        } catch (TemplateEngineException e3) {
            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", TemplateEngine.threadIndex(), this.templateSpec, e3.getMessage()), (Throwable) e3);
            throw e3;
        } catch (Exception e4) {
            this.eventProcessingFinished = true;
            this.allProcessingFinished = true;
            logger.error(String.format("[THYMELEAF][%s] Exception processing throttled template \"%s\": %s", TemplateEngine.threadIndex(), this.templateSpec, e4.getMessage()), (Throwable) e4);
            throw new TemplateProcessingException("Exception processing throttled template", this.templateSpec.toString(), e4);
        }
    }
}