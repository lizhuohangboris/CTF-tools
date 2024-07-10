package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/HandlerMethodMappingNamingStrategy.class */
public interface HandlerMethodMappingNamingStrategy<T> {
    String getName(HandlerMethod handlerMethod, T t);
}