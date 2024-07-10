package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertiesPropertySource.class */
public class PropertiesPropertySource extends MapPropertySource {
    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    public PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }
}