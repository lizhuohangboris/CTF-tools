package javax.validation;

import java.lang.annotation.Annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/ConstraintValidator.class */
public interface ConstraintValidator<A extends Annotation, T> {
    boolean isValid(T t, ConstraintValidatorContext constraintValidatorContext);

    default void initialize(A constraintAnnotation) {
    }
}