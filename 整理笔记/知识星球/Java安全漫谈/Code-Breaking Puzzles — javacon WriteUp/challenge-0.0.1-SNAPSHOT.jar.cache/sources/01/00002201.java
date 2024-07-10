package org.springframework.jmx.support;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/support/ObjectNameManager.class */
public final class ObjectNameManager {
    private ObjectNameManager() {
    }

    public static ObjectName getInstance(Object objectName) throws MalformedObjectNameException {
        if (objectName instanceof ObjectName) {
            return (ObjectName) objectName;
        }
        if (!(objectName instanceof String)) {
            throw new MalformedObjectNameException("Invalid ObjectName value type [" + objectName.getClass().getName() + "]: only ObjectName and String supported.");
        }
        return getInstance((String) objectName);
    }

    public static ObjectName getInstance(String objectName) throws MalformedObjectNameException {
        return ObjectName.getInstance(objectName);
    }

    public static ObjectName getInstance(String domainName, String key, String value) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, key, value);
    }

    public static ObjectName getInstance(String domainName, Hashtable<String, String> properties) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, properties);
    }
}