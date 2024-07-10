package org.springframework.boot.loader.jar;

import ch.qos.logback.core.FileAppender;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/jar/ZipInflaterInputStream.class */
class ZipInflaterInputStream extends InflaterInputStream {
    private int available;
    private boolean extraBytesWritten;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZipInflaterInputStream(InputStream inputStream, int size) {
        super(inputStream, new Inflater(true), getInflaterBufferSize(size));
        this.available = size;
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        if (this.available < 0) {
            return super.available();
        }
        return this.available;
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);
        if (result != -1) {
            this.available -= result;
        }
        return result;
    }

    @Override // java.util.zip.InflaterInputStream, java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        this.inf.end();
    }

    @Override // java.util.zip.InflaterInputStream
    protected void fill() throws IOException {
        try {
            super.fill();
        } catch (EOFException ex) {
            if (this.extraBytesWritten) {
                throw ex;
            }
            this.len = 1;
            this.buf[0] = 0;
            this.extraBytesWritten = true;
            this.inf.setInput(this.buf, 0, this.len);
        }
    }

    private static int getInflaterBufferSize(long size) {
        long size2 = size + 2;
        long size3 = size2 > 65536 ? FileAppender.DEFAULT_BUFFER_SIZE : size2;
        return (int) (size3 <= 0 ? 4096L : size3);
    }
}