package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/RequestCondition.class */
public interface RequestCondition<T> {
    T combine(T t);

    @Nullable
    T getMatchingCondition(HttpServletRequest httpServletRequest);

    int compareTo(T t, HttpServletRequest httpServletRequest);
}