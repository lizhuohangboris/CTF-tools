package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/annotation/ResponseBody.class */
public @interface ResponseBody {
}