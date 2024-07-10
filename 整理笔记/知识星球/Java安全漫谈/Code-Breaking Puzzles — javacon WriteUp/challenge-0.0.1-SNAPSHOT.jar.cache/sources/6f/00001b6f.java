package org.springframework.cache.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/interceptor/NameMatchCacheOperationSource.class */
public class NameMatchCacheOperationSource implements CacheOperationSource, Serializable {
    protected static final Log logger = LogFactory.getLog(NameMatchCacheOperationSource.class);
    private Map<String, Collection<CacheOperation>> nameMap = new LinkedHashMap();

    public void setNameMap(Map<String, Collection<CacheOperation>> nameMap) {
        nameMap.forEach(this::addCacheMethod);
    }

    public void addCacheMethod(String methodName, Collection<CacheOperation> ops) {
        if (logger.isDebugEnabled()) {
            logger.debug("Adding method [" + methodName + "] with cache operations [" + ops + "]");
        }
        this.nameMap.put(methodName, ops);
    }

    @Override // org.springframework.cache.interceptor.CacheOperationSource
    @Nullable
    public Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass) {
        String methodName = method.getName();
        Collection<CacheOperation> ops = this.nameMap.get(methodName);
        if (ops == null) {
            String bestNameMatch = null;
            for (String mappedName : this.nameMap.keySet()) {
                if (isMatch(methodName, mappedName) && (bestNameMatch == null || bestNameMatch.length() <= mappedName.length())) {
                    ops = this.nameMap.get(mappedName);
                    bestNameMatch = mappedName;
                }
            }
        }
        return ops;
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameMatchCacheOperationSource)) {
            return false;
        }
        NameMatchCacheOperationSource otherTas = (NameMatchCacheOperationSource) other;
        return ObjectUtils.nullSafeEquals(this.nameMap, otherTas.nameMap);
    }

    public int hashCode() {
        return NameMatchCacheOperationSource.class.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.nameMap;
    }
}