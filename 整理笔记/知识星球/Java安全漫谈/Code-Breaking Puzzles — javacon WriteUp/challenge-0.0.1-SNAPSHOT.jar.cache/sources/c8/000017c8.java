package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/MvcRequestMatcherProvider.class */
public class MvcRequestMatcherProvider implements RequestMatcherProvider {
    private final HandlerMappingIntrospector introspector;

    public MvcRequestMatcherProvider(HandlerMappingIntrospector introspector) {
        this.introspector = introspector;
    }

    @Override // org.springframework.boot.autoconfigure.security.servlet.RequestMatcherProvider
    public RequestMatcher getRequestMatcher(String pattern) {
        return new MvcRequestMatcher(this.introspector, pattern);
    }
}