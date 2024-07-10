package org.thymeleaf.cache;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/ICacheEntryValidity.class */
public interface ICacheEntryValidity {
    boolean isCacheable();

    boolean isCacheStillValid();
}