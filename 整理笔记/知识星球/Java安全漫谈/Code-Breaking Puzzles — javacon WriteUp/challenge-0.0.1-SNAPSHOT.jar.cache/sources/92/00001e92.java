package org.springframework.core.io.buffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/PooledDataBuffer.class */
public interface PooledDataBuffer extends DataBuffer {
    boolean isAllocated();

    PooledDataBuffer retain();

    boolean release();
}