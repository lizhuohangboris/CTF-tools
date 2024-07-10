package org.springframework.web.bind;

import org.springframework.core.MethodParameter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/MissingMatrixVariableException.class */
public class MissingMatrixVariableException extends ServletRequestBindingException {
    private final String variableName;
    private final MethodParameter parameter;

    public MissingMatrixVariableException(String variableName, MethodParameter parameter) {
        super("");
        this.variableName = variableName;
        this.parameter = parameter;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Missing matrix variable '" + this.variableName + "' for method parameter of type " + this.parameter.getNestedParameterType().getSimpleName();
    }

    public final String getVariableName() {
        return this.variableName;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }
}