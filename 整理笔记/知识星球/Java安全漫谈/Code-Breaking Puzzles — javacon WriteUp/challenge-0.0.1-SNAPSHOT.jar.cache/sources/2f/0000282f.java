package org.thymeleaf.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.thymeleaf.engine.ThrottledTemplateWriter;
import org.thymeleaf.exceptions.TemplateOutputException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ThrottledTemplateWriterOutputStreamAdapter.class */
public final class ThrottledTemplateWriterOutputStreamAdapter extends OutputStream implements ThrottledTemplateWriter.IThrottledTemplateWriterAdapter {
    private final String templateName;
    private final TemplateFlowController flowController;
    private final int overflowIncrementInBytes;
    private OutputStream os;
    private byte[] overflow = null;
    private int overflowSize = 0;
    private int maxOverflowSize = 0;
    private int overflowGrowCount = 0;
    private boolean unlimited = false;
    private int limit = 0;
    private int writtenCount = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThrottledTemplateWriterOutputStreamAdapter(String templateName, TemplateFlowController flowController, int overflowIncrementInBytes) {
        this.templateName = templateName;
        this.flowController = flowController;
        this.overflowIncrementInBytes = overflowIncrementInBytes;
        this.flowController.stopProcessing = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOutputStream(OutputStream os) {
        this.os = os;
        this.writtenCount = 0;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public boolean isOverflown() {
        return this.overflowSize > 0;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public boolean isStopped() {
        return this.limit == 0;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public int getWrittenCount() {
        return this.writtenCount;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public int getMaxOverflowSize() {
        return this.maxOverflowSize;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public int getOverflowGrowCount() {
        return this.overflowGrowCount;
    }

    @Override // org.thymeleaf.engine.ThrottledTemplateWriter.IThrottledTemplateWriterAdapter
    public void allow(int limit) {
        if (limit == Integer.MAX_VALUE || limit < 0) {
            this.unlimited = true;
            this.limit = -1;
        } else {
            this.unlimited = false;
            this.limit = limit;
        }
        this.flowController.stopProcessing = this.limit == 0;
        if (this.overflowSize == 0 || this.limit == 0) {
            return;
        }
        try {
            if (this.unlimited || this.limit > this.overflowSize) {
                this.os.write(this.overflow, 0, this.overflowSize);
                if (!this.unlimited) {
                    this.limit -= this.overflowSize;
                }
                this.writtenCount += this.overflowSize;
                this.overflowSize = 0;
                return;
            }
            this.os.write(this.overflow, 0, this.limit);
            if (this.limit < this.overflowSize) {
                System.arraycopy(this.overflow, this.limit, this.overflow, 0, this.overflowSize - this.limit);
            }
            this.overflowSize -= this.limit;
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        } catch (IOException e) {
            throw new TemplateOutputException("Exception while trying to write overflowed buffer in throttled template", this.templateName, -1, -1, e);
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        if (this.limit == 0) {
            overflow(b);
            return;
        }
        this.os.write(b);
        if (!this.unlimited) {
            this.limit--;
        }
        this.writtenCount++;
        if (this.limit == 0) {
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int off, int len) throws IOException {
        if (this.limit == 0) {
            overflow(bytes, off, len);
        } else if (this.unlimited || this.limit > len) {
            this.os.write(bytes, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.os.write(bytes, off, this.limit);
            if (this.limit < len) {
                overflow(bytes, off + this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes) throws IOException {
        int len = bytes.length;
        if (this.limit == 0) {
            overflow(bytes, 0, len);
        } else if (this.unlimited || this.limit > len) {
            this.os.write(bytes, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.os.write(bytes, 0, this.limit);
            if (this.limit < len) {
                overflow(bytes, this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    private void overflow(int c) {
        ensureOverflowCapacity(1);
        this.overflow[this.overflowSize] = (byte) c;
        this.overflowSize++;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }

    private void overflow(byte[] bytes, int off, int len) {
        ensureOverflowCapacity(len);
        System.arraycopy(bytes, off, this.overflow, this.overflowSize, len);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }

    private void ensureOverflowCapacity(int len) {
        if (this.overflow == null) {
            int i = this.overflowIncrementInBytes * 3;
            while (true) {
                int bufferInitialSize = i;
                if (bufferInitialSize < len) {
                    i = bufferInitialSize + this.overflowIncrementInBytes;
                } else {
                    this.overflow = new byte[bufferInitialSize];
                    return;
                }
            }
        } else {
            int targetLen = this.overflowSize + len;
            if (this.overflow.length < targetLen) {
                int newLen = this.overflow.length;
                do {
                    newLen += this.overflowIncrementInBytes;
                } while (newLen < targetLen);
                this.overflow = Arrays.copyOf(this.overflow, newLen);
                this.overflowGrowCount++;
            }
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.os.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.os.close();
    }
}