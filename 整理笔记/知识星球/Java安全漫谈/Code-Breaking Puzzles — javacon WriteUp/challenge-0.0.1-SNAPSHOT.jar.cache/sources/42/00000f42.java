package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@ReportAsSingleViolation
@SupportedValidationTarget({ValidationTarget.ANNOTATED_ELEMENT})
@Retention(RetentionPolicy.RUNTIME)
@Max(Long.MAX_VALUE)
@Constraint(validatedBy = {})
@Min(0)
@Documented
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/Range.class */
public @interface Range {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/Range$List.class */
    public @interface List {
        Range[] value();
    }

    @OverridesAttribute(constraint = Min.class, name = "value")
    long min() default 0;

    @OverridesAttribute(constraint = Max.class, name = "value")
    long max() default Long.MAX_VALUE;

    String message() default "{org.hibernate.validator.constraints.Range.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}