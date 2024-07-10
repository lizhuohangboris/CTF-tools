package org.springframework.util;

import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/MultiValueMap.class */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    @Nullable
    V getFirst(K k);

    void add(K k, @Nullable V v);

    void addAll(K k, List<? extends V> list);

    void addAll(MultiValueMap<K, V> multiValueMap);

    void set(K k, @Nullable V v);

    void setAll(Map<K, V> map);

    Map<K, V> toSingleValueMap();
}