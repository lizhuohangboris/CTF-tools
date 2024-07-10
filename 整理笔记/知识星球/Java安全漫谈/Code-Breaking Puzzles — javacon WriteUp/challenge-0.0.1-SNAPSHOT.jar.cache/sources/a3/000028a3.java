package org.thymeleaf.spring5;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.engine.DataDrivenTemplateIterator;
import org.thymeleaf.engine.ISSEThrottledTemplateWriterControl;
import org.thymeleaf.engine.IThrottledTemplateWriterControl;
import org.thymeleaf.engine.ThrottledTemplateProcessor;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.IReactiveSSEDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContextFactory;
import org.thymeleaf.spring5.linkbuilder.webflux.SpringWebFluxLinkBuilder;
import org.thymeleaf.util.LoggingUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine.class */
public class SpringWebFluxTemplateEngine extends SpringTemplateEngine implements ISpringWebFluxTemplateEngine {
    private static final Logger logger = LoggerFactory.getLogger(SpringWebFluxTemplateEngine.class);
    private static final String LOG_CATEGORY_FULL_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.FULL";
    private static final String LOG_CATEGORY_CHUNKED_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.CHUNKED";
    private static final String LOG_CATEGORY_DATADRIVEN_INPUT = SpringWebFluxTemplateEngine.class.getName() + ".UPSTREAM.DATA-DRIVEN";
    private static final String LOG_CATEGORY_DATADRIVEN_OUTPUT = SpringWebFluxTemplateEngine.class.getName() + ".DOWNSTREAM.DATA-DRIVEN";

    public SpringWebFluxTemplateEngine() {
        setEngineContextFactory(new SpringWebFluxEngineContextFactory());
        setLinkBuilder(new SpringWebFluxLinkBuilder());
    }

    @Override // org.thymeleaf.spring5.ISpringWebFluxTemplateEngine
    public Publisher<DataBuffer> processStream(String template, Set<String> markupSelectors, IContext context, DataBufferFactory bufferFactory, MediaType mediaType, Charset charset) {
        return processStream(template, markupSelectors, context, bufferFactory, mediaType, charset, Integer.MAX_VALUE);
    }

    @Override // org.thymeleaf.spring5.ISpringWebFluxTemplateEngine
    public Publisher<DataBuffer> processStream(String template, Set<String> markupSelectors, IContext context, DataBufferFactory bufferFactory, MediaType mediaType, Charset charset, int responseMaxChunkSizeBytes) {
        if (template == null) {
            return Flux.error(new IllegalArgumentException("Template cannot be null"));
        }
        if (context == null) {
            return Flux.error(new IllegalArgumentException("Context cannot be null"));
        }
        if (bufferFactory == null) {
            return Flux.error(new IllegalArgumentException("Buffer Factory cannot be null"));
        }
        if (mediaType == null) {
            return Flux.error(new IllegalArgumentException("Media Type cannot be null"));
        }
        if (charset == null) {
            return Flux.error(new IllegalArgumentException("Charset cannot be null"));
        }
        if (responseMaxChunkSizeBytes == 0) {
            return Flux.error(new IllegalArgumentException("Max Chunk Size cannot be zero"));
        }
        int chunkSizeBytes = responseMaxChunkSizeBytes < 0 ? Integer.MAX_VALUE : responseMaxChunkSizeBytes;
        boolean sse = MediaType.TEXT_EVENT_STREAM.includes(mediaType);
        try {
            String dataDriverVariableName = findDataDriverInModel(context);
            if (dataDriverVariableName != null) {
                return createDataDrivenStream(template, markupSelectors, context, dataDriverVariableName, bufferFactory, charset, chunkSizeBytes, sse);
            }
            if (sse) {
                return Flux.error(new TemplateProcessingException("SSE mode has been requested ('Accept: text/event-stream') but no data-driver variable has been added to the model/context. In order to perform SSE rendering, a variable implementing the " + IReactiveDataDriverContextVariable.class.getName() + " interface is required."));
            }
            if (chunkSizeBytes == Integer.MAX_VALUE) {
                return createFullStream(template, markupSelectors, context, bufferFactory, charset);
            }
            return createChunkedStream(template, markupSelectors, context, bufferFactory, charset, responseMaxChunkSizeBytes);
        } catch (Throwable t) {
            return Flux.error(t);
        }
    }

    private Mono<DataBuffer> createFullStream(String templateName, Set<String> markupSelectors, IContext context, DataBufferFactory bufferFactory, Charset charset) {
        Mono<DataBuffer> stream = Mono.create(subscriber -> {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] STARTING STREAM PROCESS (FULL MODE) OF TEMPLATE \"{}\" WITH LOCALE {}", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName), context.getLocale());
            }
            DataBuffer dataBuffer = bufferFactory.allocateBuffer();
            OutputStreamWriter writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset);
            try {
                process(templateName, markupSelectors, context, writer);
                int bytesProduced = dataBuffer.readableByteCount();
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] FINISHED STREAM PROCESS (FULL MODE) OF TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced));
                }
                subscriber.success(dataBuffer);
            } catch (Throwable t) {
                logger.error(String.format("[THYMELEAF][%s] Exception processing template \"%s\": %s", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(templateName), t.getMessage()), t);
                subscriber.error(t);
            }
        });
        return stream.log(LOG_CATEGORY_FULL_OUTPUT, Level.FINEST, new SignalType[0]);
    }

    private Flux<DataBuffer> createChunkedStream(String templateName, Set<String> markupSelectors, IContext context, DataBufferFactory bufferFactory, Charset charset, int responseMaxChunkSizeBytes) {
        Flux<DataBuffer> stream = Flux.generate(() -> {
            return new StreamThrottledTemplateProcessor(processThrottled(templateName, markupSelectors, context), null, null, 0L, false);
        }, throttledProcessor, emitter -> {
            throttledProcessor.startChunk();
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (CHUNKED MODE, THROTTLER ID \"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}", TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(), throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()), LoggingUtils.loggifyTemplateName(templateName), context.getLocale());
            }
            DataBuffer buffer = bufferFactory.allocateBuffer(responseMaxChunkSizeBytes);
            try {
                int bytesProduced = throttledProcessor.process(responseMaxChunkSizeBytes, buffer.asOutputStream(), charset);
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (CHUNKED MODE, THROTTLER ID \"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES", TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(), throttledProcessor.getProcessorIdentifier(), Integer.valueOf(throttledProcessor.getChunkCount()), LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced));
                }
                emitter.next(buffer);
                if (throttledProcessor.isFinished()) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (CHUNKED MODE, THROTTLER ID \"{}\") FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED A TOTAL OF {} BYTES IN {} CHUNKS", TemplateEngine.threadIndex(), throttledProcessor.getProcessorIdentifier(), throttledProcessor.getProcessorIdentifier(), LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Long.valueOf(throttledProcessor.getTotalBytesProduced()), Integer.valueOf(throttledProcessor.getChunkCount() + 1));
                    }
                    emitter.complete();
                }
                return throttledProcessor;
            } catch (Throwable t) {
                emitter.error(t);
                return null;
            }
        });
        return stream.log(LOG_CATEGORY_CHUNKED_OUTPUT, Level.FINEST, new SignalType[0]);
    }

    private Flux<DataBuffer> createDataDrivenStream(String templateName, Set<String> markupSelectors, IContext context, String dataDriverVariableName, DataBufferFactory bufferFactory, Charset charset, int responseMaxChunkSizeBytes, boolean sse) {
        IReactiveDataDriverContextVariable dataDriver = (IReactiveDataDriverContextVariable) context.getVariable(dataDriverVariableName);
        int bufferSizeElements = dataDriver.getBufferSizeElements();
        String sseEventsPrefix = dataDriver instanceof IReactiveSSEDataDriverContextVariable ? ((IReactiveSSEDataDriverContextVariable) dataDriver).getSseEventsPrefix() : null;
        long sseEventsID = dataDriver instanceof IReactiveSSEDataDriverContextVariable ? ((IReactiveSSEDataDriverContextVariable) dataDriver).getSseEventsFirstID() : 0L;
        ReactiveAdapterRegistry reactiveAdapterRegistry = context instanceof SpringWebFluxContext ? ((SpringWebFluxContext) context).getReactiveAdapterRegistry() : null;
        DataDrivenTemplateIterator dataDrivenIterator = new DataDrivenTemplateIterator();
        IContext wrappedContext = applyDataDriverWrapper(context, dataDriverVariableName, dataDrivenIterator);
        Flux<List<Object>> dataDrivenBufferedStream = Flux.from(dataDriver.getDataStream(reactiveAdapterRegistry)).buffer(bufferSizeElements).log(LOG_CATEGORY_DATADRIVEN_INPUT, Level.FINEST, new SignalType[0]);
        Flux<DataDrivenFluxStep> dataDrivenWithContextStream = Flux.using(() -> {
            String outputContentType = sse ? MediaType.TEXT_EVENT_STREAM_VALUE : null;
            TemplateSpec templateSpec = new TemplateSpec(templateName, markupSelectors, outputContentType, (Map<String, Object>) null);
            return new StreamThrottledTemplateProcessor(processThrottled(templateSpec, wrappedContext), dataDrivenIterator, sseEventsPrefix, sseEventsID, sse);
        }, throttledProcessor -> {
            return Flux.concat(Flux.generate(() -> {
                return DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_HEAD;
            }, phase, emitter -> {
                if (throttledProcessor.isFinished()) {
                    emitter.complete();
                    return null;
                }
                switch (phase) {
                    case DATA_DRIVEN_PHASE_HEAD:
                        emitter.next(Mono.just(DataDrivenFluxStep.forHead(throttledProcessor)));
                        return DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_BUFFER;
                    case DATA_DRIVEN_PHASE_BUFFER:
                        emitter.next(dataDrivenBufferedStream.map(values -> {
                            return DataDrivenFluxStep.forBuffer(throttledProcessor, values);
                        }));
                        return DataDrivenFluxStep.FluxStepPhase.DATA_DRIVEN_PHASE_TAIL;
                    case DATA_DRIVEN_PHASE_TAIL:
                        emitter.next(Mono.just(DataDrivenFluxStep.forTail(throttledProcessor)));
                        emitter.complete();
                        return null;
                    default:
                        return null;
                }
            }));
        }, throttledProcessor2 -> {
        });
        Flux<DataBuffer> stream = dataDrivenWithContextStream.concatMap(step -> {
            return Flux.generate(() -> {
                return Boolean.TRUE;
            }, initialize, emitter -> {
                DataBuffer allocateBuffer;
                StreamThrottledTemplateProcessor throttledProcessor3 = step.getThrottledProcessor();
                DataDrivenTemplateIterator dataDrivenTemplateIterator = throttledProcessor3.getDataDrivenTemplateIterator();
                if (throttledProcessor3.isFinished()) {
                    emitter.complete();
                    return Boolean.FALSE;
                }
                if (initialize.booleanValue()) {
                    if (step.isHead()) {
                        dataDrivenTemplateIterator.startHead();
                    } else if (step.isDataBuffer()) {
                        dataDrivenTemplateIterator.feedBuffer(step.getValues());
                    } else {
                        dataDrivenTemplateIterator.feedingComplete();
                        dataDrivenTemplateIterator.startTail();
                    }
                }
                throttledProcessor3.startChunk();
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}][{}] STARTING PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID \"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}", TemplateEngine.threadIndex(), throttledProcessor3.getProcessorIdentifier(), throttledProcessor3.getProcessorIdentifier(), Integer.valueOf(throttledProcessor3.getChunkCount()), LoggingUtils.loggifyTemplateName(templateName), context.getLocale());
                }
                if (responseMaxChunkSizeBytes != Integer.MAX_VALUE) {
                    allocateBuffer = bufferFactory.allocateBuffer(responseMaxChunkSizeBytes);
                } else {
                    allocateBuffer = bufferFactory.allocateBuffer();
                }
                DataBuffer buffer = allocateBuffer;
                try {
                    int bytesProduced = throttledProcessor3.process(responseMaxChunkSizeBytes, buffer.asOutputStream(), charset);
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}][{}] FINISHED PARTIAL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID \"{}\", CHUNK {}) FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED {} BYTES", TemplateEngine.threadIndex(), throttledProcessor3.getProcessorIdentifier(), throttledProcessor3.getProcessorIdentifier(), Integer.valueOf(throttledProcessor3.getChunkCount()), LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Integer.valueOf(bytesProduced));
                    }
                    if (bytesProduced == 0) {
                        dataDrivenTemplateIterator.takeBackLastEventID();
                    }
                    boolean phaseFinished = false;
                    if (throttledProcessor3.isFinished()) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("[THYMELEAF][{}][{}] FINISHED ALL STREAM PROCESS (DATA-DRIVEN MODE, THROTTLER ID \"{}\") FOR TEMPLATE \"{}\" WITH LOCALE {}. PRODUCED A TOTAL OF {} BYTES IN {} CHUNKS", TemplateEngine.threadIndex(), throttledProcessor3.getProcessorIdentifier(), throttledProcessor3.getProcessorIdentifier(), LoggingUtils.loggifyTemplateName(templateName), context.getLocale(), Long.valueOf(throttledProcessor3.getTotalBytesProduced()), Integer.valueOf(throttledProcessor3.getChunkCount() + 1));
                        }
                        phaseFinished = true;
                        dataDrivenTemplateIterator.finishStep();
                    } else if (step.isHead() && dataDrivenTemplateIterator.hasBeenQueried()) {
                        phaseFinished = true;
                        dataDrivenTemplateIterator.finishStep();
                    } else if (step.isDataBuffer() && !dataDrivenTemplateIterator.continueBufferExecution()) {
                        phaseFinished = true;
                    }
                    boolean stepOutputFinished = dataDrivenTemplateIterator.isStepOutputFinished();
                    emitter.next(buffer);
                    if (phaseFinished && stepOutputFinished) {
                        emitter.complete();
                    }
                    return Boolean.FALSE;
                } catch (Throwable t) {
                    emitter.error(t);
                    return Boolean.FALSE;
                }
            });
        });
        return stream.log(LOG_CATEGORY_DATADRIVEN_OUTPUT, Level.FINEST, new SignalType[0]);
    }

    private static IContext applyDataDriverWrapper(IContext context, String dataDriverVariableName, DataDrivenTemplateIterator dataDrivenTemplateIterator) {
        if (context instanceof IEngineContext) {
            ((IEngineContext) context).setVariable(dataDriverVariableName, dataDrivenTemplateIterator);
            return context;
        } else if (context instanceof ISpringWebFluxContext) {
            return new DataDrivenSpringWebFluxContextWrapper((ISpringWebFluxContext) context, dataDriverVariableName, dataDrivenTemplateIterator);
        } else {
            return new DataDrivenContextWrapper(context, dataDriverVariableName, dataDrivenTemplateIterator);
        }
    }

    private static String findDataDriverInModel(IContext context) {
        String dataDriverVariableName = null;
        Set<String> contextVariableNames = context.getVariableNames();
        for (String contextVariableName : contextVariableNames) {
            Object contextVariableValue = context.getVariable(contextVariableName);
            if (contextVariableValue instanceof IReactiveDataDriverContextVariable) {
                if (dataDriverVariableName != null) {
                    throw new TemplateProcessingException("Only one data-driver variable is allowed to be specified as a model attribute, but at least two have been identified: '" + dataDriverVariableName + "' and '" + contextVariableName + "'");
                }
                dataDriverVariableName = contextVariableName;
            }
        }
        return dataDriverVariableName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine$StreamThrottledTemplateProcessor.class */
    public static class StreamThrottledTemplateProcessor {
        private final IThrottledTemplateProcessor throttledProcessor;
        private final DataDrivenTemplateIterator dataDrivenTemplateIterator;
        private int chunkCount;
        private long totalBytesProduced;

        StreamThrottledTemplateProcessor(IThrottledTemplateProcessor throttledProcessor, DataDrivenTemplateIterator dataDrivenTemplateIterator, String sseEventsPrefix, long sseEventsFirstID, boolean sse) {
            IThrottledTemplateWriterControl writerControl;
            this.throttledProcessor = throttledProcessor;
            this.dataDrivenTemplateIterator = dataDrivenTemplateIterator;
            if (this.throttledProcessor instanceof ThrottledTemplateProcessor) {
                writerControl = ((ThrottledTemplateProcessor) this.throttledProcessor).getThrottledTemplateWriterControl();
            } else {
                writerControl = null;
            }
            if (sse) {
                if (writerControl == null || !(writerControl instanceof ISSEThrottledTemplateWriterControl)) {
                    throw new TemplateProcessingException("Cannot process template in Server-Sent Events (SSE) mode: template writer is not SSE capable. Either SSE content type has not been declared at the " + TemplateSpec.class.getSimpleName() + " or an implementation of " + IThrottledTemplateProcessor.class.getName() + " other than " + ThrottledTemplateProcessor.class.getName() + " is being used.");
                }
                if (this.dataDrivenTemplateIterator == null) {
                    throw new TemplateProcessingException("Cannot process template in Server-Sent Events (SSE) mode: a data-driven template iterator is required in context in order to apply SSE.");
                }
            }
            if (this.dataDrivenTemplateIterator != null) {
                this.dataDrivenTemplateIterator.setWriterControl(writerControl);
                this.dataDrivenTemplateIterator.setSseEventsPrefix(sseEventsPrefix);
                this.dataDrivenTemplateIterator.setSseEventsFirstID(sseEventsFirstID);
            }
            this.chunkCount = -1;
            this.totalBytesProduced = 0L;
        }

        int process(int maxOutputInBytes, OutputStream outputStream, Charset charset) {
            int chunkBytes = this.throttledProcessor.process(maxOutputInBytes, outputStream, charset);
            this.totalBytesProduced += chunkBytes;
            return chunkBytes;
        }

        String getProcessorIdentifier() {
            return this.throttledProcessor.getProcessorIdentifier();
        }

        boolean isFinished() {
            return this.throttledProcessor.isFinished();
        }

        void startChunk() {
            this.chunkCount++;
        }

        int getChunkCount() {
            return this.chunkCount;
        }

        long getTotalBytesProduced() {
            return this.totalBytesProduced;
        }

        DataDrivenTemplateIterator getDataDrivenTemplateIterator() {
            return this.dataDrivenTemplateIterator;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine$DataDrivenFluxStep.class */
    public static final class DataDrivenFluxStep {
        private final StreamThrottledTemplateProcessor throttledProcessor;
        private final List<Object> values;
        private final FluxStepPhase phase;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine$DataDrivenFluxStep$FluxStepPhase.class */
        public enum FluxStepPhase {
            DATA_DRIVEN_PHASE_HEAD,
            DATA_DRIVEN_PHASE_BUFFER,
            DATA_DRIVEN_PHASE_TAIL
        }

        static DataDrivenFluxStep forHead(StreamThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, FluxStepPhase.DATA_DRIVEN_PHASE_HEAD);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static DataDrivenFluxStep forBuffer(StreamThrottledTemplateProcessor throttledProcessor, List<Object> values) {
            return new DataDrivenFluxStep(throttledProcessor, values, FluxStepPhase.DATA_DRIVEN_PHASE_BUFFER);
        }

        static DataDrivenFluxStep forTail(StreamThrottledTemplateProcessor throttledProcessor) {
            return new DataDrivenFluxStep(throttledProcessor, null, FluxStepPhase.DATA_DRIVEN_PHASE_TAIL);
        }

        private DataDrivenFluxStep(StreamThrottledTemplateProcessor throttledProcessor, List<Object> values, FluxStepPhase phase) {
            this.throttledProcessor = throttledProcessor;
            this.values = values;
            this.phase = phase;
        }

        StreamThrottledTemplateProcessor getThrottledProcessor() {
            return this.throttledProcessor;
        }

        List<Object> getValues() {
            return this.values;
        }

        boolean isHead() {
            return this.phase == FluxStepPhase.DATA_DRIVEN_PHASE_HEAD;
        }

        boolean isDataBuffer() {
            return this.phase == FluxStepPhase.DATA_DRIVEN_PHASE_BUFFER;
        }

        boolean isTail() {
            return this.phase == FluxStepPhase.DATA_DRIVEN_PHASE_TAIL;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine$DataDrivenSpringWebFluxContextWrapper.class */
    public static class DataDrivenSpringWebFluxContextWrapper extends DataDrivenContextWrapper implements ISpringWebFluxContext {
        private final ISpringWebFluxContext context;

        DataDrivenSpringWebFluxContextWrapper(ISpringWebFluxContext context, String dataDriverVariableName, DataDrivenTemplateIterator dataDrivenTemplateIterator) {
            super(context, dataDriverVariableName, dataDrivenTemplateIterator);
            this.context = context;
        }

        @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
        public ServerHttpRequest getRequest() {
            return this.context.getRequest();
        }

        @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
        public ServerHttpResponse getResponse() {
            return this.context.getResponse();
        }

        @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
        public Mono<WebSession> getSession() {
            return this.context.getSession();
        }

        @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
        public ServerWebExchange getExchange() {
            return this.context.getExchange();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringWebFluxTemplateEngine$DataDrivenContextWrapper.class */
    public static class DataDrivenContextWrapper implements IContext {
        private final IContext context;
        private final String dataDriverVariableName;
        private final DataDrivenTemplateIterator dataDrivenTemplateIterator;

        DataDrivenContextWrapper(IContext context, String dataDriverVariableName, DataDrivenTemplateIterator dataDrivenTemplateIterator) {
            this.context = context;
            this.dataDriverVariableName = dataDriverVariableName;
            this.dataDrivenTemplateIterator = dataDrivenTemplateIterator;
        }

        public IContext getWrappedContext() {
            return this.context;
        }

        @Override // org.thymeleaf.context.IContext
        public Locale getLocale() {
            return this.context.getLocale();
        }

        @Override // org.thymeleaf.context.IContext
        public boolean containsVariable(String name) {
            return this.context.containsVariable(name);
        }

        @Override // org.thymeleaf.context.IContext
        public Set<String> getVariableNames() {
            return this.context.getVariableNames();
        }

        @Override // org.thymeleaf.context.IContext
        public Object getVariable(String name) {
            if (this.dataDriverVariableName.equals(name)) {
                return this.dataDrivenTemplateIterator;
            }
            return this.context.getVariable(name);
        }
    }
}