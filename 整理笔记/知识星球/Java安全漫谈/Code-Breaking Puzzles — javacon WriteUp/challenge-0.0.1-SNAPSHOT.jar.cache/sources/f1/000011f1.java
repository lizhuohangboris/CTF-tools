package org.jboss.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/AbstractMdcLoggerProvider.class */
abstract class AbstractMdcLoggerProvider extends AbstractLoggerProvider {
    private final ThreadLocal<Map<String, Object>> mdcMap = new ThreadLocal<>();

    public void clearMdc() {
        Map<String, Object> map = this.mdcMap.get();
        if (map != null) {
            map.clear();
        }
    }

    public Object getMdc(String key) {
        if (this.mdcMap.get() == null) {
            return null;
        }
        return this.mdcMap.get().get(key);
    }

    public Map<String, Object> getMdcMap() {
        Map<String, Object> map = this.mdcMap.get();
        return map == null ? Collections.emptyMap() : map;
    }

    public Object putMdc(String key, Object value) {
        Map<String, Object> map = this.mdcMap.get();
        if (map == null) {
            map = new HashMap<>();
            this.mdcMap.set(map);
        }
        return map.put(key, value);
    }

    public void removeMdc(String key) {
        Map<String, Object> map = this.mdcMap.get();
        if (map == null) {
            return;
        }
        map.remove(key);
    }
}