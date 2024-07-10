package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Constraint(validatedBy = {})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/ParameterScriptAssert.class */
public @interface ParameterScriptAssert {

    @Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/ParameterScriptAssert$List.class */
    public @interface List {
        ParameterScriptAssert[] value();
    }

    String message() default "{org.hibernate.validator.constraints.ParametersScriptAssert.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String lang();

    String script();
}