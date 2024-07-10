package org.thymeleaf.cache;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/ICache.class */
public interface ICache<K, V> {
    void put(K k, V v);

    V get(K k);

    V get(K k, ICacheEntryValidityChecker<? super K, ? super V> iCacheEntryValidityChecker);

    void clear();

    void clearKey(K k);

    Set<K> keySet();
}