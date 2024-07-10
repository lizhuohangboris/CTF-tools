package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/ConfigurationPropertiesBinder.class */
public class ConfigurationPropertiesBinder {
    private final ApplicationContext applicationContext;
    private final PropertySources propertySources;
    private final Validator configurationPropertiesValidator;
    private final boolean jsr303Present;
    private volatile Validator jsr303Validator;
    private volatile Binder binder;

    public ConfigurationPropertiesBinder(ApplicationContext applicationContext, String validatorBeanName) {
        this.applicationContext = applicationContext;
        this.propertySources = new PropertySourcesDeducer(applicationContext).getPropertySources();
        this.configurationPropertiesValidator = getConfigurationPropertiesValidator(applicationContext, validatorBeanName);
        this.jsr303Present = ConfigurationPropertiesJsr303Validator.isJsr303Present(applicationContext);
    }

    public void bind(Bindable<?> target) {
        ConfigurationProperties annotation = (ConfigurationProperties) target.getAnnotation(ConfigurationProperties.class);
        Assert.state(annotation != null, () -> {
            return "Missing @ConfigurationProperties on " + target;
        });
        List<Validator> validators = getValidators(target);
        BindHandler bindHandler = getBindHandler(annotation, validators);
        getBinder().bind(annotation.prefix(), target, bindHandler);
    }

    private Validator getConfigurationPropertiesValidator(ApplicationContext applicationContext, String validatorBeanName) {
        if (applicationContext.containsBean(validatorBeanName)) {
            return (Validator) applicationContext.getBean(validatorBeanName, Validator.class);
        }
        return null;
    }

    private List<Validator> getValidators(Bindable<?> target) {
        List<Validator> validators = new ArrayList<>(3);
        if (this.configurationPropertiesValidator != null) {
            validators.add(this.configurationPropertiesValidator);
        }
        if (this.jsr303Present && target.getAnnotation(Validated.class) != null) {
            validators.add(getJsr303Validator());
        }
        if (target.getValue() != null && (target.getValue().get() instanceof Validator)) {
            validators.add((Validator) target.getValue().get());
        }
        return validators;
    }

    private Validator getJsr303Validator() {
        if (this.jsr303Validator == null) {
            this.jsr303Validator = new ConfigurationPropertiesJsr303Validator(this.applicationContext);
        }
        return this.jsr303Validator;
    }

    private BindHandler getBindHandler(ConfigurationProperties annotation, List<Validator> validators) {
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        if (annotation.ignoreInvalidFields()) {
            handler = new IgnoreErrorsBindHandler(handler);
        }
        if (!annotation.ignoreUnknownFields()) {
            UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
            handler = new NoUnboundElementsBindHandler(handler, filter);
        }
        if (!validators.isEmpty()) {
            handler = new ValidationBindHandler(handler, (Validator[]) validators.toArray(new Validator[0]));
        }
        for (ConfigurationPropertiesBindHandlerAdvisor advisor : getBindHandlerAdvisors()) {
            handler = advisor.apply(handler);
        }
        return handler;
    }

    private List<ConfigurationPropertiesBindHandlerAdvisor> getBindHandlerAdvisors() {
        return (List) this.applicationContext.getBeanProvider(ConfigurationPropertiesBindHandlerAdvisor.class).orderedStream().collect(Collectors.toList());
    }

    private Binder getBinder() {
        if (this.binder == null) {
            this.binder = new Binder(getConfigurationPropertySources(), getPropertySourcesPlaceholdersResolver(), getConversionService(), getPropertyEditorInitializer());
        }
        return this.binder;
    }

    private Iterable<ConfigurationPropertySource> getConfigurationPropertySources() {
        return ConfigurationPropertySources.from(this.propertySources);
    }

    private PropertySourcesPlaceholdersResolver getPropertySourcesPlaceholdersResolver() {
        return new PropertySourcesPlaceholdersResolver(this.propertySources);
    }

    private ConversionService getConversionService() {
        return new ConversionServiceDeducer(this.applicationContext).getConversionService();
    }

    private Consumer<PropertyEditorRegistry> getPropertyEditorInitializer() {
        if (this.applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
            beanFactory.getClass();
            return this::copyRegisteredEditorsTo;
        }
        return null;
    }
}