package org.springframework.core.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DefaultDataBuffer.class */
public class DefaultDataBuffer implements DataBuffer {
    private static final int MAX_CAPACITY = Integer.MAX_VALUE;
    private static final int CAPACITY_THRESHOLD = 4194304;
    private final DefaultDataBufferFactory dataBufferFactory;
    private ByteBuffer byteBuffer;
    private int capacity;
    private int readPosition;
    private int writePosition;

    private DefaultDataBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        Assert.notNull(dataBufferFactory, "DefaultDataBufferFactory must not be null");
        Assert.notNull(byteBuffer, "ByteBuffer must not be null");
        this.dataBufferFactory = dataBufferFactory;
        ByteBuffer slice = byteBuffer.slice();
        this.byteBuffer = slice;
        this.capacity = slice.remaining();
    }

    public static DefaultDataBuffer fromFilledByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        DefaultDataBuffer dataBuffer = new DefaultDataBuffer(dataBufferFactory, byteBuffer);
        dataBuffer.writePosition(byteBuffer.remaining());
        return dataBuffer;
    }

    public static DefaultDataBuffer fromEmptyByteBuffer(DefaultDataBufferFactory dataBufferFactory, ByteBuffer byteBuffer) {
        return new DefaultDataBuffer(dataBufferFactory, byteBuffer);
    }

    public ByteBuffer getNativeBuffer() {
        return this.byteBuffer;
    }

    private void setNativeBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        this.capacity = byteBuffer.remaining();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "'predicate' must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.writePosition) {
            return -1;
        }
        for (int i = fromIndex; i < this.writePosition; i++) {
            byte b = this.byteBuffer.get(i);
            if (predicate.test(b)) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "'predicate' must not be null");
        for (int i = Math.min(fromIndex, this.writePosition - 1); i >= 0; i--) {
            byte b = this.byteBuffer.get(i);
            if (predicate.test(b)) {
                return i;
            }
        }
        return -1;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.writePosition - this.readPosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.capacity - this.writePosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.readPosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer readPosition(int readPosition) {
        assertIndex(readPosition >= 0, "'readPosition' %d must be >= 0", Integer.valueOf(readPosition));
        assertIndex(readPosition <= this.writePosition, "'readPosition' %d must be <= %d", Integer.valueOf(readPosition), Integer.valueOf(this.writePosition));
        this.readPosition = readPosition;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.writePosition;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer writePosition(int writePosition) {
        assertIndex(writePosition >= this.readPosition, "'writePosition' %d must be >= %d", Integer.valueOf(writePosition), Integer.valueOf(this.readPosition));
        assertIndex(writePosition <= this.capacity, "'writePosition' %d must be <= %d", Integer.valueOf(writePosition), Integer.valueOf(this.capacity));
        this.writePosition = writePosition;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.capacity;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer capacity(int newCapacity) {
        Assert.isTrue(newCapacity > 0, String.format("'newCapacity' %d must be higher than 0", Integer.valueOf(newCapacity)));
        int readPosition = readPosition();
        int writePosition = writePosition();
        int oldCapacity = capacity();
        if (newCapacity > oldCapacity) {
            ByteBuffer oldBuffer = this.byteBuffer;
            ByteBuffer newBuffer = allocate(newCapacity, oldBuffer.isDirect());
            oldBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            setNativeBuffer(newBuffer);
        } else if (newCapacity < oldCapacity) {
            ByteBuffer oldBuffer2 = this.byteBuffer;
            ByteBuffer newBuffer2 = allocate(newCapacity, oldBuffer2.isDirect());
            if (readPosition < newCapacity) {
                if (writePosition > newCapacity) {
                    writePosition = newCapacity;
                    writePosition(writePosition);
                }
                oldBuffer2.position(readPosition).limit(writePosition);
                newBuffer2.position(readPosition).limit(writePosition);
                newBuffer2.put(oldBuffer2);
                newBuffer2.clear();
            } else {
                readPosition(newCapacity);
                writePosition(newCapacity);
            }
            setNativeBuffer(newBuffer2);
        }
        return this;
    }

    private static ByteBuffer allocate(int capacity, boolean direct) {
        return direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        assertIndex(index >= 0, "index %d must be >= 0", Integer.valueOf(index));
        assertIndex(index <= this.writePosition - 1, "index %d must be <= %d", Integer.valueOf(index), Integer.valueOf(this.writePosition - 1));
        return this.byteBuffer.get(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        assertIndex(this.readPosition <= this.writePosition - 1, "readPosition %d must be <= %d", Integer.valueOf(this.readPosition), Integer.valueOf(this.writePosition - 1));
        int pos = this.readPosition;
        byte b = this.byteBuffer.get(pos);
        this.readPosition = pos + 1;
        return b;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer read(byte[] destination) {
        Assert.notNull(destination, "'destination' must not be null");
        read(destination, 0, destination.length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer read(byte[] destination, int offset, int length) {
        Assert.notNull(destination, "'destination' must not be null");
        assertIndex(this.readPosition <= this.writePosition - length, "readPosition %d and length %d should be smaller than writePosition %d", Integer.valueOf(this.readPosition), Integer.valueOf(length), Integer.valueOf(this.writePosition));
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.readPosition + length;
        tmp.clear().position(this.readPosition).limit(limit);
        tmp.get(destination, offset, length);
        this.readPosition += length;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte b) {
        ensureCapacity(1);
        int pos = this.writePosition;
        this.byteBuffer.put(pos, b);
        this.writePosition = pos + 1;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte[] source) {
        Assert.notNull(source, "'source' must not be null");
        write(source, 0, source.length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(byte[] source, int offset, int length) {
        Assert.notNull(source, "'source' must not be null");
        ensureCapacity(length);
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + length;
        tmp.clear().position(this.writePosition).limit(limit);
        tmp.put(source, offset, length);
        this.writePosition += length;
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(DataBuffer... buffers) {
        if (!ObjectUtils.isEmpty((Object[]) buffers)) {
            ByteBuffer[] byteBuffers = (ByteBuffer[]) Arrays.stream(buffers).map((v0) -> {
                return v0.asByteBuffer();
            }).toArray(x$0 -> {
                return new ByteBuffer[x$0];
            });
            write(byteBuffers);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer write(ByteBuffer... byteBuffers) {
        Assert.notEmpty(byteBuffers, "'byteBuffers' must not be empty");
        int capacity = Arrays.stream(byteBuffers).mapToInt((v0) -> {
            return v0.remaining();
        }).sum();
        ensureCapacity(capacity);
        Arrays.stream(byteBuffers).forEach(this::write);
        return this;
    }

    private void write(ByteBuffer source) {
        int length = source.remaining();
        ByteBuffer tmp = this.byteBuffer.duplicate();
        int limit = this.writePosition + source.remaining();
        tmp.clear().position(this.writePosition).limit(limit);
        tmp.put(source);
        this.writePosition += length;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public DefaultDataBuffer slice(int index, int length) {
        checkIndex(index, length);
        int oldPosition = this.byteBuffer.position();
        Buffer buffer = this.byteBuffer;
        try {
            buffer.position(index);
            ByteBuffer slice = this.byteBuffer.slice();
            slice.limit(length);
            SlicedDefaultDataBuffer slicedDefaultDataBuffer = new SlicedDefaultDataBuffer(slice, this.dataBufferFactory, length);
            buffer.position(oldPosition);
            return slicedDefaultDataBuffer;
        } catch (Throwable th) {
            buffer.position(oldPosition);
            throw th;
        }
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer() {
        return asByteBuffer(this.readPosition, readableByteCount());
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer(int index, int length) {
        checkIndex(index, length);
        ByteBuffer duplicate = this.byteBuffer.duplicate();
        duplicate.position(index);
        duplicate.limit(index + length);
        return duplicate.slice();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream() {
        return new DefaultDataBufferInputStream();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream(boolean releaseOnClose) {
        return new DefaultDataBufferInputStream();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public OutputStream asOutputStream() {
        return new DefaultDataBufferOutputStream();
    }

    private void ensureCapacity(int length) {
        if (length <= writableByteCount()) {
            return;
        }
        int newCapacity = calculateCapacity(this.writePosition + length);
        capacity(newCapacity);
    }

    private int calculateCapacity(int neededCapacity) {
        int newCapacity;
        Assert.isTrue(neededCapacity >= 0, "'neededCapacity' must >= 0");
        if (neededCapacity == 4194304) {
            return 4194304;
        }
        if (neededCapacity > 4194304) {
            int newCapacity2 = (neededCapacity / 4194304) * 4194304;
            if (newCapacity2 > 2143289343) {
                newCapacity = Integer.MAX_VALUE;
            } else {
                newCapacity = newCapacity2 + 4194304;
            }
            return newCapacity;
        }
        int i = 64;
        while (true) {
            int newCapacity3 = i;
            if (newCapacity3 < neededCapacity) {
                i = newCapacity3 << 1;
            } else {
                return Math.min(newCapacity3, Integer.MAX_VALUE);
            }
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultDataBuffer)) {
            return false;
        }
        DefaultDataBuffer otherBuffer = (DefaultDataBuffer) other;
        return this.readPosition == otherBuffer.readPosition && this.writePosition == otherBuffer.writePosition && this.byteBuffer.equals(otherBuffer.byteBuffer);
    }

    public int hashCode() {
        return this.byteBuffer.hashCode();
    }

    public String toString() {
        return String.format("DefaultDataBuffer (r: %d, w: %d, c: %d)", Integer.valueOf(this.readPosition), Integer.valueOf(this.writePosition), Integer.valueOf(this.capacity));
    }

    private void checkIndex(int index, int length) {
        assertIndex(index >= 0, "index %d must be >= 0", Integer.valueOf(index));
        assertIndex(length >= 0, "length %d must be >= 0", Integer.valueOf(index));
        assertIndex(index <= this.capacity, "index %d must be <= %d", Integer.valueOf(index), Integer.valueOf(this.capacity));
        assertIndex(length <= this.capacity, "length %d must be <= %d", Integer.valueOf(index), Integer.valueOf(this.capacity));
    }

    private static void assertIndex(boolean expression, String format, Object... args) {
        if (!expression) {
            String message = String.format(format, args);
            throw new IndexOutOfBoundsException(message);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DefaultDataBuffer$DefaultDataBufferInputStream.class */
    private class DefaultDataBufferInputStream extends InputStream {
        private DefaultDataBufferInputStream() {
            DefaultDataBuffer.this = r4;
        }

        @Override // java.io.InputStream
        public int available() {
            return DefaultDataBuffer.this.readableByteCount();
        }

        @Override // java.io.InputStream
        public int read() {
            if (available() > 0) {
                return DefaultDataBuffer.this.read() & 255;
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read(byte[] bytes, int off, int len) throws IOException {
            int available = available();
            if (available > 0) {
                int len2 = Math.min(len, available);
                DefaultDataBuffer.this.read(bytes, off, len2);
                return len2;
            }
            return -1;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DefaultDataBuffer$DefaultDataBufferOutputStream.class */
    private class DefaultDataBufferOutputStream extends OutputStream {
        private DefaultDataBufferOutputStream() {
            DefaultDataBuffer.this = r4;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            DefaultDataBuffer.this.write((byte) b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            DefaultDataBuffer.this.write(bytes, off, len);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DefaultDataBuffer$SlicedDefaultDataBuffer.class */
    public static class SlicedDefaultDataBuffer extends DefaultDataBuffer {
        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer slice(int i, int i2) {
            return super.slice(i, i2);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(ByteBuffer[] byteBufferArr) {
            return super.write(byteBufferArr);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(DataBuffer[] dataBufferArr) {
            return super.write(dataBufferArr);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte[] bArr, int i, int i2) {
            return super.write(bArr, i, i2);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte[] bArr) {
            return super.write(bArr);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer write(byte b) {
            return super.write(b);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer read(byte[] bArr, int i, int i2) {
            return super.read(bArr, i, i2);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer read(byte[] bArr) {
            return super.read(bArr);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer writePosition(int i) {
            return super.writePosition(i);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBuffer readPosition(int i) {
            return super.readPosition(i);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public /* bridge */ /* synthetic */ DataBufferFactory factory() {
            return super.factory();
        }

        SlicedDefaultDataBuffer(ByteBuffer byteBuffer, DefaultDataBufferFactory dataBufferFactory, int length) {
            super(dataBufferFactory, byteBuffer);
            writePosition(length);
        }

        @Override // org.springframework.core.io.buffer.DefaultDataBuffer, org.springframework.core.io.buffer.DataBuffer
        public DefaultDataBuffer capacity(int newCapacity) {
            throw new UnsupportedOperationException("Changing the capacity of a sliced buffer is not supported");
        }
    }
}