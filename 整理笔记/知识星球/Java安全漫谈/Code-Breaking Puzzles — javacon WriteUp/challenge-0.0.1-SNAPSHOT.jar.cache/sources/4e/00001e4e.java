package org.springframework.core.env;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ConfigurableEnvironment.class */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
    void setActiveProfiles(String... strArr);

    void addActiveProfile(String str);

    void setDefaultProfiles(String... strArr);

    MutablePropertySources getPropertySources();

    Map<String, Object> getSystemProperties();

    Map<String, Object> getSystemEnvironment();

    void merge(ConfigurableEnvironment configurableEnvironment);
}