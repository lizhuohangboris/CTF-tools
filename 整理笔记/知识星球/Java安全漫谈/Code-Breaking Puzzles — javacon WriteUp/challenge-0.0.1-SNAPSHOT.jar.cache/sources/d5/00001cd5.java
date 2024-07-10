package org.springframework.context.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationClassUtils.class */
public abstract class ConfigurationClassUtils {
    private static final String CONFIGURATION_CLASS_FULL = "full";
    private static final String CONFIGURATION_CLASS_LITE = "lite";
    private static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");
    private static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");
    private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);
    private static final Set<String> candidateIndicators = new HashSet(8);

    ConfigurationClassUtils() {
    }

    static {
        candidateIndicators.add(Component.class.getName());
        candidateIndicators.add(ComponentScan.class.getName());
        candidateIndicators.add(Import.class.getName());
        candidateIndicators.add(ImportResource.class.getName());
    }

    public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {
        AnnotationMetadata metadata;
        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }
        if ((beanDef instanceof AnnotatedBeanDefinition) && className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
            metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
        } else if ((beanDef instanceof AbstractBeanDefinition) && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
            Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
            metadata = new StandardAnnotationMetadata(beanClass, true);
        } else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            } catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find class file for introspecting configuration annotations: " + className, ex);
                    return false;
                }
                return false;
            }
        }
        if (isFullConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
        } else if (isLiteConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        } else {
            return false;
        }
        Integer order = getOrder(metadata);
        if (order != null) {
            beanDef.setAttribute(ORDER_ATTRIBUTE, order);
            return true;
        }
        return true;
    }

    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        return isFullConfigurationCandidate(metadata) || isLiteConfigurationCandidate(metadata);
    }

    public static boolean isFullConfigurationCandidate(AnnotationMetadata metadata) {
        return metadata.isAnnotated(Configuration.class.getName());
    }

    public static boolean isLiteConfigurationCandidate(AnnotationMetadata metadata) {
        if (metadata.isInterface()) {
            return false;
        }
        for (String indicator : candidateIndicators) {
            if (metadata.isAnnotated(indicator)) {
                return true;
            }
        }
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        } catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
                return false;
            }
            return false;
        }
    }

    public static boolean isFullConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_FULL.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    public static boolean isLiteConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_LITE.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    @Nullable
    public static Integer getOrder(AnnotationMetadata metadata) {
        Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
        if (orderAttributes != null) {
            return (Integer) orderAttributes.get("value");
        }
        return null;
    }

    public static int getOrder(BeanDefinition beanDef) {
        Integer order = (Integer) beanDef.getAttribute(ORDER_ATTRIBUTE);
        if (order != null) {
            return order.intValue();
        }
        return Integer.MAX_VALUE;
    }
}