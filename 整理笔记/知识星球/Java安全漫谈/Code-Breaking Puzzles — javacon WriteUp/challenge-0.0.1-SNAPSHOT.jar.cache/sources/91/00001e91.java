package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/NettyDataBufferFactory.class */
public class NettyDataBufferFactory implements DataBufferFactory {
    private final ByteBufAllocator byteBufAllocator;

    public NettyDataBufferFactory(ByteBufAllocator byteBufAllocator) {
        Assert.notNull(byteBufAllocator, "'byteBufAllocator' must not be null");
        this.byteBufAllocator = byteBufAllocator;
    }

    public ByteBufAllocator getByteBufAllocator() {
        return this.byteBufAllocator;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public NettyDataBuffer allocateBuffer() {
        ByteBuf byteBuf = this.byteBufAllocator.buffer();
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public NettyDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuf byteBuf = this.byteBufAllocator.buffer(initialCapacity);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public NettyDataBuffer wrap(ByteBuffer byteBuffer) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer wrap(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notNull(dataBuffers, "'dataBuffers' must not be null");
        CompositeByteBuf composite = this.byteBufAllocator.compositeBuffer(dataBuffers.size());
        for (DataBuffer dataBuffer : dataBuffers) {
            Assert.isInstanceOf(NettyDataBuffer.class, dataBuffer);
            composite.addComponent(true, ((NettyDataBuffer) dataBuffer).getNativeBuffer());
        }
        return new NettyDataBuffer(composite, this);
    }

    public NettyDataBuffer wrap(ByteBuf byteBuf) {
        return new NettyDataBuffer(byteBuf, this);
    }

    public static ByteBuf toByteBuf(DataBuffer buffer) {
        if (buffer instanceof NettyDataBuffer) {
            return ((NettyDataBuffer) buffer).getNativeBuffer();
        }
        return Unpooled.wrappedBuffer(buffer.asByteBuffer());
    }

    public String toString() {
        return "NettyDataBufferFactory (" + this.byteBufAllocator + ")";
    }
}