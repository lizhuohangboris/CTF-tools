package org.springframework.boot.loader.data;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/data/RandomAccessDataFile.class */
public class RandomAccessDataFile implements RandomAccessData {
    private final FileAccess fileAccess;
    private final long offset;
    private final long length;

    public RandomAccessDataFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        }
        this.fileAccess = new FileAccess(file);
        this.offset = 0L;
        this.length = file.length();
    }

    private RandomAccessDataFile(FileAccess fileAccess, long offset, long length) {
        this.fileAccess = fileAccess;
        this.offset = offset;
        this.length = length;
    }

    public File getFile() {
        return this.fileAccess.file;
    }

    @Override // org.springframework.boot.loader.data.RandomAccessData
    public InputStream getInputStream() throws IOException {
        return new DataInputStream();
    }

    @Override // org.springframework.boot.loader.data.RandomAccessData
    public RandomAccessData getSubsection(long offset, long length) {
        if (offset < 0 || length < 0 || offset + length > this.length) {
            throw new IndexOutOfBoundsException();
        }
        return new RandomAccessDataFile(this.fileAccess, this.offset + offset, length);
    }

    @Override // org.springframework.boot.loader.data.RandomAccessData
    public byte[] read() throws IOException {
        return read(0L, this.length);
    }

    @Override // org.springframework.boot.loader.data.RandomAccessData
    public byte[] read(long offset, long length) throws IOException {
        if (offset > this.length) {
            throw new IndexOutOfBoundsException();
        }
        if (offset + length > this.length) {
            throw new EOFException();
        }
        byte[] bytes = new byte[(int) length];
        read(bytes, offset, 0, bytes.length);
        return bytes;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int readByte(long position) throws IOException {
        if (position >= this.length) {
            return -1;
        }
        return this.fileAccess.readByte(this.offset + position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int read(byte[] bytes, long position, int offset, int length) throws IOException {
        if (position > this.length) {
            return -1;
        }
        return this.fileAccess.read(bytes, this.offset + position, offset, length);
    }

    @Override // org.springframework.boot.loader.data.RandomAccessData
    public long getSize() {
        return this.length;
    }

    public void close() throws IOException {
        this.fileAccess.close();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/data/RandomAccessDataFile$DataInputStream.class */
    private class DataInputStream extends InputStream {
        private int position;

        private DataInputStream() {
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int read = RandomAccessDataFile.this.readByte(this.position);
            if (read > -1) {
                moveOn(1);
            }
            return read;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            return read(b, 0, b != null ? b.length : 0);
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("Bytes must not be null");
            }
            return doRead(b, off, len);
        }

        public int doRead(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            int cappedLen = cap(len);
            if (cappedLen <= 0) {
                return -1;
            }
            return (int) moveOn(RandomAccessDataFile.this.read(b, this.position, off, cappedLen));
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0L;
            }
            return moveOn(cap(n));
        }

        private int cap(long n) {
            return (int) Math.min(RandomAccessDataFile.this.length - this.position, n);
        }

        private long moveOn(int amount) {
            this.position += amount;
            return amount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/data/RandomAccessDataFile$FileAccess.class */
    public static final class FileAccess {
        private final Object monitor;
        private final File file;
        private RandomAccessFile randomAccessFile;

        private FileAccess(File file) {
            this.monitor = new Object();
            this.file = file;
            openIfNecessary();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int read(byte[] bytes, long position, int offset, int length) throws IOException {
            int read;
            synchronized (this.monitor) {
                openIfNecessary();
                this.randomAccessFile.seek(position);
                read = this.randomAccessFile.read(bytes, offset, length);
            }
            return read;
        }

        private void openIfNecessary() {
            if (this.randomAccessFile == null) {
                try {
                    this.randomAccessFile = new RandomAccessFile(this.file, "r");
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException(String.format("File %s must exist", this.file.getAbsolutePath()));
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void close() throws IOException {
            synchronized (this.monitor) {
                if (this.randomAccessFile != null) {
                    this.randomAccessFile.close();
                    this.randomAccessFile = null;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int readByte(long position) throws IOException {
            int read;
            synchronized (this.monitor) {
                openIfNecessary();
                this.randomAccessFile.seek(position);
                read = this.randomAccessFile.read();
            }
            return read;
        }
    }
}