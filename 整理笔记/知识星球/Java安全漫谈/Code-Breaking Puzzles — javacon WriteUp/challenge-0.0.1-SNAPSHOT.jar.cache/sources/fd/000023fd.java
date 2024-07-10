package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/MissingRequestHeaderException.class */
public class MissingRequestHeaderException extends ServletRequestBindingException {
    private final String headerName;
    private final MethodParameter parameter;

    public MissingRequestHeaderException(String headerName, MethodParameter parameter) {
        super("");
        this.headerName = headerName;
        this.parameter = parameter;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Missing request header '" + this.headerName + "' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
    }

    public final String getHeaderName() {
        return this.headerName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}