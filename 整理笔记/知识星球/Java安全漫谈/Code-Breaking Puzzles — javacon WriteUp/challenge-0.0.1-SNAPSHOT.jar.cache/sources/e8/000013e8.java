package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/BeanDefinition.class */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";
    public static final int ROLE_APPLICATION = 0;
    public static final int ROLE_SUPPORT = 1;
    public static final int ROLE_INFRASTRUCTURE = 2;

    void setParentName(@Nullable String str);

    @Nullable
    String getParentName();

    void setBeanClassName(@Nullable String str);

    @Nullable
    String getBeanClassName();

    void setScope(@Nullable String str);

    @Nullable
    String getScope();

    void setLazyInit(boolean z);

    boolean isLazyInit();

    void setDependsOn(@Nullable String... strArr);

    @Nullable
    String[] getDependsOn();

    void setAutowireCandidate(boolean z);

    boolean isAutowireCandidate();

    void setPrimary(boolean z);

    boolean isPrimary();

    void setFactoryBeanName(@Nullable String str);

    @Nullable
    String getFactoryBeanName();

    void setFactoryMethodName(@Nullable String str);

    @Nullable
    String getFactoryMethodName();

    ConstructorArgumentValues getConstructorArgumentValues();

    MutablePropertyValues getPropertyValues();

    void setInitMethodName(@Nullable String str);

    @Nullable
    String getInitMethodName();

    void setDestroyMethodName(@Nullable String str);

    @Nullable
    String getDestroyMethodName();

    void setRole(int i);

    int getRole();

    void setDescription(@Nullable String str);

    @Nullable
    String getDescription();

    boolean isSingleton();

    boolean isPrototype();

    boolean isAbstract();

    @Nullable
    String getResourceDescription();

    @Nullable
    BeanDefinition getOriginatingBeanDefinition();

    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }
}