package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConversionServiceDeducer.class */
public class ConversionServiceDeducer {
    private final ApplicationContext applicationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConversionServiceDeducer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ConversionService getConversionService() {
        try {
            return (ConversionService) this.applicationContext.getBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConversionService.class);
        } catch (NoSuchBeanDefinitionException e) {
            return new Factory(this.applicationContext.getAutowireCapableBeanFactory()).create();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConversionServiceDeducer$Factory.class */
    public static class Factory {
        private final List<Converter> converters;
        private final List<GenericConverter> genericConverters;

        Factory(BeanFactory beanFactory) {
            this.converters = beans(beanFactory, Converter.class, ConfigurationPropertiesBinding.VALUE);
            this.genericConverters = beans(beanFactory, GenericConverter.class, ConfigurationPropertiesBinding.VALUE);
        }

        private <T> List<T> beans(BeanFactory beanFactory, Class<T> type, String qualifier) {
            if (beanFactory instanceof ListableBeanFactory) {
                return beans(type, qualifier, (ListableBeanFactory) beanFactory);
            }
            return Collections.emptyList();
        }

        private <T> List<T> beans(Class<T> type, String qualifier, ListableBeanFactory beanFactory) {
            return new ArrayList(BeanFactoryAnnotationUtils.qualifiedBeansOfType(beanFactory, type, qualifier).values());
        }

        public ConversionService create() {
            if (this.converters.isEmpty() && this.genericConverters.isEmpty()) {
                return ApplicationConversionService.getSharedInstance();
            }
            ApplicationConversionService conversionService = new ApplicationConversionService();
            for (Converter converter : this.converters) {
                conversionService.addConverter(converter);
            }
            for (GenericConverter genericConverter : this.genericConverters) {
                conversionService.addConverter(genericConverter);
            }
            return conversionService;
        }
    }
}