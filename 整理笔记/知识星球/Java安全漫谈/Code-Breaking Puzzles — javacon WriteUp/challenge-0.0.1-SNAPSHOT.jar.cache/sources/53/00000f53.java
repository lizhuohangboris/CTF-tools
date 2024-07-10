package org.hibernate.validator.constraints.br;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import org.hibernate.validator.constraints.Mod11Check;

@ReportAsSingleViolation
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@SupportedValidationTarget({ValidationTarget.ANNOTATED_ELEMENT})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "[0-9]{12}")
@Mod11Check.List({@Mod11Check(threshold = 9, endIndex = 7, checkDigitIndex = 10), @Mod11Check(threshold = 9, startIndex = 8, endIndex = 10, checkDigitIndex = 11)})
@Constraint(validatedBy = {})
@Documented
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/br/TituloEleitoral.class */
public @interface TituloEleitoral {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/br/TituloEleitoral$List.class */
    public @interface List {
        TituloEleitoral[] value();
    }

    String message() default "{org.hibernate.validator.constraints.br.TituloEleitoral.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}