package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DefaultDataBufferFactory.class */
public class DefaultDataBufferFactory implements DataBufferFactory {
    public static final int DEFAULT_INITIAL_CAPACITY = 256;
    private final boolean preferDirect;
    private final int defaultInitialCapacity;

    public DefaultDataBufferFactory() {
        this(false);
    }

    public DefaultDataBufferFactory(boolean preferDirect) {
        this(preferDirect, 256);
    }

    public DefaultDataBufferFactory(boolean preferDirect, int defaultInitialCapacity) {
        Assert.isTrue(defaultInitialCapacity > 0, "'defaultInitialCapacity' should be larger than 0");
        this.preferDirect = preferDirect;
        this.defaultInitialCapacity = defaultInitialCapacity;
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer allocateBuffer() {
        return allocateBuffer(this.defaultInitialCapacity);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuffer allocate;
        if (this.preferDirect) {
            allocate = ByteBuffer.allocateDirect(initialCapacity);
        } else {
            allocate = ByteBuffer.allocate(initialCapacity);
        }
        ByteBuffer byteBuffer = allocate;
        return DefaultDataBuffer.fromEmptyByteBuffer(this, byteBuffer);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DefaultDataBuffer wrap(ByteBuffer byteBuffer) {
        ByteBuffer sliced = byteBuffer.slice();
        return DefaultDataBuffer.fromFilledByteBuffer(this, sliced);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer wrap(byte[] bytes) {
        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        return DefaultDataBuffer.fromFilledByteBuffer(this, wrapper);
    }

    @Override // org.springframework.core.io.buffer.DataBufferFactory
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notEmpty(dataBuffers, "'dataBuffers' must not be empty");
        int capacity = dataBuffers.stream().mapToInt((v0) -> {
            return v0.readableByteCount();
        }).sum();
        DefaultDataBuffer dataBuffer = allocateBuffer(capacity);
        DataBuffer result = (DataBuffer) dataBuffers.stream().map(o -> {
            return o;
        }).reduce(dataBuffer, rec$, xva$0 -> {
            return ((DataBuffer) rec$).write(xva$0);
        });
        dataBuffers.forEach(DataBufferUtils::release);
        return result;
    }

    public String toString() {
        return "DefaultDataBufferFactory (preferDirect=" + this.preferDirect + ")";
    }
}