package ch.qos.logback.core.joran.spi;

import java.util.HashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/DefaultNestedComponentRegistry.class */
public class DefaultNestedComponentRegistry {
    Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap();

    public void add(Class<?> hostClass, String propertyName, Class<?> componentClass) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
        this.defaultComponentMap.put(hpDouble, componentClass);
    }

    public Class<?> findDefaultComponentType(Class<?> hostClass, String propertyName) {
        String propertyName2 = propertyName.toLowerCase();
        while (hostClass != null) {
            Class<?> componentClass = oneShotFind(hostClass, propertyName2);
            if (componentClass != null) {
                return componentClass;
            }
            hostClass = hostClass.getSuperclass();
        }
        return null;
    }

    private Class<?> oneShotFind(Class<?> hostClass, String propertyName) {
        HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
        return this.defaultComponentMap.get(hpDouble);
    }
}