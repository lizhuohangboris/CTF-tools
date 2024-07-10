package org.hibernate.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.Normalizer;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(List.class)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/CodePointLength.class */
public @interface CodePointLength {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/CodePointLength$List.class */
    public @interface List {
        CodePointLength[] value();
    }

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    NormalizationStrategy normalizationStrategy() default NormalizationStrategy.NONE;

    String message() default "{org.hibernate.validator.constraints.CodePointLength.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/constraints/CodePointLength$NormalizationStrategy.class */
    public enum NormalizationStrategy {
        NONE(null),
        NFD(Normalizer.Form.NFD),
        NFC(Normalizer.Form.NFC),
        NFKD(Normalizer.Form.NFKD),
        NFKC(Normalizer.Form.NFKC);
        
        private final Normalizer.Form form;

        NormalizationStrategy(Normalizer.Form form) {
            this.form = form;
        }

        public CharSequence normalize(CharSequence value) {
            if (this.form == null || value == null || value.length() == 0) {
                return value;
            }
            return Normalizer.normalize(value, this.form);
        }
    }
}