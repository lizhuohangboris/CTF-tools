package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.thymeleaf.engine.ThrottledTemplateWriter;
import org.thymeleaf.exceptions.TemplateOutputException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ThrottledTemplateWriterWriterAdapter.class */
public final class ThrottledTemplateWriterWriterAdapter extends Writer implements ThrottledTemplateWriter.IThrottledTemplateWriterAdapter {
    private static int OVERFLOW_BUFFER_INCREMENT = 256;
    private final String templateName;
    private final TemplateFlowController flowController;
    private Writer writer;
    private char[] overflow = null;
    private int overflowSize = 0;
    private int maxOverflowSize = 0;
    private int overflowGrowCount = 0;
    private boolean unlimited = false;
    private int limit = 0;
    private int writtenCount = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThrottledTemplateWriterWriterAdapter(String templateName, TemplateFlowController flowController) {
        this.templateName = templateName;
        this.flowController = flowController;
        this.flowController.stopProcessing = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWriter(Writer writer) {
        this.writer = writer;
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
                this.writer.write(this.overflow, 0, this.overflowSize);
                if (!this.unlimited) {
                    this.limit -= this.overflowSize;
                }
                this.writtenCount += this.overflowSize;
                this.overflowSize = 0;
                return;
            }
            this.writer.write(this.overflow, 0, this.limit);
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

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        if (this.limit == 0) {
            overflow(c);
            return;
        }
        this.writer.write(c);
        if (!this.unlimited) {
            this.limit--;
        }
        this.writtenCount++;
        if (this.limit == 0) {
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        int len = str.length();
        if (this.limit == 0) {
            overflow(str, 0, len);
        } else if (this.unlimited || this.limit > len) {
            this.writer.write(str, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.writer.write(str, 0, this.limit);
            if (this.limit < len) {
                overflow(str, this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        if (this.limit == 0) {
            overflow(str, off, len);
        } else if (this.unlimited || this.limit > len) {
            this.writer.write(str, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.writer.write(str, off, this.limit);
            if (this.limit < len) {
                overflow(str, off + this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        int len = cbuf.length;
        if (this.limit == 0) {
            overflow(cbuf, 0, len);
        } else if (this.unlimited || this.limit > len) {
            this.writer.write(cbuf, 0, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.writer.write(cbuf, 0, this.limit);
            if (this.limit < len) {
                overflow(cbuf, this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.limit == 0) {
            overflow(cbuf, off, len);
        } else if (this.unlimited || this.limit > len) {
            this.writer.write(cbuf, off, len);
            if (!this.unlimited) {
                this.limit -= len;
            }
            this.writtenCount += len;
        } else {
            this.writer.write(cbuf, off, this.limit);
            if (this.limit < len) {
                overflow(cbuf, off + this.limit, len - this.limit);
            }
            this.writtenCount += this.limit;
            this.limit = 0;
            this.flowController.stopProcessing = true;
        }
    }

    private void overflow(int c) {
        ensureOverflowCapacity(1);
        this.overflow[this.overflowSize] = (char) c;
        this.overflowSize++;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }

    private void overflow(String str, int off, int len) {
        ensureOverflowCapacity(len);
        str.getChars(off, off + len, this.overflow, this.overflowSize);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }

    private void overflow(char[] cbuf, int off, int len) {
        ensureOverflowCapacity(len);
        System.arraycopy(cbuf, off, this.overflow, this.overflowSize, len);
        this.overflowSize += len;
        if (this.overflowSize > this.maxOverflowSize) {
            this.maxOverflowSize = this.overflowSize;
        }
    }

    private void ensureOverflowCapacity(int len) {
        if (this.overflow == null) {
            this.overflow = new char[((len / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT];
            return;
        }
        int targetLen = this.overflowSize + len;
        if (this.overflow.length < targetLen) {
            this.overflow = Arrays.copyOf(this.overflow, ((targetLen / OVERFLOW_BUFFER_INCREMENT) + 1) * OVERFLOW_BUFFER_INCREMENT);
            this.overflowGrowCount++;
        }
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