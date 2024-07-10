package org.apache.tomcat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/InstanceManagerBindings.class */
public final class InstanceManagerBindings {
    private static final Map<ClassLoader, InstanceManager> bindings = new ConcurrentHashMap();

    public static final void bind(ClassLoader classLoader, InstanceManager instanceManager) {
        bindings.put(classLoader, instanceManager);
    }

    public static final void unbind(ClassLoader classLoader) {
        bindings.remove(classLoader);
    }

    public static final InstanceManager get(ClassLoader classLoader) {
        return bindings.get(classLoader);
    }
}