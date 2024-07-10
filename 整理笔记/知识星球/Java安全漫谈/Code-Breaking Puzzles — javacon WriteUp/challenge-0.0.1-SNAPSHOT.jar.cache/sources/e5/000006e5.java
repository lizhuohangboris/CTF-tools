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
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraints/Pattern.class */
public @interface Pattern {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraints/Pattern$List.class */
    public @interface List {
        Pattern[] value();
    }

    String regexp();

    Flag[] flags() default {};

    String message() default "{javax.validation.constraints.Pattern.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraints/Pattern$Flag.class */
    public enum Flag {
        UNIX_LINES(1),
        CASE_INSENSITIVE(2),
        COMMENTS(4),
        MULTILINE(8),
        DOTALL(32),
        UNICODE_CASE(64),
        CANON_EQ(128);
        
        private final int value;

        Flag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}