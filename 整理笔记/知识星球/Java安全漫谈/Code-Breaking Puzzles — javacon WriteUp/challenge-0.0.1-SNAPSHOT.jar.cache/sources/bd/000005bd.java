package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.SOURCE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/javax.annotation-api-1.3.2.jar:javax/annotation/Generated.class */
public @interface Generated {
    String[] value();

    String date() default "";

    String comments() default "";
}