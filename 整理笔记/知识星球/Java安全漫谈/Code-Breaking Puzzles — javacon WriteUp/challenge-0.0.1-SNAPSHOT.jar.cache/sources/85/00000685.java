package javax.validation;

import java.util.Map;
import java.util.Set;
import javax.validation.executable.ExecutableType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/BootstrapConfiguration.class */
public interface BootstrapConfiguration {
    String getDefaultProviderClassName();

    String getConstraintValidatorFactoryClassName();

    String getMessageInterpolatorClassName();

    String getTraversableResolverClassName();

    String getParameterNameProviderClassName();

    String getClockProviderClassName();

    Set<String> getValueExtractorClassNames();

    Set<String> getConstraintMappingResourcePaths();

    boolean isExecutableValidationEnabled();

    Set<ExecutableType> getDefaultValidatedExecutableTypes();

    Map<String, String> getProperties();
}