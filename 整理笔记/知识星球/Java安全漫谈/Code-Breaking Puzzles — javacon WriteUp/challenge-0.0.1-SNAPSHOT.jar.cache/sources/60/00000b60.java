package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/NoOpThreadContextMap.class */
public class NoOpThreadContextMap implements ThreadContextMap {
    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(String key) {
        return false;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public String get(String key) {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        return new HashMap();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        return true;
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(String key, String value) {
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(String key) {
    }
}