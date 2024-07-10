package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/annotation/ResponseStatus.class */
public @interface ResponseStatus {
    @AliasFor("code")
    HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

    @AliasFor("value")
    HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

    String reason() default "";
}