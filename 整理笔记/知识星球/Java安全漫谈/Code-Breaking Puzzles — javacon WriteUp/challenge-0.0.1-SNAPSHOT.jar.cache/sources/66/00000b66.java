package org.apache.logging.log4j.spi;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ThreadContextMap.class */
public interface ThreadContextMap {
    void clear();

    boolean containsKey(String str);

    String get(String str);

    Map<String, String> getCopy();

    Map<String, String> getImmutableMapOrNull();

    boolean isEmpty();

    void put(String str, String str2);

    void remove(String str);
}