package org.thymeleaf.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.thymeleaf.exceptions.TemplateOutputException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ThrottledTemplateWriter.class */
class ThrottledTemplateWriter extends Writer implements IThrottledTemplateWriterControl {
    private final String templateName;
    private final TemplateFlowController flowController;
    private IThrottledTemplateWriterAdapter adapter = null;
    private Writer writer = null;
    private boolean flushable = false;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ThrottledTemplateWriter$IThrottledTemplateWriterAdapter.class */
    interface IThrottledTemplateWriterAdapter {
        boolean isOverflown();

        boolean isStopped();

        int getWrittenCount();

        int getMaxOverflowSize();

        int getOverflowGrowCount();

        void allow(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThrottledTemplateWriter(String templateName, TemplateFlowController flowController) {
        this.templateName = templateName;
        this.flowController = flowController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOutput(Writer writer) {
        if (this.adapter != null && (this.adapter instanceof ThrottledTemplateWriterOutputStreamAdapter)) {
            throw new TemplateOutputException("The throttled processor has already been initialized to use byte-based output (OutputStream), but a Writer has been specified.", this.templateName, -1, -1, null);
        }
        if (this.adapter == null) {
            this.adapter = new ThrottledTemplateWriterWriterAdapter(this.templateName, this.flowController);
            this.writer = (ThrottledTemplateWriterWriterAdapter) this.adapter;
        }
        ((ThrottledTemplateWriterWriterAdapter) this.adapter).setWriter(writer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOutput(OutputStream outputStream, Charset charset, int maxOutputInBytes) {
        int min;
        int min2;
        if (this.adapter != null && (this.adapter instanceof ThrottledTemplateWriterWriterAdapter)) {
            throw new TemplateOutputException("The throttled processor has already been initialized to use char-based output (Writer), but an OutputStream has been specified.", this.templateName, -1, -1, null);
        }
        if (this.adapter == null) {
            if (maxOutputInBytes == Integer.MAX_VALUE) {
                min = 128;
            } else {
                min = Math.min(128, Math.max(16, maxOutputInBytes / 8));
            }
            int adapterOverflowBufferIncrementBytes = min;
            this.adapter = new ThrottledTemplateWriterOutputStreamAdapter(this.templateName, this.flowController, adapterOverflowBufferIncrementBytes);
            CharsetEncoder charsetEncoder = charset.newEncoder();
            if (maxOutputInBytes == Integer.MAX_VALUE) {
                min2 = 1024;
            } else {
                min2 = Math.min(512, Math.max(64, adapterOverflowBufferIncrementBytes * 2));
            }
            int channelBufferSize = min2;
            WritableByteChannel channel = Channels.newChannel((ThrottledTemplateWriterOutputStreamAdapter) this.adapter);
            this.writer = Channels.newWriter(channel, charsetEncoder, channelBufferSize);
        }
        ((ThrottledTemplateWriterOutputStreamAdapter) this.adapter).setOutputStream(outputStream);
    }

    @Override // org.thymeleaf.engine.IThrottledTemplateWriterControl
    public boolean isOverflown() throws IOException {
        if (this.flushable) {
            flush();
            this.flushable = false;
        }
        return this.adapter.isOverflown();
    }

    @Override // org.thymeleaf.engine.IThrottledTemplateWriterControl
    public boolean isStopped() throws IOException {
        if (this.flushable) {
            flush();
            this.flushable = false;
        }
        return this.adapter.isStopped();
    }

    @Override // org.thymeleaf.engine.IThrottledTemplateWriterControl
    public int getWrittenCount() {
        return this.adapter.getWrittenCount();
    }

    @Override // org.thymeleaf.engine.IThrottledTemplateWriterControl
    public int getMaxOverflowSize() {
        return this.adapter.getMaxOverflowSize();
    }

    @Override // org.thymeleaf.engine.IThrottledTemplateWriterControl
    public int getOverflowGrowCount() {
        return this.adapter.getOverflowGrowCount();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void allow(int limit) {
        this.adapter.allow(limit);
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        this.flushable = true;
        this.writer.write(c);
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        this.flushable = true;
        this.writer.write(str);
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        this.flushable = true;
        this.writer.write(str, off, len);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        this.flushable = true;
        this.writer.write(cbuf);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.flushable = true;
        this.writer.write(cbuf, off, len);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.writer.close();
    }
}