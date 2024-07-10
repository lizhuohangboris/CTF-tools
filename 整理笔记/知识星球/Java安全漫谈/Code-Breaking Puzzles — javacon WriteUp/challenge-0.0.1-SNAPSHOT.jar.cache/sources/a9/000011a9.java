package org.hibernate.validator.internal.xml.config;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.BootstrapConfiguration;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;
import javax.validation.spi.ValidationProvider;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ValidationBootstrapParameters.class */
public class ValidationBootstrapParameters {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private ConstraintValidatorFactory constraintValidatorFactory;
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    private ParameterNameProvider parameterNameProvider;
    private ClockProvider clockProvider;
    private ValidationProvider<?> provider;
    private Class<? extends ValidationProvider<?>> providerClass = null;
    private final Map<String, String> configProperties = new HashMap();
    private final Set<InputStream> mappings = new HashSet();
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> valueExtractorDescriptors = new HashMap();

    public ValidationBootstrapParameters() {
    }

    public ValidationBootstrapParameters(BootstrapConfiguration bootstrapConfiguration, ClassLoader externalClassLoader) {
        setProviderClass(bootstrapConfiguration.getDefaultProviderClassName(), externalClassLoader);
        setMessageInterpolator(bootstrapConfiguration.getMessageInterpolatorClassName(), externalClassLoader);
        setTraversableResolver(bootstrapConfiguration.getTraversableResolverClassName(), externalClassLoader);
        setConstraintValidatorFactory(bootstrapConfiguration.getConstraintValidatorFactoryClassName(), externalClassLoader);
        setParameterNameProvider(bootstrapConfiguration.getParameterNameProviderClassName(), externalClassLoader);
        setClockProvider(bootstrapConfiguration.getClockProviderClassName(), externalClassLoader);
        setValueExtractors(bootstrapConfiguration.getValueExtractorClassNames(), externalClassLoader);
        setMappingStreams(bootstrapConfiguration.getConstraintMappingResourcePaths(), externalClassLoader);
        setConfigProperties(bootstrapConfiguration.getProperties());
    }

    public final ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorFactory;
    }

    public final void setConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public final MessageInterpolator getMessageInterpolator() {
        return this.messageInterpolator;
    }

    public final void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public final ValidationProvider<?> getProvider() {
        return this.provider;
    }

    public final void setProvider(ValidationProvider<?> provider) {
        this.provider = provider;
    }

    public final Class<? extends ValidationProvider<?>> getProviderClass() {
        return this.providerClass;
    }

    public final void setProviderClass(Class<? extends ValidationProvider<?>> providerClass) {
        this.providerClass = providerClass;
    }

    public final TraversableResolver getTraversableResolver() {
        return this.traversableResolver;
    }

    public final void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public final void addConfigProperty(String key, String value) {
        this.configProperties.put(key, value);
    }

    public final void addMapping(InputStream in) {
        this.mappings.add(in);
    }

    public final void addAllMappings(Set<InputStream> mappings) {
        this.mappings.addAll(mappings);
    }

    public final Set<InputStream> getMappings() {
        return CollectionHelper.toImmutableSet(this.mappings);
    }

    public final Map<String, String> getConfigProperties() {
        return CollectionHelper.toImmutableMap(this.configProperties);
    }

    public ParameterNameProvider getParameterNameProvider() {
        return this.parameterNameProvider;
    }

    public void setParameterNameProvider(ParameterNameProvider parameterNameProvider) {
        this.parameterNameProvider = parameterNameProvider;
    }

    public ClockProvider getClockProvider() {
        return this.clockProvider;
    }

    public void setClockProvider(ClockProvider clockProvider) {
        this.clockProvider = clockProvider;
    }

    public Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> getValueExtractorDescriptors() {
        return this.valueExtractorDescriptors;
    }

    public void addValueExtractorDescriptor(ValueExtractorDescriptor descriptor) {
        this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
    }

    private void setProviderClass(String providerFqcn, ClassLoader externalClassLoader) {
        if (providerFqcn != null) {
            try {
                this.providerClass = (Class) run(LoadClass.action(providerFqcn, externalClassLoader));
                LOG.usingValidationProvider(this.providerClass);
            } catch (Exception e) {
                throw LOG.getUnableToInstantiateValidationProviderClassException(providerFqcn, e);
            }
        }
    }

    private void setMessageInterpolator(String messageInterpolatorFqcn, ClassLoader externalClassLoader) {
        if (messageInterpolatorFqcn != null) {
            try {
                Class<? extends MessageInterpolator> messageInterpolatorClass = (Class) run(LoadClass.action(messageInterpolatorFqcn, externalClassLoader));
                this.messageInterpolator = (MessageInterpolator) run(NewInstance.action(messageInterpolatorClass, "message interpolator"));
                LOG.usingMessageInterpolator(messageInterpolatorClass);
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateMessageInterpolatorClassException(messageInterpolatorFqcn, e);
            }
        }
    }

    private void setTraversableResolver(String traversableResolverFqcn, ClassLoader externalClassLoader) {
        if (traversableResolverFqcn != null) {
            try {
                Class<? extends TraversableResolver> clazz = (Class) run(LoadClass.action(traversableResolverFqcn, externalClassLoader));
                this.traversableResolver = (TraversableResolver) run(NewInstance.action(clazz, "traversable resolver"));
                LOG.usingTraversableResolver(clazz);
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateTraversableResolverClassException(traversableResolverFqcn, e);
            }
        }
    }

    private void setConstraintValidatorFactory(String constraintValidatorFactoryFqcn, ClassLoader externalClassLoader) {
        if (constraintValidatorFactoryFqcn != null) {
            try {
                Class<? extends ConstraintValidatorFactory> clazz = (Class) run(LoadClass.action(constraintValidatorFactoryFqcn, externalClassLoader));
                this.constraintValidatorFactory = (ConstraintValidatorFactory) run(NewInstance.action(clazz, "constraint validator factory class"));
                LOG.usingConstraintValidatorFactory(clazz);
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateConstraintValidatorFactoryClassException(constraintValidatorFactoryFqcn, e);
            }
        }
    }

    private void setParameterNameProvider(String parameterNameProviderFqcn, ClassLoader externalClassLoader) {
        if (parameterNameProviderFqcn != null) {
            try {
                Class<? extends ParameterNameProvider> clazz = (Class) run(LoadClass.action(parameterNameProviderFqcn, externalClassLoader));
                this.parameterNameProvider = (ParameterNameProvider) run(NewInstance.action(clazz, "parameter name provider class"));
                LOG.usingParameterNameProvider(clazz);
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateParameterNameProviderClassException(parameterNameProviderFqcn, e);
            }
        }
    }

    private void setClockProvider(String clockProviderFqcn, ClassLoader externalClassLoader) {
        if (clockProviderFqcn != null) {
            try {
                Class<? extends ClockProvider> clazz = (Class) run(LoadClass.action(clockProviderFqcn, externalClassLoader));
                this.clockProvider = (ClockProvider) run(NewInstance.action(clazz, "clock provider class"));
                LOG.usingClockProvider(clazz);
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateClockProviderClassException(clockProviderFqcn, e);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setValueExtractors(Set<String> valueExtractorFqcns, ClassLoader externalClassLoader) {
        for (String valueExtractorFqcn : valueExtractorFqcns) {
            try {
                Class<? extends ValueExtractor<?>> clazz = (Class) run(LoadClass.action(valueExtractorFqcn, externalClassLoader));
                ValueExtractor<?> valueExtractor = (ValueExtractor) run(NewInstance.action(clazz, "value extractor class"));
                ValueExtractorDescriptor descriptor = new ValueExtractorDescriptor(valueExtractor);
                ValueExtractorDescriptor previous = this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
                if (previous != null) {
                    throw LOG.getValueExtractorForTypeAndTypeUseAlreadyPresentException(valueExtractor, previous.getValueExtractor());
                }
                LOG.addingValueExtractor(valueExtractor.getClass());
            } catch (ValidationException e) {
                throw LOG.getUnableToInstantiateValueExtractorClassException(valueExtractorFqcn, e);
            }
        }
    }

    private void setMappingStreams(Set<String> mappingFileNames, ClassLoader externalClassLoader) {
        for (String mappingFileName : mappingFileNames) {
            LOG.debugf("Trying to open input stream for %s.", mappingFileName);
            InputStream in = ResourceLoaderHelper.getResettableInputStreamForPath(mappingFileName, externalClassLoader);
            if (in == null) {
                throw LOG.getUnableToOpenInputStreamForMappingFileException(mappingFileName);
            }
            this.mappings.add(in);
        }
    }

    private void setConfigProperties(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            this.configProperties.put(entry.getKey(), entry.getValue());
        }
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}