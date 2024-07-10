package org.springframework.web.bind;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/MissingServletRequestParameterException.class */
public class MissingServletRequestParameterException extends ServletRequestBindingException {
    private final String parameterName;
    private final String parameterType;

    public MissingServletRequestParameterException(String parameterName, String parameterType) {
        super("");
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }

    @Override // org.springframework.web.util.NestedServletException, java.lang.Throwable
    public String getMessage() {
        return "Required " + this.parameterType + " parameter '" + this.parameterName + "' is not present";
    }

    public final String getParameterName() {
        return this.parameterName;
    }

    public final String getParameterType() {
        return this.parameterType;
    }
}