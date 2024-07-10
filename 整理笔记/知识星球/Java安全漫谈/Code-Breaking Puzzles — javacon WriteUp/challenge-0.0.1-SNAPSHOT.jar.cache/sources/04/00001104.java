package org.hibernate.validator.internal.metadata.aggregated.rule;

import java.lang.reflect.Method;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/VoidMethodsMustNotBeReturnValueConstrained.class */
public class VoidMethodsMustNotBeReturnValueConstrained extends MethodConfigurationRule {
    @Override // org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule
    public void apply(ConstrainedExecutable executable, ConstrainedExecutable otherExecutable) {
        if ((executable.getExecutable() instanceof Method) && ((Method) executable.getExecutable()).getReturnType() == Void.TYPE) {
            if (!executable.getConstraints().isEmpty() || executable.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
                throw LOG.getVoidMethodsMustNotBeConstrainedException(executable.getExecutable());
            }
        }
    }
}