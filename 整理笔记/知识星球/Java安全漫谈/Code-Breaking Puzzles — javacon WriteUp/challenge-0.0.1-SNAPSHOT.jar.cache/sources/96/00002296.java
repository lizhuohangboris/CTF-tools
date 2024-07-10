package org.springframework.scheduling.annotation;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/annotation/AbstractAsyncConfiguration.class */
public abstract class AbstractAsyncConfiguration implements ImportAware {
    @Nullable
    protected AnnotationAttributes enableAsync;
    @Nullable
    protected Supplier<Executor> executor;
    @Nullable
    protected Supplier<AsyncUncaughtExceptionHandler> exceptionHandler;

    @Override // org.springframework.context.annotation.ImportAware
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableAsync = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false));
        if (this.enableAsync == null) {
            throw new IllegalArgumentException("@EnableAsync is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    void setConfigurers(Collection<AsyncConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("Only one AsyncConfigurer may exist");
        }
        AsyncConfigurer configurer = configurers.iterator().next();
        configurer.getClass();
        this.executor = this::getAsyncExecutor;
        configurer.getClass();
        this.exceptionHandler = this::getAsyncUncaughtExceptionHandler;
    }
}