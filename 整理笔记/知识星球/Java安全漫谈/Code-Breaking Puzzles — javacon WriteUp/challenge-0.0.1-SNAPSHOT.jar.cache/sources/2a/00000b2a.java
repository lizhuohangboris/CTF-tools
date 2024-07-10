package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ParameterConsumer.class */
public interface ParameterConsumer<S> {
    void accept(Object obj, int i, S s);
}