package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/FastStringWriter.class */
public final class FastStringWriter extends Writer {
    private final StringBuilder builder;

    public FastStringWriter() {
        this.builder = new StringBuilder();
    }

    public FastStringWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative buffer size");
        }
        this.builder = new StringBuilder(initialSize);
    }

    @Override // java.io.Writer
    public void write(int c) {
        this.builder.append((char) c);
    }

    @Override // java.io.Writer
    public void write(String str) {
        this.builder.append(str);
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) {
        this.builder.append((CharSequence) str, off, off + len);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) {
        this.builder.append(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) {
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.builder.append(cbuf, off, len);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    public String toString() {
        return this.builder.toString();
    }
}