package org.apache.commons.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-jcl-5.1.2.RELEASE.jar:org/apache/commons/logging/LogFactoryService.class */
public class LogFactoryService extends LogFactory {
    private final Map<String, Object> attributes = new ConcurrentHashMap();

    @Override // org.apache.commons.logging.LogFactory
    public Log getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    @Override // org.apache.commons.logging.LogFactory
    public Log getInstance(String name) {
        return LogAdapter.createLog(name);
    }

    public void setAttribute(String name, Object value) {
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public String[] getAttributeNames() {
        return (String[]) this.attributes.keySet().toArray(new String[0]);
    }

    public void release() {
    }
}