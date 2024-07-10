package org.springframework.web.bind;

import org.springframework.beans.PropertyAccessor;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/MethodArgumentNotValidException.class */
public class MethodArgumentNotValidException extends Exception {
    private final MethodParameter parameter;
    private final BindingResult bindingResult;

    public MethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
        this.parameter = parameter;
        this.bindingResult = bindingResult;
    }

    public MethodParameter getParameter() {
        return this.parameter;
    }

    public BindingResult getBindingResult() {
        return this.bindingResult;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Validation failed for argument [").append(this.parameter.getParameterIndex()).append("] in ").append(this.parameter.getExecutable().toGenericString());
        if (this.bindingResult.getErrorCount() > 1) {
            sb.append(" with ").append(this.bindingResult.getErrorCount()).append(" errors");
        }
        sb.append(": ");
        for (ObjectError error : this.bindingResult.getAllErrors()) {
            sb.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(error).append("] ");
        }
        return sb.toString();
    }
}