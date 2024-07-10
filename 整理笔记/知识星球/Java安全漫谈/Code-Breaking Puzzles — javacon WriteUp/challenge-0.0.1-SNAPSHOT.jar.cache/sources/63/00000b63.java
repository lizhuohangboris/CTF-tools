package org.apache.logging.log4j.spi;

import java.util.Map;
import org.apache.logging.log4j.util.StringMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ReadOnlyThreadContextMap.class */
public interface ReadOnlyThreadContextMap {
    void clear();

    boolean containsKey(String str);

    String get(String str);

    Map<String, String> getCopy();

    Map<String, String> getImmutableMapOrNull();

    StringMap getReadOnlyContextData();

    boolean isEmpty();
}