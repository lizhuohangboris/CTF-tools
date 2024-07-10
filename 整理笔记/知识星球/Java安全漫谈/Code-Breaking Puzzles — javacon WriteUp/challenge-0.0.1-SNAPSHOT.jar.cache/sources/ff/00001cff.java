package org.springframework.context.annotation;

import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ScopeMetadata.class */
public class ScopeMetadata {
    private String scopeName = "singleton";
    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

    public void setScopeName(String scopeName) {
        Assert.notNull(scopeName, "'scopeName' must not be null");
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return this.scopeName;
    }

    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        Assert.notNull(scopedProxyMode, "'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }

    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }
}