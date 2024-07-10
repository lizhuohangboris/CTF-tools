package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.IntPredicate;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/NettyDataBuffer.class */
public class NettyDataBuffer implements PooledDataBuffer {
    private final ByteBuf byteBuf;
    private final NettyDataBufferFactory dataBufferFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NettyDataBuffer(ByteBuf byteBuf, NettyDataBufferFactory dataBufferFactory) {
        Assert.notNull(byteBuf, "ByteBuf must not be null");
        Assert.notNull(dataBufferFactory, "NettyDataBufferFactory must not be null");
        this.byteBuf = byteBuf;
        this.dataBufferFactory = dataBufferFactory;
    }

    public ByteBuf getNativeBuffer() {
        return this.byteBuf;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBufferFactory factory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int indexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "'predicate' must not be null");
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.byteBuf.writerIndex()) {
            return -1;
        }
        int length = this.byteBuf.writerIndex() - fromIndex;
        IntPredicate negate = predicate.negate();
        negate.getClass();
        return this.byteBuf.forEachByte(fromIndex, length, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int lastIndexOf(IntPredicate predicate, int fromIndex) {
        Assert.notNull(predicate, "'predicate' must not be null");
        if (fromIndex < 0) {
            return -1;
        }
        int fromIndex2 = Math.min(fromIndex, this.byteBuf.writerIndex() - 1);
        IntPredicate negate = predicate.negate();
        negate.getClass();
        return this.byteBuf.forEachByteDesc(0, fromIndex2 + 1, (v1) -> {
            return r3.test(v1);
        });
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readableByteCount() {
        return this.byteBuf.readableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writableByteCount() {
        return this.byteBuf.writableBytes();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int readPosition() {
        return this.byteBuf.readerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer readPosition(int readPosition) {
        this.byteBuf.readerIndex(readPosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int writePosition() {
        return this.byteBuf.writerIndex();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer writePosition(int writePosition) {
        this.byteBuf.writerIndex(writePosition);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte getByte(int index) {
        return this.byteBuf.getByte(index);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public int capacity() {
        return this.byteBuf.capacity();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer capacity(int capacity) {
        this.byteBuf.capacity(capacity);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public byte read() {
        return this.byteBuf.readByte();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination) {
        this.byteBuf.readBytes(destination);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer read(byte[] destination, int offset, int length) {
        this.byteBuf.readBytes(destination, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte b) {
        this.byteBuf.writeByte(b);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source) {
        this.byteBuf.writeBytes(source);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(byte[] source, int offset, int length) {
        this.byteBuf.writeBytes(source, offset, length);
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(DataBuffer... buffers) {
        Assert.notNull(buffers, "'buffers' must not be null");
        if (buffers.length > 0) {
            if (hasNettyDataBuffers(buffers)) {
                ByteBuf[] nativeBuffers = (ByteBuf[]) Arrays.stream(buffers).map(b -> {
                    return ((NettyDataBuffer) b).getNativeBuffer();
                }).toArray(x$0 -> {
                    return new ByteBuf[x$0];
                });
                write(nativeBuffers);
            } else {
                ByteBuffer[] byteBuffers = (ByteBuffer[]) Arrays.stream(buffers).map((v0) -> {
                    return v0.asByteBuffer();
                }).toArray(x$02 -> {
                    return new ByteBuffer[x$02];
                });
                write(byteBuffers);
            }
        }
        return this;
    }

    private static boolean hasNettyDataBuffers(DataBuffer[] dataBuffers) {
        for (DataBuffer dataBuffer : dataBuffers) {
            if (!(dataBuffer instanceof NettyDataBuffer)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer write(ByteBuffer... buffers) {
        Assert.notNull(buffers, "'buffers' must not be null");
        for (ByteBuffer buffer : buffers) {
            this.byteBuf.writeBytes(buffer);
        }
        return this;
    }

    public NettyDataBuffer write(ByteBuf... byteBufs) {
        Assert.notNull(byteBufs, "'byteBufs' must not be null");
        for (ByteBuf byteBuf : byteBufs) {
            this.byteBuf.writeBytes(byteBuf);
        }
        return this;
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public NettyDataBuffer slice(int index, int length) {
        ByteBuf slice = this.byteBuf.slice(index, length);
        return new NettyDataBuffer(slice, this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer() {
        return this.byteBuf.nioBuffer();
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public ByteBuffer asByteBuffer(int index, int length) {
        return this.byteBuf.nioBuffer(index, length);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream() {
        return new ByteBufInputStream(this.byteBuf);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public InputStream asInputStream(boolean releaseOnClose) {
        return new ByteBufInputStream(this.byteBuf, releaseOnClose);
    }

    @Override // org.springframework.core.io.buffer.DataBuffer
    public OutputStream asOutputStream() {
        return new ByteBufOutputStream(this.byteBuf);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean isAllocated() {
        return this.byteBuf.refCnt() > 0;
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public PooledDataBuffer retain() {
        return new NettyDataBuffer(this.byteBuf.retain(), this.dataBufferFactory);
    }

    @Override // org.springframework.core.io.buffer.PooledDataBuffer
    public boolean release() {
        return this.byteBuf.release();
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof NettyDataBuffer) && this.byteBuf.equals(((NettyDataBuffer) other).byteBuf));
    }

    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    public String toString() {
        return this.byteBuf.toString();
    }
}