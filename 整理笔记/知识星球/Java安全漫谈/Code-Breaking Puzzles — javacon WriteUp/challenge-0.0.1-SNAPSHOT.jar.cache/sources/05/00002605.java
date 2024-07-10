package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/NameValueExpression.class */
public interface NameValueExpression<T> {
    String getName();

    @Nullable
    T getValue();

    boolean isNegated();
}