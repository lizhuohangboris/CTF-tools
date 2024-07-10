package org.hibernate.validator.internal.metadata.aggregated.rule;

import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/OverridingMethodMustNotAlterParameterConstraints.class */
public class OverridingMethodMustNotAlterParameterConstraints extends MethodConfigurationRule {
    @Override // org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule
    public void apply(ConstrainedExecutable method, ConstrainedExecutable otherMethod) {
        if (isDefinedOnSubType(method, otherMethod) && otherMethod.hasParameterConstraints() && !method.isEquallyParameterConstrained(otherMethod)) {
            throw LOG.getParameterConfigurationAlteredInSubTypeException(method.getExecutable(), otherMethod.getExecutable());
        }
    }
}