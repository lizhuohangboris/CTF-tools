package org.springframework.beans.factory.support;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionDefaults.class */
public class BeanDefinitionDefaults {
    private boolean lazyInit;
    private int dependencyCheck = 0;
    private int autowireMode = 0;
    @Nullable
    private String initMethodName;
    @Nullable
    private String destroyMethodName;

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodName = StringUtils.hasText(initMethodName) ? initMethodName : null;
    }

    @Nullable
    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodName = StringUtils.hasText(destroyMethodName) ? destroyMethodName : null;
    }

    @Nullable
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }
}