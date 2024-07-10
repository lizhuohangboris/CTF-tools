package org.hibernate.validator.constraints;

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
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/ModCheck.class */
public @interface ModCheck {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/ModCheck$List.class */
    public @interface List {
        ModCheck[] value();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/ModCheck$ModType.class */
    public enum ModType {
        MOD10,
        MOD11
    }

    String message() default "{org.hibernate.validator.constraints.ModCheck.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ModType modType();

    int multiplier();

    int startIndex() default 0;

    int endIndex() default Integer.MAX_VALUE;

    int checkDigitPosition() default -1;

    boolean ignoreNonDigitCharacters() default true;
}