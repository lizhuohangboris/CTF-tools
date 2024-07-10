package com.fasterxml.jackson.core.async;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/async/NonBlockingInputFeeder.class */
public interface NonBlockingInputFeeder {
    boolean needMoreInput();

    void endOfInput();
}