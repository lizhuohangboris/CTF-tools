package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({LoadTimeWeavingConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/EnableLoadTimeWeaving.class */
public @interface EnableLoadTimeWeaving {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/EnableLoadTimeWeaving$AspectJWeaving.class */
    public enum AspectJWeaving {
        ENABLED,
        DISABLED,
        AUTODETECT
    }

    AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;
}