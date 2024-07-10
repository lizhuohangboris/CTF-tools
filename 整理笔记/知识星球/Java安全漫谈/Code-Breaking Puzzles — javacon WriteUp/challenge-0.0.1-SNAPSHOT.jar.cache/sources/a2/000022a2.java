package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import org.springframework.util.Assert;

@Configuration
@Role(2)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/annotation/ProxyAsyncConfiguration.class */
public class ProxyAsyncConfiguration extends AbstractAsyncConfiguration {
    @Bean(name = {TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME})
    @Role(2)
    public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
        Assert.notNull(this.enableAsync, "@EnableAsync annotation metadata was not injected");
        AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
        bpp.configure(this.executor, this.exceptionHandler);
        Class<? extends Annotation> customAsyncAnnotation = this.enableAsync.getClass("annotation");
        if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
            bpp.setAsyncAnnotationType(customAsyncAnnotation);
        }
        bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
        bpp.setOrder(((Integer) this.enableAsync.getNumber("order")).intValue());
        return bpp;
    }
}