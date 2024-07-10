package org.hibernate.validator.constraintvalidation;

import java.lang.annotation.Annotation;
import javax.validation.ConstraintValidator;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraintvalidation/HibernateConstraintValidator.class */
public interface HibernateConstraintValidator<A extends Annotation, T> extends ConstraintValidator<A, T> {
    default void initialize(ConstraintDescriptor<A> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
    }
}