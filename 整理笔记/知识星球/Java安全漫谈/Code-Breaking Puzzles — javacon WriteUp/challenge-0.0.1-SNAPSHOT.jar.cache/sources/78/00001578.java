package org.springframework.boot.autoconfigure.cache;

import org.springframework.cache.CacheManager;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheManagerCustomizer.class */
public interface CacheManagerCustomizer<T extends CacheManager> {
    void customize(T cacheManager);
}