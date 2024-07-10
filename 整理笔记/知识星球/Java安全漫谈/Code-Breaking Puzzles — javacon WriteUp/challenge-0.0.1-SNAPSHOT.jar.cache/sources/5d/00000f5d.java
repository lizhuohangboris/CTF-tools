package org.hibernate.validator.constraints.time;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import org.hibernate.validator.Incubating;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@ReportAsSingleViolation
@Incubating
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/time/DurationMin.class */
public @interface DurationMin {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/time/DurationMin$List.class */
    public @interface List {
        DurationMin[] value();
    }

    String message() default "{org.hibernate.validator.constraints.time.DurationMin.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long days() default 0;

    long hours() default 0;

    long minutes() default 0;

    long seconds() default 0;

    long millis() default 0;

    long nanos() default 0;

    boolean inclusive() default true;
}