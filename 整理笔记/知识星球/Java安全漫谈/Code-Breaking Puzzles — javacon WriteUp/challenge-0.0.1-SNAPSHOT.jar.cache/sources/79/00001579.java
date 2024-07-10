package org.springframework.boot.autoconfigure.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.cache.CacheManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CacheManagerCustomizers.class */
public class CacheManagerCustomizers {
    private final List<CacheManagerCustomizer<?>> customizers;

    public CacheManagerCustomizers(List<? extends CacheManagerCustomizer<?>> customizers) {
        this.customizers = customizers != null ? new ArrayList<>(customizers) : Collections.emptyList();
    }

    public <T extends CacheManager> T customize(T cacheManager) {
        LambdaSafe.callbacks(CacheManagerCustomizer.class, this.customizers, cacheManager, new Object[0]).withLogger(CacheManagerCustomizers.class).invoke(customizer -> {
            customizer.customize(cacheManager);
        });
        return cacheManager;
    }
}