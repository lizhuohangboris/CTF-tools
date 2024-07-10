package org.hibernate.validator.internal.metadata.aggregated.rule;

import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/ParallelMethodsMustNotDefineParameterConstraints.class */
public class ParallelMethodsMustNotDefineParameterConstraints extends MethodConfigurationRule {
    @Override // org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule
    public void apply(ConstrainedExecutable method, ConstrainedExecutable otherMethod) {
        if (isDefinedOnParallelType(method, otherMethod)) {
            if (method.hasParameterConstraints() || otherMethod.hasParameterConstraints()) {
                throw LOG.getParameterConstraintsDefinedInMethodsFromParallelTypesException(method.getExecutable(), otherMethod.getExecutable());
            }
        }
    }
}