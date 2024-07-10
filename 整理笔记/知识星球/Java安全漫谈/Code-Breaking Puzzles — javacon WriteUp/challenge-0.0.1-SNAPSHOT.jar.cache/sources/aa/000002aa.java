package com.fasterxml.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/async/ByteBufferFeeder.class */
public interface ByteBufferFeeder extends NonBlockingInputFeeder {
    void feedInput(ByteBuffer byteBuffer) throws IOException;
}