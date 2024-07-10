package com.fasterxml.jackson.core.async;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/async/ByteArrayFeeder.class */
public interface ByteArrayFeeder extends NonBlockingInputFeeder {
    void feedInput(byte[] bArr, int i, int i2) throws IOException;
}