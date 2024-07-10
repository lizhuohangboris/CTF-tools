package org.springframework.boot.autoconfigure.hateoas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaHttpMessageConverterConfiguration.class */
public class HypermediaHttpMessageConverterConfiguration {
    @ConditionalOnProperty(prefix = "spring.hateoas", name = {"use-hal-as-default-json-media-type"}, matchIfMissing = true)
    @Bean
    public static HalMessageConverterSupportedMediaTypesCustomizer halMessageConverterSupportedMediaTypeCustomizer() {
        return new HalMessageConverterSupportedMediaTypesCustomizer();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaHttpMessageConverterConfiguration$HalMessageConverterSupportedMediaTypesCustomizer.class */
    private static class HalMessageConverterSupportedMediaTypesCustomizer implements BeanFactoryAware {
        private volatile BeanFactory beanFactory;

        private HalMessageConverterSupportedMediaTypesCustomizer() {
        }

        @PostConstruct
        public void configureHttpMessageConverters() {
            if (this.beanFactory instanceof ListableBeanFactory) {
                configureHttpMessageConverters(((ListableBeanFactory) this.beanFactory).getBeansOfType(RequestMappingHandlerAdapter.class).values());
            }
        }

        private void configureHttpMessageConverters(Collection<RequestMappingHandlerAdapter> handlerAdapters) {
            for (RequestMappingHandlerAdapter handlerAdapter : handlerAdapters) {
                for (HttpMessageConverter<?> messageConverter : handlerAdapter.getMessageConverters()) {
                    configureHttpMessageConverter(messageConverter);
                }
            }
        }

        private void configureHttpMessageConverter(HttpMessageConverter<?> converter) {
            if (converter instanceof TypeConstrainedMappingJackson2HttpMessageConverter) {
                List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                if (!supportedMediaTypes.contains(MediaType.APPLICATION_JSON)) {
                    supportedMediaTypes.add(MediaType.APPLICATION_JSON);
                }
                ((AbstractHttpMessageConverter) converter).setSupportedMediaTypes(supportedMediaTypes);
            }
        }

        @Override // org.springframework.beans.factory.BeanFactoryAware
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }
}