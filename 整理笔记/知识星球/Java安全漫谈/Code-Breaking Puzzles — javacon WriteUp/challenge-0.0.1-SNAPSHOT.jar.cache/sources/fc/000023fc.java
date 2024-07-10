package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/MissingRequestCookieException.class */
public class MissingRequestCookieException extends ServletRequestBindingException {
    private final String cookieName;
    private final MethodParameter parameter;

    public MissingRequestCookieException(String cookieName, MethodParameter parameter) {
        super("");
        this.cookieName = cookieName;
        this.parameter = parameter;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Missing cookie '" + this.cookieName + "' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
    }

    public final String getCookieName() {
        return this.cookieName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}