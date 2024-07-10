package org.springframework.web.bind.support;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/WebBindingInitializer.class */
public interface WebBindingInitializer {
    void initBinder(WebDataBinder webDataBinder);

    @Deprecated
    default void initBinder(WebDataBinder binder, WebRequest request) {
        initBinder(binder);
    }
}