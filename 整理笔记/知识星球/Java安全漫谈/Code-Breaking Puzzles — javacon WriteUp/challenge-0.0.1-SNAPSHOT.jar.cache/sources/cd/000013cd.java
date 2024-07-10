package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/AnnotatedGenericBeanDefinition.class */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
    private final AnnotationMetadata metadata;
    @Nullable
    private MethodMetadata factoryMethodMetadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = new StandardAnnotationMetadata(beanClass, true);
    }

    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata) {
        Assert.notNull(metadata, "AnnotationMetadata must not be null");
        if (metadata instanceof StandardAnnotationMetadata) {
            setBeanClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
        } else {
            setBeanClassName(metadata.getClassName());
        }
        this.metadata = metadata;
    }

    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata, MethodMetadata factoryMethodMetadata) {
        this(metadata);
        Assert.notNull(factoryMethodMetadata, "MethodMetadata must not be null");
        setFactoryMethodName(factoryMethodMetadata.getMethodName());
        this.factoryMethodMetadata = factoryMethodMetadata;
    }

    @Override // org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override // org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
    @Nullable
    public final MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }
}