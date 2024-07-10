package javax.validation.constraintvalidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/validation-api-2.0.1.Final.jar:javax/validation/constraintvalidation/SupportedValidationTarget.class */
public @interface SupportedValidationTarget {
    ValidationTarget[] value();
}