package org.apache.logging.log4j.spi;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ObjectThreadContextMap.class */
public interface ObjectThreadContextMap extends CleanableThreadContextMap {
    <V> V getValue(String str);

    <V> void putValue(String str, V v);

    <V> void putAllValues(Map<String, V> map);
}