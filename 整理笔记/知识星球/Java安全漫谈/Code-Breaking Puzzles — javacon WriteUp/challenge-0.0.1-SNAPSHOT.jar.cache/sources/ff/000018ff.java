package org.springframework.boot.context.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.annotation.Validated;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBindingPostProcessor.class */
public class ConfigurationPropertiesBindingPostProcessor implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean {
    public static final String BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class.getName();
    public static final String VALIDATOR_BEAN_NAME = "configurationPropertiesValidator";
    private ConfigurationBeanFactoryMetadata beanFactoryMetadata;
    private ApplicationContext applicationContext;
    private ConfigurationPropertiesBinder configurationPropertiesBinder;

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        this.beanFactoryMetadata = (ConfigurationBeanFactoryMetadata) this.applicationContext.getBean(ConfigurationBeanFactoryMetadata.BEAN_NAME, ConfigurationBeanFactoryMetadata.class);
        this.configurationPropertiesBinder = new ConfigurationPropertiesBinder(this.applicationContext, VALIDATOR_BEAN_NAME);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483647;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ConfigurationProperties annotation = (ConfigurationProperties) getAnnotation(bean, beanName, ConfigurationProperties.class);
        if (annotation != null) {
            bind(bean, beanName, annotation);
        }
        return bean;
    }

    private void bind(Object bean, String beanName, ConfigurationProperties annotation) {
        ResolvableType type = getBeanType(bean, beanName);
        Validated validated = (Validated) getAnnotation(bean, beanName, Validated.class);
        Annotation[] annotations = validated != null ? new Annotation[]{annotation, validated} : new Annotation[]{annotation};
        Bindable<?> target = Bindable.of(type).withExistingValue(bean).withAnnotations(annotations);
        try {
            this.configurationPropertiesBinder.bind(target);
        } catch (Exception ex) {
            throw new ConfigurationPropertiesBindException(beanName, bean, annotation, ex);
        }
    }

    private ResolvableType getBeanType(Object bean, String beanName) {
        Method factoryMethod = this.beanFactoryMetadata.findFactoryMethod(beanName);
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return ResolvableType.forClass(bean.getClass());
    }

    private <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
        Annotation findFactoryAnnotation = this.beanFactoryMetadata.findFactoryAnnotation(beanName, type);
        if (findFactoryAnnotation == null) {
            findFactoryAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), (Class<Annotation>) type);
        }
        return (A) findFactoryAnnotation;
    }
}