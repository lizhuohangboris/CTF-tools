package org.hibernate.validator.internal.metadata.aggregated.rule;

import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/ParallelMethodsMustNotDefineGroupConversionForCascadedReturnValue.class */
public class ParallelMethodsMustNotDefineGroupConversionForCascadedReturnValue extends MethodConfigurationRule {
    @Override // org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule
    public void apply(ConstrainedExecutable method, ConstrainedExecutable otherMethod) {
        boolean isCascaded = method.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || otherMethod.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements();
        boolean hasGroupConversions = method.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements() || otherMethod.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements();
        if (isDefinedOnParallelType(method, otherMethod) && isCascaded && hasGroupConversions) {
            throw LOG.getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException(method.getExecutable(), otherMethod.getExecutable());
        }
    }
}