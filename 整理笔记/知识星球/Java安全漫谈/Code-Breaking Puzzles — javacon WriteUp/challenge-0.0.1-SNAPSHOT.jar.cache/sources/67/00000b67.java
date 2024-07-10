package org.apache.logging.log4j.spi;

import java.util.Map;
import org.apache.logging.log4j.util.StringMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ThreadContextMap2.class */
public interface ThreadContextMap2 extends ThreadContextMap {
    void putAll(Map<String, String> map);

    StringMap getReadOnlyContextData();
}