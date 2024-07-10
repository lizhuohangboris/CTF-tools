package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/RequiredAnnotationBeanPostProcessor.class */
public class RequiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {
    public static final String SKIP_REQUIRED_CHECK_ATTRIBUTE = Conventions.getQualifiedAttributeName(RequiredAnnotationBeanPostProcessor.class, "skipRequiredCheck");
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;
    private Class<? extends Annotation> requiredAnnotationType = Required.class;
    private int order = 2147483646;
    private final Set<String> validatedBeanNames = Collections.newSetFromMap(new ConcurrentHashMap(64));

    public void setRequiredAnnotationType(Class<? extends Annotation> requiredAnnotationType) {
        Assert.notNull(requiredAnnotationType, "'requiredAnnotationType' must not be null");
        this.requiredAnnotationType = requiredAnnotationType;
    }

    protected Class<? extends Annotation> getRequiredAnnotationType() {
        return this.requiredAnnotationType;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter, org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
        if (!this.validatedBeanNames.contains(beanName)) {
            if (!shouldSkip(this.beanFactory, beanName)) {
                List<String> invalidProperties = new ArrayList<>();
                for (PropertyDescriptor pd : pds) {
                    if (isRequiredProperty(pd) && !pvs.contains(pd.getName())) {
                        invalidProperties.add(pd.getName());
                    }
                }
                if (!invalidProperties.isEmpty()) {
                    throw new BeanInitializationException(buildExceptionMessage(invalidProperties, beanName));
                }
            }
            this.validatedBeanNames.add(beanName);
        }
        return pvs;
    }

    protected boolean shouldSkip(@Nullable ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        if (beanDefinition.getFactoryBeanName() != null) {
            return true;
        }
        Object value = beanDefinition.getAttribute(SKIP_REQUIRED_CHECK_ATTRIBUTE);
        return value != null && (Boolean.TRUE.equals(value) || Boolean.valueOf(value.toString()).booleanValue());
    }

    protected boolean isRequiredProperty(PropertyDescriptor propertyDescriptor) {
        Method setter = propertyDescriptor.getWriteMethod();
        return (setter == null || AnnotationUtils.getAnnotation(setter, (Class<Annotation>) getRequiredAnnotationType()) == null) ? false : true;
    }

    private String buildExceptionMessage(List<String> invalidProperties, String beanName) {
        int size = invalidProperties.size();
        StringBuilder sb = new StringBuilder();
        sb.append(size == 1 ? "Property" : "Properties");
        for (int i = 0; i < size; i++) {
            String propertyName = invalidProperties.get(i);
            if (i > 0) {
                if (i == size - 1) {
                    sb.append(" and");
                } else {
                    sb.append(",");
                }
            }
            sb.append(" '").append(propertyName).append("'");
        }
        sb.append(size == 1 ? " is" : " are");
        sb.append(" required for bean '").append(beanName).append("'");
        return sb.toString();
    }
}