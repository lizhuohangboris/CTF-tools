package org.springframework.core.io.buffer;

import java.nio.ByteBuffer;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBufferFactory.class */
public interface DataBufferFactory {
    DataBuffer allocateBuffer();

    DataBuffer allocateBuffer(int i);

    DataBuffer wrap(ByteBuffer byteBuffer);

    DataBuffer wrap(byte[] bArr);

    DataBuffer join(List<? extends DataBuffer> list);
}