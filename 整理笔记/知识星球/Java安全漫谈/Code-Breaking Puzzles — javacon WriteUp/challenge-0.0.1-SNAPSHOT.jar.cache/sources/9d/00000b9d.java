package org.apache.logging.slf4j;

import java.util.Map;
import org.apache.logging.log4j.spi.CleanableThreadContextMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.slf4j.MDC;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-to-slf4j-2.11.1.jar:org/apache/logging/slf4j/MDCContextMap.class */
public class MDCContextMap implements CleanableThreadContextMap {
    private static final StringMap EMPTY_CONTEXT_DATA = new SortedArrayStringMap(1);

    static {
        EMPTY_CONTEXT_DATA.freeze();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void put(String key, String value) {
        MDC.put(key, value);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap2
    public void putAll(Map<String, String> m) {
        for (Map.Entry<String, String> entry : m.entrySet()) {
            MDC.put(entry.getKey(), entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public String get(String key) {
        return MDC.get(key);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override // org.apache.logging.log4j.spi.CleanableThreadContextMap
    public void removeAll(Iterable<String> keys) {
        for (String key : keys) {
            MDC.remove(key);
        }
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public void clear() {
        MDC.clear();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean containsKey(String key) {
        return MDC.getCopyOfContextMap().containsKey(key);
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getCopy() {
        return MDC.getCopyOfContextMap();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public Map<String, String> getImmutableMapOrNull() {
        return MDC.getCopyOfContextMap();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap
    public boolean isEmpty() {
        return MDC.getCopyOfContextMap().isEmpty();
    }

    @Override // org.apache.logging.log4j.spi.ThreadContextMap2
    public StringMap getReadOnlyContextData() {
        Map<String, String> copy = getCopy();
        if (copy.isEmpty()) {
            return EMPTY_CONTEXT_DATA;
        }
        StringMap result = new SortedArrayStringMap();
        for (Map.Entry<String, String> entry : copy.entrySet()) {
            result.putValue(entry.getKey(), entry.getValue());
        }
        return result;
    }
}