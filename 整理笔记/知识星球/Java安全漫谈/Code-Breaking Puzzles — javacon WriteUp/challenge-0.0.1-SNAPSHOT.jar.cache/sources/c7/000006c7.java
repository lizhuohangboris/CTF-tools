package javax.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraints/DecimalMin.class */
public @interface DecimalMin {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraints/DecimalMin$List.class */
    public @interface List {
        DecimalMin[] value();
    }

    String message() default "{javax.validation.constraints.DecimalMin.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();

    boolean inclusive() default true;
}