package org.hibernate.validator.internal.metadata.aggregated.rule;

import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine.class */
public class ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine extends MethodConfigurationRule {
    @Override // org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule
    public void apply(ConstrainedExecutable method, ConstrainedExecutable otherMethod) {
        if (method.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements() && otherMethod.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
            if (isDefinedOnSubType(method, otherMethod) || isDefinedOnSubType(otherMethod, method)) {
                throw LOG.getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException(method.getExecutable(), otherMethod.getExecutable());
            }
        }
    }
}