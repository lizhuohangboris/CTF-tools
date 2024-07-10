package org.springframework.boot.autoconfigure.cache;

import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/JCachePropertiesCustomizer.class */
interface JCachePropertiesCustomizer {
    void customize(CacheProperties cacheProperties, Properties properties);
}