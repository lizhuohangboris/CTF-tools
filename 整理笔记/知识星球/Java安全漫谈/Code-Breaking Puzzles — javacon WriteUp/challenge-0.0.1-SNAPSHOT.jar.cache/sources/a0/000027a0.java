package org.thymeleaf.cache;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/ICacheEntryValidityChecker.class */
public interface ICacheEntryValidityChecker<K, V> extends Serializable {
    boolean checkIsValueStillValid(K k, V v, long j);
}