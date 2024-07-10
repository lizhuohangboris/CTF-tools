package org.springframework.core.env;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/StandardEnvironment.class */
public class StandardEnvironment extends AbstractEnvironment {
    public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";
    public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.core.env.AbstractEnvironment
    public void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.addLast(new MapPropertySource("systemProperties", getSystemProperties()));
        propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", getSystemEnvironment()));
    }
}