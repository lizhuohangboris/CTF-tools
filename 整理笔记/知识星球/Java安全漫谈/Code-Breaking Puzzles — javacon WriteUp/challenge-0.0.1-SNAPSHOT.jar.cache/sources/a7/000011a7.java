package org.hibernate.validator.internal.xml.config;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.BootstrapConfiguration;
import javax.validation.executable.ExecutableType;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/BootstrapConfigurationImpl.class */
class BootstrapConfigurationImpl implements BootstrapConfiguration {
    private static final Set<ExecutableType> DEFAULT_VALIDATED_EXECUTABLE_TYPES = Collections.unmodifiableSet(EnumSet.of(ExecutableType.CONSTRUCTORS, ExecutableType.NON_GETTER_METHODS));
    private static final Set<ExecutableType> ALL_VALIDATED_EXECUTABLE_TYPES = Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(ExecutableType.ALL, ExecutableType.NONE, ExecutableType.IMPLICIT)));
    private static final BootstrapConfiguration DEFAULT_BOOTSTRAP_CONFIGURATION = new BootstrapConfigurationImpl();
    private final String defaultProviderClassName;
    private final String constraintValidatorFactoryClassName;
    private final String messageInterpolatorClassName;
    private final String traversableResolverClassName;
    private final String parameterNameProviderClassName;
    private final String clockProviderClassName;
    private final Set<String> valueExtractorClassNames;
    private final Set<String> constraintMappingResourcePaths;
    private final Map<String, String> properties;
    private final Set<ExecutableType> validatedExecutableTypes;
    private final boolean isExecutableValidationEnabled;

    private BootstrapConfigurationImpl() {
        this.defaultProviderClassName = null;
        this.constraintValidatorFactoryClassName = null;
        this.messageInterpolatorClassName = null;
        this.traversableResolverClassName = null;
        this.parameterNameProviderClassName = null;
        this.clockProviderClassName = null;
        this.valueExtractorClassNames = new HashSet();
        this.validatedExecutableTypes = DEFAULT_VALIDATED_EXECUTABLE_TYPES;
        this.isExecutableValidationEnabled = true;
        this.constraintMappingResourcePaths = new HashSet();
        this.properties = new HashMap();
    }

    public BootstrapConfigurationImpl(String defaultProviderClassName, String constraintValidatorFactoryClassName, String messageInterpolatorClassName, String traversableResolverClassName, String parameterNameProviderClassName, String clockProviderClassName, Set<String> valueExtractorClassNames, EnumSet<ExecutableType> validatedExecutableTypes, boolean isExecutableValidationEnabled, Set<String> constraintMappingResourcePaths, Map<String, String> properties) {
        this.defaultProviderClassName = defaultProviderClassName;
        this.constraintValidatorFactoryClassName = constraintValidatorFactoryClassName;
        this.messageInterpolatorClassName = messageInterpolatorClassName;
        this.traversableResolverClassName = traversableResolverClassName;
        this.parameterNameProviderClassName = parameterNameProviderClassName;
        this.clockProviderClassName = clockProviderClassName;
        this.valueExtractorClassNames = valueExtractorClassNames;
        this.validatedExecutableTypes = prepareValidatedExecutableTypes(validatedExecutableTypes);
        this.isExecutableValidationEnabled = isExecutableValidationEnabled;
        this.constraintMappingResourcePaths = constraintMappingResourcePaths;
        this.properties = properties;
    }

    public static BootstrapConfiguration getDefaultBootstrapConfiguration() {
        return DEFAULT_BOOTSTRAP_CONFIGURATION;
    }

    private Set<ExecutableType> prepareValidatedExecutableTypes(EnumSet<ExecutableType> validatedExecutableTypes) {
        if (validatedExecutableTypes == null) {
            return DEFAULT_VALIDATED_EXECUTABLE_TYPES;
        }
        if (validatedExecutableTypes.contains(ExecutableType.ALL)) {
            return ALL_VALIDATED_EXECUTABLE_TYPES;
        }
        if (validatedExecutableTypes.contains(ExecutableType.NONE)) {
            if (validatedExecutableTypes.size() == 1) {
                return Collections.emptySet();
            }
            EnumSet<ExecutableType> preparedValidatedExecutableTypes = EnumSet.copyOf((EnumSet) validatedExecutableTypes);
            preparedValidatedExecutableTypes.remove(ExecutableType.NONE);
            return CollectionHelper.toImmutableSet(preparedValidatedExecutableTypes);
        }
        return CollectionHelper.toImmutableSet(validatedExecutableTypes);
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getDefaultProviderClassName() {
        return this.defaultProviderClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getConstraintValidatorFactoryClassName() {
        return this.constraintValidatorFactoryClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getMessageInterpolatorClassName() {
        return this.messageInterpolatorClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getTraversableResolverClassName() {
        return this.traversableResolverClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getParameterNameProviderClassName() {
        return this.parameterNameProviderClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public String getClockProviderClassName() {
        return this.clockProviderClassName;
    }

    @Override // javax.validation.BootstrapConfiguration
    public Set<String> getValueExtractorClassNames() {
        return new HashSet(this.valueExtractorClassNames);
    }

    @Override // javax.validation.BootstrapConfiguration
    public Set<String> getConstraintMappingResourcePaths() {
        return new HashSet(this.constraintMappingResourcePaths);
    }

    @Override // javax.validation.BootstrapConfiguration
    public boolean isExecutableValidationEnabled() {
        return this.isExecutableValidationEnabled;
    }

    @Override // javax.validation.BootstrapConfiguration
    public Set<ExecutableType> getDefaultValidatedExecutableTypes() {
        return new HashSet(this.validatedExecutableTypes);
    }

    @Override // javax.validation.BootstrapConfiguration
    public Map<String, String> getProperties() {
        return new HashMap(this.properties);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BootstrapConfigurationImpl");
        sb.append("{defaultProviderClassName='").append(this.defaultProviderClassName).append('\'');
        sb.append(", constraintValidatorFactoryClassName='").append(this.constraintValidatorFactoryClassName).append('\'');
        sb.append(", messageInterpolatorClassName='").append(this.messageInterpolatorClassName).append('\'');
        sb.append(", traversableResolverClassName='").append(this.traversableResolverClassName).append('\'');
        sb.append(", parameterNameProviderClassName='").append(this.parameterNameProviderClassName).append('\'');
        sb.append(", clockProviderClassName='").append(this.clockProviderClassName).append('\'');
        sb.append(", validatedExecutableTypes='").append(this.validatedExecutableTypes).append('\'');
        sb.append(", constraintMappingResourcePaths=").append(this.constraintMappingResourcePaths).append('\'');
        sb.append(", properties=").append(this.properties);
        sb.append('}');
        return sb.toString();
    }
}