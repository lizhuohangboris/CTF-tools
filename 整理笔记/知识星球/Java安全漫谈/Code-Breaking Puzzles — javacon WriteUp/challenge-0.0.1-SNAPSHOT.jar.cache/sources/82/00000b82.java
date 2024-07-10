package org.apache.logging.log4j.util;

import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/PropertiesPropertySource.class */
public class PropertiesPropertySource implements PropertySource {
    private static final String PREFIX = "log4j2.";
    private final Properties properties;

    public PropertiesPropertySource(Properties properties) {
        this.properties = properties;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public int getPriority() {
        return 0;
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public void forEach(BiConsumer<String, String> action) {
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            action.accept((String) entry.getKey(), (String) entry.getValue());
        }
    }

    @Override // org.apache.logging.log4j.util.PropertySource
    public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
        return PREFIX + ((Object) PropertySource.Util.joinAsCamelCase(tokens));
    }
}