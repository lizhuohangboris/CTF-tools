package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/CacheOperationSourcePointcut.class */
abstract class CacheOperationSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {
    @Nullable
    protected abstract CacheOperationSource getCacheOperationSource();

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        CacheOperationSource cas;
        return (CacheManager.class.isAssignableFrom(targetClass) || (cas = getCacheOperationSource()) == null || CollectionUtils.isEmpty(cas.getCacheOperations(method, targetClass))) ? false : true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheOperationSourcePointcut)) {
            return false;
        }
        CacheOperationSourcePointcut otherPc = (CacheOperationSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getCacheOperationSource(), otherPc.getCacheOperationSource());
    }

    public int hashCode() {
        return CacheOperationSourcePointcut.class.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + getCacheOperationSource();
    }
}