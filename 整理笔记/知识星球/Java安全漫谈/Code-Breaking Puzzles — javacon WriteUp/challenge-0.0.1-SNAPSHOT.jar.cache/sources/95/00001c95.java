package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/AnnotatedBeanDefinitionReader.class */
public class AnnotatedBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;
    private BeanNameGenerator beanNameGenerator;
    private ScopeMetadataResolver scopeMetadataResolver;
    private ConditionEvaluator conditionEvaluator;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, getOrCreateEnvironment(registry));
    }

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
        this.beanNameGenerator = new AnnotationBeanNameGenerator();
        this.scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setEnvironment(Environment environment) {
        this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator();
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver();
    }

    public void register(Class<?>... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            registerBean(annotatedClass);
        }
    }

    public void registerBean(Class<?> annotatedClass) {
        doRegisterBean(annotatedClass, null, null, null, new BeanDefinitionCustomizer[0]);
    }

    public <T> void registerBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier) {
        doRegisterBean(annotatedClass, instanceSupplier, null, null, new BeanDefinitionCustomizer[0]);
    }

    public <T> void registerBean(Class<T> annotatedClass, String name, @Nullable Supplier<T> instanceSupplier) {
        doRegisterBean(annotatedClass, instanceSupplier, name, null, new BeanDefinitionCustomizer[0]);
    }

    public void registerBean(Class<?> annotatedClass, Class<? extends Annotation>... qualifiers) {
        doRegisterBean(annotatedClass, null, null, qualifiers, new BeanDefinitionCustomizer[0]);
    }

    public void registerBean(Class<?> annotatedClass, String name, Class<? extends Annotation>... qualifiers) {
        doRegisterBean(annotatedClass, null, name, qualifiers, new BeanDefinitionCustomizer[0]);
    }

    public <T> void doRegisterBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name, @Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition((Class<?>) annotatedClass);
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }
        abd.setInstanceSupplier(instanceSupplier);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                } else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                } else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
        for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
            customizer.customize(abd);
        }
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry), this.registry);
    }

    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable) registry).getEnvironment();
        }
        return new StandardEnvironment();
    }
}