package org.hibernate.validator.internal.metadata.aggregated.rule;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/rule/MethodConfigurationRule.class */
public abstract class MethodConfigurationRule {
    protected static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    public abstract void apply(ConstrainedExecutable constrainedExecutable, ConstrainedExecutable constrainedExecutable2);

    protected boolean isStrictSubType(Class<?> clazz, Class<?> otherClazz) {
        return clazz.isAssignableFrom(otherClazz) && !clazz.equals(otherClazz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDefinedOnSubType(ConstrainedExecutable executable, ConstrainedExecutable otherExecutable) {
        Class<?> clazz = executable.getExecutable().getDeclaringClass();
        Class<?> otherClazz = otherExecutable.getExecutable().getDeclaringClass();
        return isStrictSubType(clazz, otherClazz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDefinedOnParallelType(ConstrainedExecutable executable, ConstrainedExecutable otherExecutable) {
        Class<?> clazz = executable.getExecutable().getDeclaringClass();
        Class<?> otherClazz = otherExecutable.getExecutable().getDeclaringClass();
        return (clazz.isAssignableFrom(otherClazz) || otherClazz.isAssignableFrom(clazz)) ? false : true;
    }
}