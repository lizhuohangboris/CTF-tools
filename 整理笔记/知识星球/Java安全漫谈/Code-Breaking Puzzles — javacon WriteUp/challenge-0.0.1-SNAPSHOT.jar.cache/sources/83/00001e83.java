package org.springframework.core.io.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.IntPredicate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/buffer/DataBuffer.class */
public interface DataBuffer {
    DataBufferFactory factory();

    int indexOf(IntPredicate intPredicate, int i);

    int lastIndexOf(IntPredicate intPredicate, int i);

    int readableByteCount();

    int writableByteCount();

    int capacity();

    DataBuffer capacity(int i);

    int readPosition();

    DataBuffer readPosition(int i);

    int writePosition();

    DataBuffer writePosition(int i);

    byte getByte(int i);

    byte read();

    DataBuffer read(byte[] bArr);

    DataBuffer read(byte[] bArr, int i, int i2);

    DataBuffer write(byte b);

    DataBuffer write(byte[] bArr);

    DataBuffer write(byte[] bArr, int i, int i2);

    DataBuffer write(DataBuffer... dataBufferArr);

    DataBuffer write(ByteBuffer... byteBufferArr);

    DataBuffer slice(int i, int i2);

    ByteBuffer asByteBuffer();

    ByteBuffer asByteBuffer(int i, int i2);

    InputStream asInputStream();

    InputStream asInputStream(boolean z);

    OutputStream asOutputStream();
}