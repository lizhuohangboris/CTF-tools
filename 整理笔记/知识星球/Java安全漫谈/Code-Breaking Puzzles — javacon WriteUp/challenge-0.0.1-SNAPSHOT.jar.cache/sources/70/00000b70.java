package org.apache.logging.log4j.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/BiConsumer.class */
public interface BiConsumer<K, V> {
    void accept(K k, V v);
}