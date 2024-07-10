package org.hibernate.validator.constraintvalidation;

import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.Incubating;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraintvalidation/HibernateConstraintValidatorContext.class */
public interface HibernateConstraintValidatorContext extends ConstraintValidatorContext {
    HibernateConstraintValidatorContext addMessageParameter(String str, Object obj);

    HibernateConstraintValidatorContext addExpressionVariable(String str, Object obj);

    HibernateConstraintValidatorContext withDynamicPayload(Object obj);

    @Incubating
    <C> C getConstraintValidatorPayload(Class<C> cls);
}