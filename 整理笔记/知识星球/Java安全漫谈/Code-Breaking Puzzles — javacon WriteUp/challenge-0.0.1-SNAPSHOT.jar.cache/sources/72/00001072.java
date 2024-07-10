package org.hibernate.validator.internal.engine;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.spi.ConfigurationState;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.HibernateValidatorFactory;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.constraintdefinition.ConstraintDefinitionContribution;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.HibernateConstraintValidatorInitializationContextImpl;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.scripting.DefaultScriptEvaluatorFactory;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.provider.MetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.ProgrammaticMetaDataProvider;
import org.hibernate.validator.internal.metadata.provider.XmlMetaDataProvider;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.hibernate.validator.spi.cfg.ConstraintMappingContributor;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorFactoryImpl.class */
public class ValidatorFactoryImpl implements HibernateValidatorFactory {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidatorFactoryScopedContext validatorFactoryScopedContext;
    private final ConstraintValidatorManager constraintValidatorManager;
    private final Set<DefaultConstraintMapping> constraintMappings;
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final ExecutableHelper executableHelper;
    private final MethodValidationConfiguration methodValidationConfiguration;
    private final XmlMetaDataProvider xmlMetaDataProvider;
    private final ConcurrentMap<BeanMetaDataManagerKey, BeanMetaDataManager> beanMetaDataManagers;
    private final ValueExtractorManager valueExtractorManager;
    private final ValidationOrderGenerator validationOrderGenerator;

    public ValidatorFactoryImpl(ConfigurationState configurationState) {
        ClassLoader externalClassLoader = getExternalClassLoader(configurationState);
        this.valueExtractorManager = new ValueExtractorManager(configurationState.getValueExtractors());
        this.beanMetaDataManagers = new ConcurrentHashMap();
        this.constraintHelper = new ConstraintHelper();
        this.typeResolutionHelper = new TypeResolutionHelper();
        this.executableHelper = new ExecutableHelper(this.typeResolutionHelper);
        ConfigurationImpl hibernateSpecificConfig = null;
        hibernateSpecificConfig = configurationState instanceof ConfigurationImpl ? (ConfigurationImpl) configurationState : hibernateSpecificConfig;
        if (configurationState.getMappingStreams().isEmpty()) {
            this.xmlMetaDataProvider = null;
        } else {
            this.xmlMetaDataProvider = new XmlMetaDataProvider(this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, configurationState.getMappingStreams(), externalClassLoader);
        }
        this.constraintMappings = Collections.unmodifiableSet(getConstraintMappings(this.typeResolutionHelper, configurationState, externalClassLoader));
        registerCustomConstraintValidators(this.constraintMappings, this.constraintHelper);
        Map<String, String> properties = configurationState.getProperties();
        this.methodValidationConfiguration = new MethodValidationConfiguration.Builder().allowOverridingMethodAlterParameterConstraint(getAllowOverridingMethodAlterParameterConstraint(hibernateSpecificConfig, properties)).allowMultipleCascadedValidationOnReturnValues(getAllowMultipleCascadedValidationOnReturnValues(hibernateSpecificConfig, properties)).allowParallelMethodsDefineParameterConstraints(getAllowParallelMethodsDefineParameterConstraints(hibernateSpecificConfig, properties)).build();
        this.validatorFactoryScopedContext = new ValidatorFactoryScopedContext(configurationState.getMessageInterpolator(), configurationState.getTraversableResolver(), new ExecutableParameterNameProvider(configurationState.getParameterNameProvider()), configurationState.getClockProvider(), getTemporalValidationTolerance(configurationState, properties), getScriptEvaluatorFactory(configurationState, properties, externalClassLoader), getFailFast(hibernateSpecificConfig, properties), getTraversableResolverResultCacheEnabled(hibernateSpecificConfig, properties), getConstraintValidatorPayload(hibernateSpecificConfig));
        this.constraintValidatorManager = new ConstraintValidatorManager(configurationState.getConstraintValidatorFactory(), this.validatorFactoryScopedContext.getConstraintValidatorInitializationContext());
        this.validationOrderGenerator = new ValidationOrderGenerator();
        if (LOG.isDebugEnabled()) {
            logValidatorFactoryScopedConfiguration(this.validatorFactoryScopedContext);
        }
    }

    private static ClassLoader getExternalClassLoader(ConfigurationState configurationState) {
        if (configurationState instanceof ConfigurationImpl) {
            return ((ConfigurationImpl) configurationState).getExternalClassLoader();
        }
        return null;
    }

    private static Set<DefaultConstraintMapping> getConstraintMappings(TypeResolutionHelper typeResolutionHelper, ConfigurationState configurationState, ClassLoader externalClassLoader) {
        Set<DefaultConstraintMapping> constraintMappings = CollectionHelper.newHashSet();
        if (configurationState instanceof ConfigurationImpl) {
            ConfigurationImpl hibernateConfiguration = (ConfigurationImpl) configurationState;
            constraintMappings.addAll(hibernateConfiguration.getProgrammaticMappings());
            ConstraintMappingContributor serviceLoaderBasedContributor = new ServiceLoaderBasedConstraintMappingContributor(typeResolutionHelper, externalClassLoader != null ? externalClassLoader : (ClassLoader) run(GetClassLoader.fromContext()));
            DefaultConstraintMappingBuilder builder = new DefaultConstraintMappingBuilder(constraintMappings);
            serviceLoaderBasedContributor.createConstraintMappings(builder);
        }
        List<ConstraintMappingContributor> contributors = getPropertyConfiguredConstraintMappingContributors(configurationState.getProperties(), externalClassLoader);
        for (ConstraintMappingContributor contributor : contributors) {
            DefaultConstraintMappingBuilder builder2 = new DefaultConstraintMappingBuilder(constraintMappings);
            contributor.createConstraintMappings(builder2);
        }
        return constraintMappings;
    }

    @Override // javax.validation.ValidatorFactory
    public Validator getValidator() {
        return createValidator(this.constraintValidatorManager.getDefaultConstraintValidatorFactory(), this.valueExtractorManager, this.validatorFactoryScopedContext, this.methodValidationConfiguration);
    }

    @Override // javax.validation.ValidatorFactory
    public MessageInterpolator getMessageInterpolator() {
        return this.validatorFactoryScopedContext.getMessageInterpolator();
    }

    @Override // javax.validation.ValidatorFactory
    public TraversableResolver getTraversableResolver() {
        return this.validatorFactoryScopedContext.getTraversableResolver();
    }

    @Override // javax.validation.ValidatorFactory
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorManager.getDefaultConstraintValidatorFactory();
    }

    @Override // javax.validation.ValidatorFactory
    public ParameterNameProvider getParameterNameProvider() {
        return this.validatorFactoryScopedContext.getParameterNameProvider().getDelegate();
    }

    public ExecutableParameterNameProvider getExecutableParameterNameProvider() {
        return this.validatorFactoryScopedContext.getParameterNameProvider();
    }

    @Override // javax.validation.ValidatorFactory
    public ClockProvider getClockProvider() {
        return this.validatorFactoryScopedContext.getClockProvider();
    }

    @Override // org.hibernate.validator.HibernateValidatorFactory
    public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
        return this.validatorFactoryScopedContext.getScriptEvaluatorFactory();
    }

    @Override // org.hibernate.validator.HibernateValidatorFactory
    public Duration getTemporalValidationTolerance() {
        return this.validatorFactoryScopedContext.getTemporalValidationTolerance();
    }

    public boolean isFailFast() {
        return this.validatorFactoryScopedContext.isFailFast();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodValidationConfiguration getMethodValidationConfiguration() {
        return this.methodValidationConfiguration;
    }

    public boolean isTraversableResolverResultCacheEnabled() {
        return this.validatorFactoryScopedContext.isTraversableResolverResultCacheEnabled();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueExtractorManager getValueExtractorManager() {
        return this.valueExtractorManager;
    }

    @Override // javax.validation.ValidatorFactory
    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(HibernateValidatorFactory.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    @Override // org.hibernate.validator.HibernateValidatorFactory, javax.validation.ValidatorFactory
    public HibernateValidatorContext usingContext() {
        return new ValidatorContextImpl(this);
    }

    @Override // javax.validation.ValidatorFactory, java.lang.AutoCloseable
    public void close() {
        this.constraintValidatorManager.clear();
        this.constraintHelper.clear();
        for (BeanMetaDataManager beanMetaDataManager : this.beanMetaDataManagers.values()) {
            beanMetaDataManager.clear();
        }
        this.validatorFactoryScopedContext.getScriptEvaluatorFactory().clear();
        this.valueExtractorManager.clear();
    }

    public ValidatorFactoryScopedContext getValidatorFactoryScopedContext() {
        return this.validatorFactoryScopedContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Validator createValidator(ConstraintValidatorFactory constraintValidatorFactory, ValueExtractorManager valueExtractorManager, ValidatorFactoryScopedContext validatorFactoryScopedContext, MethodValidationConfiguration methodValidationConfiguration) {
        BeanMetaDataManager beanMetaDataManager = this.beanMetaDataManagers.computeIfAbsent(new BeanMetaDataManagerKey(validatorFactoryScopedContext.getParameterNameProvider(), valueExtractorManager, methodValidationConfiguration), key -> {
            return new BeanMetaDataManager(this.constraintHelper, this.executableHelper, this.typeResolutionHelper, validatorFactoryScopedContext.getParameterNameProvider(), valueExtractorManager, this.validationOrderGenerator, buildDataProviders(), methodValidationConfiguration);
        });
        return new ValidatorImpl(constraintValidatorFactory, beanMetaDataManager, valueExtractorManager, this.constraintValidatorManager, this.validationOrderGenerator, validatorFactoryScopedContext);
    }

    private List<MetaDataProvider> buildDataProviders() {
        List<MetaDataProvider> metaDataProviders = CollectionHelper.newArrayList();
        if (this.xmlMetaDataProvider != null) {
            metaDataProviders.add(this.xmlMetaDataProvider);
        }
        if (!this.constraintMappings.isEmpty()) {
            metaDataProviders.add(new ProgrammaticMetaDataProvider(this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.constraintMappings));
        }
        return metaDataProviders;
    }

    private static boolean checkPropertiesForBoolean(Map<String, String> properties, String propertyKey, boolean programmaticValue) {
        boolean value = programmaticValue;
        String propertyStringValue = properties.get(propertyKey);
        if (propertyStringValue != null) {
            value = Boolean.valueOf(propertyStringValue).booleanValue();
        }
        return value;
    }

    private static List<ConstraintMappingContributor> getPropertyConfiguredConstraintMappingContributors(Map<String, String> properties, ClassLoader externalClassLoader) {
        String deprecatedPropertyValue = properties.get(HibernateValidatorConfiguration.CONSTRAINT_MAPPING_CONTRIBUTOR);
        String propertyValue = properties.get(HibernateValidatorConfiguration.CONSTRAINT_MAPPING_CONTRIBUTORS);
        if (StringHelper.isNullOrEmptyString(deprecatedPropertyValue) && StringHelper.isNullOrEmptyString(propertyValue)) {
            return Collections.emptyList();
        }
        StringBuilder assembledPropertyValue = new StringBuilder();
        if (!StringHelper.isNullOrEmptyString(deprecatedPropertyValue)) {
            assembledPropertyValue.append(deprecatedPropertyValue);
        }
        if (!StringHelper.isNullOrEmptyString(propertyValue)) {
            if (assembledPropertyValue.length() > 0) {
                assembledPropertyValue.append(",");
            }
            assembledPropertyValue.append(propertyValue);
        }
        String[] contributorNames = assembledPropertyValue.toString().split(",");
        ArrayList newArrayList = CollectionHelper.newArrayList(contributorNames.length);
        for (String contributorName : contributorNames) {
            Class<? extends ConstraintMappingContributor> contributorType = (Class) run(LoadClass.action(contributorName, externalClassLoader));
            newArrayList.add(run(NewInstance.action(contributorType, "constraint mapping contributor class")));
        }
        return newArrayList;
    }

    private static boolean getAllowParallelMethodsDefineParameterConstraints(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return checkPropertiesForBoolean(properties, HibernateValidatorConfiguration.ALLOW_PARALLEL_METHODS_DEFINE_PARAMETER_CONSTRAINTS, hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowParallelMethodsDefineParameterConstraints() : false);
    }

    private static boolean getAllowMultipleCascadedValidationOnReturnValues(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return checkPropertiesForBoolean(properties, HibernateValidatorConfiguration.ALLOW_MULTIPLE_CASCADED_VALIDATION_ON_RESULT, hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowMultipleCascadedValidationOnReturnValues() : false);
    }

    private static boolean getAllowOverridingMethodAlterParameterConstraint(ConfigurationImpl hibernateSpecificConfig, Map<String, String> properties) {
        return checkPropertiesForBoolean(properties, HibernateValidatorConfiguration.ALLOW_PARAMETER_CONSTRAINT_OVERRIDE, hibernateSpecificConfig != null ? hibernateSpecificConfig.getMethodValidationConfiguration().isAllowOverridingMethodAlterParameterConstraint() : false);
    }

    private static boolean getTraversableResolverResultCacheEnabled(ConfigurationImpl configuration, Map<String, String> properties) {
        return checkPropertiesForBoolean(properties, HibernateValidatorConfiguration.ENABLE_TRAVERSABLE_RESOLVER_RESULT_CACHE, configuration != null ? configuration.isTraversableResolverResultCacheEnabled() : true);
    }

    private static boolean getFailFast(ConfigurationImpl configuration, Map<String, String> properties) {
        boolean tmpFailFast = configuration != null ? configuration.getFailFast() : false;
        String propertyStringValue = properties.get(HibernateValidatorConfiguration.FAIL_FAST);
        if (propertyStringValue != null) {
            boolean configurationValue = Boolean.valueOf(propertyStringValue).booleanValue();
            if (tmpFailFast && !configurationValue) {
                throw LOG.getInconsistentFailFastConfigurationException();
            }
            tmpFailFast = configurationValue;
        }
        return tmpFailFast;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static ScriptEvaluatorFactory getScriptEvaluatorFactory(ConfigurationState configurationState, Map<String, String> properties, ClassLoader externalClassLoader) {
        if (configurationState instanceof ConfigurationImpl) {
            ConfigurationImpl hibernateSpecificConfig = (ConfigurationImpl) configurationState;
            if (hibernateSpecificConfig.getScriptEvaluatorFactory() != null) {
                LOG.usingScriptEvaluatorFactory(hibernateSpecificConfig.getScriptEvaluatorFactory().getClass());
                return hibernateSpecificConfig.getScriptEvaluatorFactory();
            }
        }
        String scriptEvaluatorFactoryFqcn = properties.get(HibernateValidatorConfiguration.SCRIPT_EVALUATOR_FACTORY_CLASSNAME);
        if (scriptEvaluatorFactoryFqcn != null) {
            try {
                Class<? extends ScriptEvaluatorFactory> clazz = (Class) run(LoadClass.action(scriptEvaluatorFactoryFqcn, externalClassLoader));
                ScriptEvaluatorFactory scriptEvaluatorFactory = (ScriptEvaluatorFactory) run(NewInstance.action(clazz, "script evaluator factory class"));
                LOG.usingScriptEvaluatorFactory(clazz);
                return scriptEvaluatorFactory;
            } catch (Exception e) {
                throw LOG.getUnableToInstantiateScriptEvaluatorFactoryClassException(scriptEvaluatorFactoryFqcn, e);
            }
        }
        return new DefaultScriptEvaluatorFactory(externalClassLoader);
    }

    private Duration getTemporalValidationTolerance(ConfigurationState configurationState, Map<String, String> properties) {
        if (configurationState instanceof ConfigurationImpl) {
            ConfigurationImpl hibernateSpecificConfig = (ConfigurationImpl) configurationState;
            if (hibernateSpecificConfig.getTemporalValidationTolerance() != null) {
                LOG.logTemporalValidationTolerance(hibernateSpecificConfig.getTemporalValidationTolerance());
                return hibernateSpecificConfig.getTemporalValidationTolerance();
            }
        }
        String temporalValidationToleranceProperty = properties.get(HibernateValidatorConfiguration.TEMPORAL_VALIDATION_TOLERANCE);
        if (temporalValidationToleranceProperty != null) {
            try {
                Duration tolerance = Duration.ofMillis(Long.parseLong(temporalValidationToleranceProperty)).abs();
                LOG.logTemporalValidationTolerance(tolerance);
                return tolerance;
            } catch (Exception e) {
                throw LOG.getUnableToParseTemporalValidationToleranceException(temporalValidationToleranceProperty, e);
            }
        }
        return Duration.ZERO;
    }

    private Object getConstraintValidatorPayload(ConfigurationState configurationState) {
        if (configurationState instanceof ConfigurationImpl) {
            ConfigurationImpl hibernateSpecificConfig = (ConfigurationImpl) configurationState;
            if (hibernateSpecificConfig.getConstraintValidatorPayload() != null) {
                LOG.logConstraintValidatorPayload(hibernateSpecificConfig.getConstraintValidatorPayload());
                return hibernateSpecificConfig.getConstraintValidatorPayload();
            }
            return null;
        }
        return null;
    }

    private static void registerCustomConstraintValidators(Set<DefaultConstraintMapping> constraintMappings, ConstraintHelper constraintHelper) {
        Set<Class<?>> definedConstraints = CollectionHelper.newHashSet();
        for (DefaultConstraintMapping constraintMapping : constraintMappings) {
            for (ConstraintDefinitionContribution<?> contribution : constraintMapping.getConstraintDefinitionContributions()) {
                processConstraintDefinitionContribution(contribution, constraintHelper, definedConstraints);
            }
        }
    }

    private static <A extends Annotation> void processConstraintDefinitionContribution(ConstraintDefinitionContribution<A> constraintDefinitionContribution, ConstraintHelper constraintHelper, Set<Class<?>> definedConstraints) {
        Class<A> constraintType = constraintDefinitionContribution.getConstraintType();
        if (definedConstraints.contains(constraintType)) {
            throw LOG.getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(constraintType);
        }
        definedConstraints.add(constraintType);
        constraintHelper.putValidatorDescriptors(constraintType, constraintDefinitionContribution.getValidatorDescriptors(), constraintDefinitionContribution.includeExisting());
    }

    private static void logValidatorFactoryScopedConfiguration(ValidatorFactoryScopedContext context) {
        LOG.logValidatorFactoryScopedConfiguration(context.getMessageInterpolator().getClass(), "message interpolator");
        LOG.logValidatorFactoryScopedConfiguration(context.getTraversableResolver().getClass(), "traversable resolver");
        LOG.logValidatorFactoryScopedConfiguration(context.getParameterNameProvider().getClass(), "parameter name provider");
        LOG.logValidatorFactoryScopedConfiguration(context.getClockProvider().getClass(), "clock provider");
        LOG.logValidatorFactoryScopedConfiguration(context.getScriptEvaluatorFactory().getClass(), "script evaluator factory");
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorFactoryImpl$DefaultConstraintMappingBuilder.class */
    public static class DefaultConstraintMappingBuilder implements ConstraintMappingContributor.ConstraintMappingBuilder {
        private final Set<DefaultConstraintMapping> mappings;

        public DefaultConstraintMappingBuilder(Set<DefaultConstraintMapping> mappings) {
            this.mappings = mappings;
        }

        @Override // org.hibernate.validator.spi.cfg.ConstraintMappingContributor.ConstraintMappingBuilder
        public ConstraintMapping addConstraintMapping() {
            DefaultConstraintMapping mapping = new DefaultConstraintMapping();
            this.mappings.add(mapping);
            return mapping;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorFactoryImpl$BeanMetaDataManagerKey.class */
    public static class BeanMetaDataManagerKey {
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ValueExtractorManager valueExtractorManager;
        private final MethodValidationConfiguration methodValidationConfiguration;
        private final int hashCode;

        public BeanMetaDataManagerKey(ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, MethodValidationConfiguration methodValidationConfiguration) {
            this.parameterNameProvider = parameterNameProvider;
            this.valueExtractorManager = valueExtractorManager;
            this.methodValidationConfiguration = methodValidationConfiguration;
            this.hashCode = buildHashCode(parameterNameProvider, valueExtractorManager, methodValidationConfiguration);
        }

        private static int buildHashCode(ExecutableParameterNameProvider parameterNameProvider, ValueExtractorManager valueExtractorManager, MethodValidationConfiguration methodValidationConfiguration) {
            int result = (31 * 1) + (methodValidationConfiguration == null ? 0 : methodValidationConfiguration.hashCode());
            return (31 * ((31 * result) + (parameterNameProvider == null ? 0 : parameterNameProvider.hashCode()))) + (valueExtractorManager == null ? 0 : valueExtractorManager.hashCode());
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            BeanMetaDataManagerKey other = (BeanMetaDataManagerKey) obj;
            return this.methodValidationConfiguration.equals(other.methodValidationConfiguration) && this.parameterNameProvider.equals(other.parameterNameProvider) && this.valueExtractorManager.equals(other.valueExtractorManager);
        }

        public String toString() {
            return "BeanMetaDataManagerKey [parameterNameProvider=" + this.parameterNameProvider + ", valueExtractorManager=" + this.valueExtractorManager + ", methodValidationConfiguration=" + this.methodValidationConfiguration + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorFactoryImpl$ValidatorFactoryScopedContext.class */
    public static class ValidatorFactoryScopedContext {
        private final MessageInterpolator messageInterpolator;
        private final TraversableResolver traversableResolver;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ClockProvider clockProvider;
        private final Duration temporalValidationTolerance;
        private final ScriptEvaluatorFactory scriptEvaluatorFactory;
        private final boolean failFast;
        private final boolean traversableResolverResultCacheEnabled;
        private final Object constraintValidatorPayload;
        private final HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext;

        private ValidatorFactoryScopedContext(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ExecutableParameterNameProvider parameterNameProvider, ClockProvider clockProvider, Duration temporalValidationTolerance, ScriptEvaluatorFactory scriptEvaluatorFactory, boolean failFast, boolean traversableResolverResultCacheEnabled, Object constraintValidatorPayload) {
            this(messageInterpolator, traversableResolver, parameterNameProvider, clockProvider, temporalValidationTolerance, scriptEvaluatorFactory, failFast, traversableResolverResultCacheEnabled, constraintValidatorPayload, new HibernateConstraintValidatorInitializationContextImpl(scriptEvaluatorFactory, clockProvider, temporalValidationTolerance));
        }

        private ValidatorFactoryScopedContext(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ExecutableParameterNameProvider parameterNameProvider, ClockProvider clockProvider, Duration temporalValidationTolerance, ScriptEvaluatorFactory scriptEvaluatorFactory, boolean failFast, boolean traversableResolverResultCacheEnabled, Object constraintValidatorPayload, HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext) {
            this.messageInterpolator = messageInterpolator;
            this.traversableResolver = traversableResolver;
            this.parameterNameProvider = parameterNameProvider;
            this.clockProvider = clockProvider;
            this.temporalValidationTolerance = temporalValidationTolerance;
            this.scriptEvaluatorFactory = scriptEvaluatorFactory;
            this.failFast = failFast;
            this.traversableResolverResultCacheEnabled = traversableResolverResultCacheEnabled;
            this.constraintValidatorPayload = constraintValidatorPayload;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
        }

        public MessageInterpolator getMessageInterpolator() {
            return this.messageInterpolator;
        }

        public TraversableResolver getTraversableResolver() {
            return this.traversableResolver;
        }

        public ExecutableParameterNameProvider getParameterNameProvider() {
            return this.parameterNameProvider;
        }

        public ClockProvider getClockProvider() {
            return this.clockProvider;
        }

        public Duration getTemporalValidationTolerance() {
            return this.temporalValidationTolerance;
        }

        public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
            return this.scriptEvaluatorFactory;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public boolean isTraversableResolverResultCacheEnabled() {
            return this.traversableResolverResultCacheEnabled;
        }

        public Object getConstraintValidatorPayload() {
            return this.constraintValidatorPayload;
        }

        public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
            return this.constraintValidatorInitializationContext;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorFactoryImpl$ValidatorFactoryScopedContext$Builder.class */
        public static class Builder {
            private final ValidatorFactoryScopedContext defaultContext;
            private MessageInterpolator messageInterpolator;
            private TraversableResolver traversableResolver;
            private ExecutableParameterNameProvider parameterNameProvider;
            private ClockProvider clockProvider;
            private ScriptEvaluatorFactory scriptEvaluatorFactory;
            private Duration temporalValidationTolerance;
            private boolean failFast;
            private boolean traversableResolverResultCacheEnabled;
            private Object constraintValidatorPayload;
            private HibernateConstraintValidatorInitializationContextImpl constraintValidatorInitializationContext;

            /* JADX INFO: Access modifiers changed from: package-private */
            public Builder(ValidatorFactoryScopedContext defaultContext) {
                Contracts.assertNotNull(defaultContext, "Default context cannot be null.");
                this.defaultContext = defaultContext;
                this.messageInterpolator = defaultContext.messageInterpolator;
                this.traversableResolver = defaultContext.traversableResolver;
                this.parameterNameProvider = defaultContext.parameterNameProvider;
                this.clockProvider = defaultContext.clockProvider;
                this.scriptEvaluatorFactory = defaultContext.scriptEvaluatorFactory;
                this.temporalValidationTolerance = defaultContext.temporalValidationTolerance;
                this.failFast = defaultContext.failFast;
                this.traversableResolverResultCacheEnabled = defaultContext.traversableResolverResultCacheEnabled;
                this.constraintValidatorPayload = defaultContext.constraintValidatorPayload;
                this.constraintValidatorInitializationContext = defaultContext.constraintValidatorInitializationContext;
            }

            public Builder setMessageInterpolator(MessageInterpolator messageInterpolator) {
                if (messageInterpolator == null) {
                    this.messageInterpolator = this.defaultContext.messageInterpolator;
                } else {
                    this.messageInterpolator = messageInterpolator;
                }
                return this;
            }

            public Builder setTraversableResolver(TraversableResolver traversableResolver) {
                if (traversableResolver == null) {
                    this.traversableResolver = this.defaultContext.traversableResolver;
                } else {
                    this.traversableResolver = traversableResolver;
                }
                return this;
            }

            public Builder setParameterNameProvider(ParameterNameProvider parameterNameProvider) {
                if (parameterNameProvider == null) {
                    this.parameterNameProvider = this.defaultContext.parameterNameProvider;
                } else {
                    this.parameterNameProvider = new ExecutableParameterNameProvider(parameterNameProvider);
                }
                return this;
            }

            public Builder setClockProvider(ClockProvider clockProvider) {
                if (clockProvider == null) {
                    this.clockProvider = this.defaultContext.clockProvider;
                } else {
                    this.clockProvider = clockProvider;
                }
                return this;
            }

            public Builder setTemporalValidationTolerance(Duration temporalValidationTolerance) {
                this.temporalValidationTolerance = temporalValidationTolerance == null ? Duration.ZERO : temporalValidationTolerance.abs();
                return this;
            }

            public Builder setScriptEvaluatorFactory(ScriptEvaluatorFactory scriptEvaluatorFactory) {
                if (scriptEvaluatorFactory == null) {
                    this.scriptEvaluatorFactory = this.defaultContext.scriptEvaluatorFactory;
                } else {
                    this.scriptEvaluatorFactory = scriptEvaluatorFactory;
                }
                return this;
            }

            public Builder setFailFast(boolean failFast) {
                this.failFast = failFast;
                return this;
            }

            public Builder setTraversableResolverResultCacheEnabled(boolean traversableResolverResultCacheEnabled) {
                this.traversableResolverResultCacheEnabled = traversableResolverResultCacheEnabled;
                return this;
            }

            public Builder setConstraintValidatorPayload(Object constraintValidatorPayload) {
                this.constraintValidatorPayload = constraintValidatorPayload;
                return this;
            }

            public ValidatorFactoryScopedContext build() {
                return new ValidatorFactoryScopedContext(this.messageInterpolator, this.traversableResolver, this.parameterNameProvider, this.clockProvider, this.temporalValidationTolerance, this.scriptEvaluatorFactory, this.failFast, this.traversableResolverResultCacheEnabled, this.constraintValidatorPayload, HibernateConstraintValidatorInitializationContextImpl.of(this.constraintValidatorInitializationContext, this.scriptEvaluatorFactory, this.clockProvider, this.temporalValidationTolerance));
            }
        }
    }
}