package org.springframework.validation.beanvalidation;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.GenericBootstrap;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/LocalValidatorFactoryBean.class */
public class LocalValidatorFactoryBean extends SpringValidatorAdapter implements ValidatorFactory, ApplicationContextAware, InitializingBean, DisposableBean {
    @Nullable
    private Class providerClass;
    @Nullable
    private ValidationProviderResolver validationProviderResolver;
    @Nullable
    private MessageInterpolator messageInterpolator;
    @Nullable
    private TraversableResolver traversableResolver;
    @Nullable
    private ConstraintValidatorFactory constraintValidatorFactory;
    @Nullable
    private Resource[] mappingLocations;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private ValidatorFactory validatorFactory;
    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Map<String, String> validationPropertyMap = new HashMap();

    public void setProviderClass(Class providerClass) {
        this.providerClass = providerClass;
    }

    public void setValidationProviderResolver(ValidationProviderResolver validationProviderResolver) {
        this.validationProviderResolver = validationProviderResolver;
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public void setValidationMessageSource(MessageSource messageSource) {
        this.messageInterpolator = HibernateValidatorDelegate.buildMessageInterpolator(messageSource);
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public void setConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setMappingLocations(Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    public void setValidationProperties(Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.validationPropertyMap);
    }

    public void setValidationPropertyMap(@Nullable Map<String, String> validationProperties) {
        if (validationProperties != null) {
            this.validationPropertyMap.putAll(validationProperties);
        }
    }

    public Map<String, String> getValidationPropertyMap() {
        return this.validationPropertyMap;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Configuration<?> configuration;
        Resource[] resourceArr;
        if (this.providerClass != null) {
            ProviderSpecificBootstrap bootstrap = Validation.byProvider(this.providerClass);
            if (this.validationProviderResolver != null) {
                bootstrap = bootstrap.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap.configure();
        } else {
            GenericBootstrap bootstrap2 = Validation.byDefaultProvider();
            if (this.validationProviderResolver != null) {
                bootstrap2 = bootstrap2.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap2.configure();
        }
        if (this.applicationContext != null) {
            try {
                Method eclMethod = configuration.getClass().getMethod("externalClassLoader", ClassLoader.class);
                ReflectionUtils.invokeMethod(eclMethod, configuration, this.applicationContext.getClassLoader());
            } catch (NoSuchMethodException e) {
            }
        }
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = configuration.getDefaultMessageInterpolator();
        }
        configuration.messageInterpolator(new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            configuration.traversableResolver(this.traversableResolver);
        }
        ConstraintValidatorFactory targetConstraintValidatorFactory = this.constraintValidatorFactory;
        if (targetConstraintValidatorFactory == null && this.applicationContext != null) {
            targetConstraintValidatorFactory = new SpringConstraintValidatorFactory(this.applicationContext.getAutowireCapableBeanFactory());
        }
        if (targetConstraintValidatorFactory != null) {
            configuration.constraintValidatorFactory(targetConstraintValidatorFactory);
        }
        if (this.parameterNameDiscoverer != null) {
            configureParameterNameProvider(this.parameterNameDiscoverer, configuration);
        }
        if (this.mappingLocations != null) {
            for (Resource location : this.mappingLocations) {
                try {
                    configuration.addMapping(location.getInputStream());
                } catch (IOException e2) {
                    throw new IllegalStateException("Cannot read mapping resource: " + location);
                }
            }
        }
        Map<String, String> map = this.validationPropertyMap;
        Configuration<?> configuration2 = configuration;
        configuration2.getClass();
        map.forEach(this::addProperty);
        postProcessConfiguration(configuration);
        this.validatorFactory = configuration.buildValidatorFactory();
        setTargetValidator(this.validatorFactory.getValidator());
    }

    private void configureParameterNameProvider(final ParameterNameDiscoverer discoverer, Configuration<?> configuration) {
        final ParameterNameProvider defaultProvider = configuration.getDefaultParameterNameProvider();
        configuration.parameterNameProvider(new ParameterNameProvider() { // from class: org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.1
            @Override // javax.validation.ParameterNameProvider
            public List<String> getParameterNames(Constructor<?> constructor) {
                String[] paramNames = discoverer.getParameterNames(constructor);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(constructor);
            }

            @Override // javax.validation.ParameterNameProvider
            public List<String> getParameterNames(Method method) {
                String[] paramNames = discoverer.getParameterNames(method);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(method);
            }
        });
    }

    protected void postProcessConfiguration(Configuration<?> configuration) {
    }

    @Override // javax.validation.ValidatorFactory
    public Validator getValidator() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getValidator();
    }

    @Override // javax.validation.ValidatorFactory
    public ValidatorContext usingContext() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.usingContext();
    }

    @Override // javax.validation.ValidatorFactory
    public MessageInterpolator getMessageInterpolator() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getMessageInterpolator();
    }

    @Override // javax.validation.ValidatorFactory
    public TraversableResolver getTraversableResolver() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getTraversableResolver();
    }

    @Override // javax.validation.ValidatorFactory
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getConstraintValidatorFactory();
    }

    @Override // javax.validation.ValidatorFactory
    public ParameterNameProvider getParameterNameProvider() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getParameterNameProvider();
    }

    @Override // org.springframework.validation.beanvalidation.SpringValidatorAdapter, javax.validation.Validator
    public <T> T unwrap(@Nullable Class<T> type) {
        if (type == null || !ValidatorFactory.class.isAssignableFrom(type)) {
            try {
                return (T) super.unwrap(type);
            } catch (ValidationException e) {
            }
        }
        if (this.validatorFactory != null) {
            try {
                return (T) this.validatorFactory.unwrap(type);
            } catch (ValidationException ex) {
                if (ValidatorFactory.class == type) {
                    return (T) this.validatorFactory;
                }
                throw ex;
            }
        }
        throw new ValidationException("Cannot unwrap to " + type);
    }

    @Override // javax.validation.ValidatorFactory, java.lang.AutoCloseable
    public void close() {
        if (this.validatorFactory != null) {
            this.validatorFactory.close();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        close();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/LocalValidatorFactoryBean$HibernateValidatorDelegate.class */
    private static class HibernateValidatorDelegate {
        private HibernateValidatorDelegate() {
        }

        public static MessageInterpolator buildMessageInterpolator(MessageSource messageSource) {
            return new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource));
        }
    }
}