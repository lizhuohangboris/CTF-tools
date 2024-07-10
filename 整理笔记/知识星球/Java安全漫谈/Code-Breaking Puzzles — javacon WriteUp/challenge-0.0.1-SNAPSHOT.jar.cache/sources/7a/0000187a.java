package org.springframework.boot.autoconfigure.web.servlet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnMissingBean(parameterizedContainer = {FilterRegistrationBean.class})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ConditionalOnMissingFilterBean.class */
public @interface ConditionalOnMissingFilterBean {
    @AliasFor(annotation = ConditionalOnMissingBean.class)
    Class<? extends Filter>[] value() default {};
}