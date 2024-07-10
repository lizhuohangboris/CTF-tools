package org.hibernate.validator.internal.engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.BootstrapConfiguration;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.resolver.TraversableResolvers;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.Version;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.GetInstancesFromServiceLoader;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.hibernate.validator.internal.xml.config.ValidationBootstrapParameters;
import org.hibernate.validator.internal.xml.config.ValidationXmlParser;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ConfigurationImpl.class */
public class ConfigurationImpl implements HibernateValidatorConfiguration, ConfigurationState {
    private static final Log LOG;
    private final ResourceBundleLocator defaultResourceBundleLocator;
    private MessageInterpolator defaultMessageInterpolator;
    private MessageInterpolator messageInterpolator;
    private final TraversableResolver defaultTraversableResolver;
    private final ConstraintValidatorFactory defaultConstraintValidatorFactory;
    private final ParameterNameProvider defaultParameterNameProvider;
    private final ClockProvider defaultClockProvider;
    private ValidationProviderResolver providerResolver;
    private final ValidationBootstrapParameters validationBootstrapParameters;
    private boolean ignoreXmlConfiguration;
    private final Set<InputStream> configurationStreams;
    private BootstrapConfiguration bootstrapConfiguration;
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> valueExtractorDescriptors;
    private final Set<DefaultConstraintMapping> programmaticMappings;
    private boolean failFast;
    private ClassLoader externalClassLoader;
    private final MethodValidationConfiguration.Builder methodValidationConfigurationBuilder;
    private boolean traversableResolverResultCacheEnabled;
    private ScriptEvaluatorFactory scriptEvaluatorFactory;
    private Duration temporalValidationTolerance;
    private Object constraintValidatorPayload;
    static final /* synthetic */ boolean $assertionsDisabled;

    @Override // javax.validation.Configuration
    public /* bridge */ /* synthetic */ HibernateValidatorConfiguration addValueExtractor(ValueExtractor valueExtractor) {
        return addValueExtractor((ValueExtractor<?>) valueExtractor);
    }

    static {
        $assertionsDisabled = !ConfigurationImpl.class.desiredAssertionStatus();
        Version.touch();
        LOG = LoggerFactory.make(MethodHandles.lookup());
    }

    public ConfigurationImpl(BootstrapState state) {
        this();
        if (state.getValidationProviderResolver() == null) {
            this.providerResolver = state.getDefaultValidationProviderResolver();
        } else {
            this.providerResolver = state.getValidationProviderResolver();
        }
    }

    public ConfigurationImpl(ValidationProvider<?> provider) {
        this();
        if (provider == null) {
            throw LOG.getInconsistentConfigurationException();
        }
        this.providerResolver = null;
        this.validationBootstrapParameters.setProvider(provider);
    }

    private ConfigurationImpl() {
        this.ignoreXmlConfiguration = false;
        this.configurationStreams = CollectionHelper.newHashSet();
        this.valueExtractorDescriptors = new HashMap();
        this.programmaticMappings = CollectionHelper.newHashSet();
        this.methodValidationConfigurationBuilder = new MethodValidationConfiguration.Builder();
        this.traversableResolverResultCacheEnabled = true;
        this.validationBootstrapParameters = new ValidationBootstrapParameters();
        this.defaultResourceBundleLocator = new PlatformResourceBundleLocator(AbstractMessageInterpolator.USER_VALIDATION_MESSAGES);
        this.defaultTraversableResolver = TraversableResolvers.getDefault();
        this.defaultConstraintValidatorFactory = new ConstraintValidatorFactoryImpl();
        this.defaultParameterNameProvider = new DefaultParameterNameProvider();
        this.defaultClockProvider = DefaultClockProvider.INSTANCE;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public final HibernateValidatorConfiguration ignoreXmlConfiguration() {
        this.ignoreXmlConfiguration = true;
        return this;
    }

    @Override // javax.validation.Configuration
    /* renamed from: messageInterpolator */
    public final HibernateValidatorConfiguration messageInterpolator2(MessageInterpolator interpolator) {
        if (LOG.isDebugEnabled() && interpolator != null) {
            LOG.debug("Setting custom MessageInterpolator of type " + interpolator.getClass().getName());
        }
        this.validationBootstrapParameters.setMessageInterpolator(interpolator);
        return this;
    }

    @Override // javax.validation.Configuration
    /* renamed from: traversableResolver */
    public final HibernateValidatorConfiguration traversableResolver2(TraversableResolver resolver) {
        if (LOG.isDebugEnabled() && resolver != null) {
            LOG.debug("Setting custom TraversableResolver of type " + resolver.getClass().getName());
        }
        this.validationBootstrapParameters.setTraversableResolver(resolver);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public final ConfigurationImpl enableTraversableResolverResultCache(boolean enabled) {
        this.traversableResolverResultCacheEnabled = enabled;
        return this;
    }

    public final boolean isTraversableResolverResultCacheEnabled() {
        return this.traversableResolverResultCacheEnabled;
    }

    @Override // javax.validation.Configuration
    /* renamed from: constraintValidatorFactory */
    public final HibernateValidatorConfiguration constraintValidatorFactory2(ConstraintValidatorFactory constraintValidatorFactory) {
        if (LOG.isDebugEnabled() && constraintValidatorFactory != null) {
            LOG.debug("Setting custom ConstraintValidatorFactory of type " + constraintValidatorFactory.getClass().getName());
        }
        this.validationBootstrapParameters.setConstraintValidatorFactory(constraintValidatorFactory);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public HibernateValidatorConfiguration parameterNameProvider(ParameterNameProvider parameterNameProvider) {
        if (LOG.isDebugEnabled() && parameterNameProvider != null) {
            LOG.debug("Setting custom ParameterNameProvider of type " + parameterNameProvider.getClass().getName());
        }
        this.validationBootstrapParameters.setParameterNameProvider(parameterNameProvider);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public HibernateValidatorConfiguration clockProvider(ClockProvider clockProvider) {
        if (LOG.isDebugEnabled() && clockProvider != null) {
            LOG.debug("Setting custom ClockProvider of type " + clockProvider.getClass().getName());
        }
        this.validationBootstrapParameters.setClockProvider(clockProvider);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public HibernateValidatorConfiguration addValueExtractor(ValueExtractor<?> extractor) {
        Contracts.assertNotNull(extractor, Messages.MESSAGES.parameterMustNotBeNull("extractor"));
        ValueExtractorDescriptor descriptor = new ValueExtractorDescriptor(extractor);
        ValueExtractorDescriptor previous = this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
        if (previous != null) {
            throw LOG.getValueExtractorForTypeAndTypeUseAlreadyPresentException(extractor, previous.getValueExtractor());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding value extractor " + extractor);
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public final HibernateValidatorConfiguration addMapping(InputStream stream) {
        Contracts.assertNotNull(stream, Messages.MESSAGES.inputStreamCannotBeNull());
        this.validationBootstrapParameters.addMapping(stream.markSupported() ? stream : new BufferedInputStream(stream));
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public final HibernateValidatorConfiguration failFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration allowOverridingMethodAlterParameterConstraint(boolean allow) {
        this.methodValidationConfigurationBuilder.allowOverridingMethodAlterParameterConstraint(allow);
        return this;
    }

    public boolean isAllowOverridingMethodAlterParameterConstraint() {
        return this.methodValidationConfigurationBuilder.isAllowOverridingMethodAlterParameterConstraint();
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration allowMultipleCascadedValidationOnReturnValues(boolean allow) {
        this.methodValidationConfigurationBuilder.allowMultipleCascadedValidationOnReturnValues(allow);
        return this;
    }

    public boolean isAllowMultipleCascadedValidationOnReturnValues() {
        return this.methodValidationConfigurationBuilder.isAllowMultipleCascadedValidationOnReturnValues();
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration allowParallelMethodsDefineParameterConstraints(boolean allow) {
        this.methodValidationConfigurationBuilder.allowParallelMethodsDefineParameterConstraints(allow);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration scriptEvaluatorFactory(ScriptEvaluatorFactory scriptEvaluatorFactory) {
        Contracts.assertNotNull(scriptEvaluatorFactory, Messages.MESSAGES.parameterMustNotBeNull("scriptEvaluatorFactory"));
        this.scriptEvaluatorFactory = scriptEvaluatorFactory;
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration temporalValidationTolerance(Duration temporalValidationTolerance) {
        Contracts.assertNotNull(temporalValidationTolerance, Messages.MESSAGES.parameterMustNotBeNull("temporalValidationTolerance"));
        this.temporalValidationTolerance = temporalValidationTolerance.abs();
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration constraintValidatorPayload(Object constraintValidatorPayload) {
        Contracts.assertNotNull(constraintValidatorPayload, Messages.MESSAGES.parameterMustNotBeNull("constraintValidatorPayload"));
        this.constraintValidatorPayload = constraintValidatorPayload;
        return this;
    }

    public boolean isAllowParallelMethodsDefineParameterConstraints() {
        return this.methodValidationConfigurationBuilder.isAllowParallelMethodsDefineParameterConstraints();
    }

    public MethodValidationConfiguration getMethodValidationConfiguration() {
        return this.methodValidationConfigurationBuilder.build();
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public final DefaultConstraintMapping createConstraintMapping() {
        return new DefaultConstraintMapping();
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public final HibernateValidatorConfiguration addMapping(ConstraintMapping mapping) {
        Contracts.assertNotNull(mapping, Messages.MESSAGES.parameterMustNotBeNull("mapping"));
        this.programmaticMappings.add((DefaultConstraintMapping) mapping);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // javax.validation.Configuration
    public final HibernateValidatorConfiguration addProperty(String name, String value) {
        if (value != null) {
            this.validationBootstrapParameters.addConfigProperty(name, value);
        }
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public HibernateValidatorConfiguration externalClassLoader(ClassLoader externalClassLoader) {
        Contracts.assertNotNull(externalClassLoader, Messages.MESSAGES.parameterMustNotBeNull("externalClassLoader"));
        this.externalClassLoader = externalClassLoader;
        return this;
    }

    @Override // javax.validation.Configuration
    public final ValidatorFactory buildValidatorFactory() {
        loadValueExtractorsFromServiceLoader();
        parseValidationXml();
        for (ValueExtractorDescriptor valueExtractorDescriptor : this.valueExtractorDescriptors.values()) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(valueExtractorDescriptor);
        }
        ValidatorFactory factory = null;
        try {
            if (isSpecificProvider()) {
                factory = this.validationBootstrapParameters.getProvider().buildValidatorFactory(this);
            } else {
                Class<? extends ValidationProvider<?>> providerClass = this.validationBootstrapParameters.getProviderClass();
                if (providerClass != null) {
                    Iterator<ValidationProvider<?>> it = this.providerResolver.getValidationProviders().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ValidationProvider<?> provider = it.next();
                        if (providerClass.isAssignableFrom(provider.getClass())) {
                            factory = provider.buildValidatorFactory(this);
                            break;
                        }
                    }
                    if (factory == null) {
                        throw LOG.getUnableToFindProviderException(providerClass);
                    }
                } else {
                    List<ValidationProvider<?>> providers = this.providerResolver.getValidationProviders();
                    if (!$assertionsDisabled && providers.size() == 0) {
                        throw new AssertionError();
                    }
                    factory = providers.get(0).buildValidatorFactory(this);
                }
            }
            return factory;
        } finally {
            for (InputStream in : this.configurationStreams) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.unableToCloseInputStream();
                }
            }
        }
    }

    @Override // javax.validation.spi.ConfigurationState
    public final boolean isIgnoreXmlConfiguration() {
        return this.ignoreXmlConfiguration;
    }

    @Override // javax.validation.spi.ConfigurationState
    public final MessageInterpolator getMessageInterpolator() {
        if (this.messageInterpolator == null) {
            MessageInterpolator interpolator = this.validationBootstrapParameters.getMessageInterpolator();
            if (interpolator != null) {
                this.messageInterpolator = interpolator;
            } else {
                this.messageInterpolator = getDefaultMessageInterpolatorConfiguredWithClassLoader();
            }
        }
        return this.messageInterpolator;
    }

    @Override // javax.validation.spi.ConfigurationState
    public final Set<InputStream> getMappingStreams() {
        return this.validationBootstrapParameters.getMappings();
    }

    public final boolean getFailFast() {
        return this.failFast;
    }

    @Override // javax.validation.spi.ConfigurationState
    public final ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.validationBootstrapParameters.getConstraintValidatorFactory();
    }

    @Override // javax.validation.spi.ConfigurationState
    public final TraversableResolver getTraversableResolver() {
        return this.validationBootstrapParameters.getTraversableResolver();
    }

    @Override // javax.validation.Configuration
    public BootstrapConfiguration getBootstrapConfiguration() {
        if (this.bootstrapConfiguration == null) {
            this.bootstrapConfiguration = new ValidationXmlParser(this.externalClassLoader).parseValidationXml();
        }
        return this.bootstrapConfiguration;
    }

    @Override // javax.validation.spi.ConfigurationState
    public ParameterNameProvider getParameterNameProvider() {
        return this.validationBootstrapParameters.getParameterNameProvider();
    }

    @Override // javax.validation.spi.ConfigurationState
    public ClockProvider getClockProvider() {
        return this.validationBootstrapParameters.getClockProvider();
    }

    public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
        return this.scriptEvaluatorFactory;
    }

    public Duration getTemporalValidationTolerance() {
        return this.temporalValidationTolerance;
    }

    public Object getConstraintValidatorPayload() {
        return this.constraintValidatorPayload;
    }

    @Override // javax.validation.spi.ConfigurationState
    public Set<ValueExtractor<?>> getValueExtractors() {
        return (Set) this.validationBootstrapParameters.getValueExtractorDescriptors().values().stream().map((v0) -> {
            return v0.getValueExtractor();
        }).collect(Collectors.toSet());
    }

    @Override // javax.validation.spi.ConfigurationState
    public final Map<String, String> getProperties() {
        return this.validationBootstrapParameters.getConfigProperties();
    }

    public ClassLoader getExternalClassLoader() {
        return this.externalClassLoader;
    }

    @Override // javax.validation.Configuration
    public final MessageInterpolator getDefaultMessageInterpolator() {
        if (this.defaultMessageInterpolator == null) {
            this.defaultMessageInterpolator = new ResourceBundleMessageInterpolator(this.defaultResourceBundleLocator);
        }
        return this.defaultMessageInterpolator;
    }

    @Override // javax.validation.Configuration
    public final TraversableResolver getDefaultTraversableResolver() {
        return this.defaultTraversableResolver;
    }

    @Override // javax.validation.Configuration
    public final ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return this.defaultConstraintValidatorFactory;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public final ResourceBundleLocator getDefaultResourceBundleLocator() {
        return this.defaultResourceBundleLocator;
    }

    @Override // javax.validation.Configuration
    public ParameterNameProvider getDefaultParameterNameProvider() {
        return this.defaultParameterNameProvider;
    }

    @Override // javax.validation.Configuration
    public ClockProvider getDefaultClockProvider() {
        return this.defaultClockProvider;
    }

    @Override // org.hibernate.validator.HibernateValidatorConfiguration
    public Set<ValueExtractor<?>> getDefaultValueExtractors() {
        return ValueExtractorManager.getDefaultValueExtractors();
    }

    public final Set<DefaultConstraintMapping> getProgrammaticMappings() {
        return this.programmaticMappings;
    }

    private boolean isSpecificProvider() {
        return this.validationBootstrapParameters.getProvider() != null;
    }

    private void parseValidationXml() {
        if (this.ignoreXmlConfiguration) {
            LOG.ignoringXmlConfiguration();
            if (this.validationBootstrapParameters.getTraversableResolver() == null) {
                this.validationBootstrapParameters.setTraversableResolver(this.defaultTraversableResolver);
            }
            if (this.validationBootstrapParameters.getConstraintValidatorFactory() == null) {
                this.validationBootstrapParameters.setConstraintValidatorFactory(this.defaultConstraintValidatorFactory);
            }
            if (this.validationBootstrapParameters.getParameterNameProvider() == null) {
                this.validationBootstrapParameters.setParameterNameProvider(this.defaultParameterNameProvider);
            }
            if (this.validationBootstrapParameters.getClockProvider() == null) {
                this.validationBootstrapParameters.setClockProvider(this.defaultClockProvider);
                return;
            }
            return;
        }
        ValidationBootstrapParameters xmlParameters = new ValidationBootstrapParameters(getBootstrapConfiguration(), this.externalClassLoader);
        applyXmlSettings(xmlParameters);
    }

    private void loadValueExtractorsFromServiceLoader() {
        List<ValueExtractor> valueExtractors = (List) run(GetInstancesFromServiceLoader.action(this.externalClassLoader != null ? this.externalClassLoader : (ClassLoader) run(GetClassLoader.fromContext()), ValueExtractor.class));
        for (ValueExtractor<?> valueExtractor : valueExtractors) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(new ValueExtractorDescriptor(valueExtractor));
        }
    }

    private void applyXmlSettings(ValidationBootstrapParameters xmlParameters) {
        this.validationBootstrapParameters.setProviderClass(xmlParameters.getProviderClass());
        if (this.validationBootstrapParameters.getMessageInterpolator() == null && xmlParameters.getMessageInterpolator() != null) {
            this.validationBootstrapParameters.setMessageInterpolator(xmlParameters.getMessageInterpolator());
        }
        if (this.validationBootstrapParameters.getTraversableResolver() == null) {
            if (xmlParameters.getTraversableResolver() != null) {
                this.validationBootstrapParameters.setTraversableResolver(xmlParameters.getTraversableResolver());
            } else {
                this.validationBootstrapParameters.setTraversableResolver(this.defaultTraversableResolver);
            }
        }
        if (this.validationBootstrapParameters.getConstraintValidatorFactory() == null) {
            if (xmlParameters.getConstraintValidatorFactory() != null) {
                this.validationBootstrapParameters.setConstraintValidatorFactory(xmlParameters.getConstraintValidatorFactory());
            } else {
                this.validationBootstrapParameters.setConstraintValidatorFactory(this.defaultConstraintValidatorFactory);
            }
        }
        if (this.validationBootstrapParameters.getParameterNameProvider() == null) {
            if (xmlParameters.getParameterNameProvider() != null) {
                this.validationBootstrapParameters.setParameterNameProvider(xmlParameters.getParameterNameProvider());
            } else {
                this.validationBootstrapParameters.setParameterNameProvider(this.defaultParameterNameProvider);
            }
        }
        if (this.validationBootstrapParameters.getClockProvider() == null) {
            if (xmlParameters.getClockProvider() != null) {
                this.validationBootstrapParameters.setClockProvider(xmlParameters.getClockProvider());
            } else {
                this.validationBootstrapParameters.setClockProvider(this.defaultClockProvider);
            }
        }
        for (ValueExtractorDescriptor valueExtractorDescriptor : xmlParameters.getValueExtractorDescriptors().values()) {
            this.validationBootstrapParameters.addValueExtractorDescriptor(valueExtractorDescriptor);
        }
        this.validationBootstrapParameters.addAllMappings(xmlParameters.getMappings());
        this.configurationStreams.addAll(xmlParameters.getMappings());
        for (Map.Entry<String, String> entry : xmlParameters.getConfigProperties().entrySet()) {
            if (this.validationBootstrapParameters.getConfigProperties().get(entry.getKey()) == null) {
                this.validationBootstrapParameters.addConfigProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private MessageInterpolator getDefaultMessageInterpolatorConfiguredWithClassLoader() {
        if (this.externalClassLoader != null) {
            PlatformResourceBundleLocator userResourceBundleLocator = new PlatformResourceBundleLocator(AbstractMessageInterpolator.USER_VALIDATION_MESSAGES, this.externalClassLoader);
            PlatformResourceBundleLocator contributorResourceBundleLocator = new PlatformResourceBundleLocator(AbstractMessageInterpolator.CONTRIBUTOR_VALIDATION_MESSAGES, this.externalClassLoader, true);
            ClassLoader originalContextClassLoader = (ClassLoader) run(GetClassLoader.fromContext());
            try {
                run(SetContextClassLoader.action(this.externalClassLoader));
                ResourceBundleMessageInterpolator resourceBundleMessageInterpolator = new ResourceBundleMessageInterpolator(userResourceBundleLocator, contributorResourceBundleLocator);
                run(SetContextClassLoader.action(originalContextClassLoader));
                return resourceBundleMessageInterpolator;
            } catch (Throwable th) {
                run(SetContextClassLoader.action(originalContextClassLoader));
                throw th;
            }
        }
        return getDefaultMessageInterpolator();
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}