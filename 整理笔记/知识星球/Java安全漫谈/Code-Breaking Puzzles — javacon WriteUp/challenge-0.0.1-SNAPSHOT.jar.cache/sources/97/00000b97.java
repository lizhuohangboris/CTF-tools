package org.apache.logging.log4j.util;

import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/SystemPropertiesPropertySource.class */
public class SystemPropertiesPropertySource implements PropertySource {
    private static final int DEFAULT_PRIORITY = 100;
    private static final String PREFIX = "log4j2.";

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 100;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(BiConsumer<String, String> action) {
        Object[] keySet;
        try {
            Properties properties = System.getProperties();
            synchronized (properties) {
                keySet = properties.keySet().toArray();
            }
            for (Object key : keySet) {
                String keyStr = Objects.toString(key, null);
                action.accept(keyStr, properties.getProperty(keyStr));
            }
        } catch (SecurityException e) {
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
        return PREFIX + ((Object) PropertySource.Util.joinAsCamelCase(tokens));
    }
}