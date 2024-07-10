package org.springframework.web;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/HttpRequestMethodNotSupportedException.class */
public class HttpRequestMethodNotSupportedException extends ServletException {
    private final String method;
    @Nullable
    private final String[] supportedMethods;

    public HttpRequestMethodNotSupportedException(String method) {
        this(method, (String[]) null);
    }

    public HttpRequestMethodNotSupportedException(String method, String msg) {
        this(method, null, msg);
    }

    public HttpRequestMethodNotSupportedException(String method, @Nullable Collection<String> supportedMethods) {
        this(method, supportedMethods != null ? StringUtils.toStringArray(supportedMethods) : null);
    }

    public HttpRequestMethodNotSupportedException(String method, @Nullable String[] supportedMethods) {
        this(method, supportedMethods, "Request method '" + method + "' not supported");
    }

    public HttpRequestMethodNotSupportedException(String method, @Nullable String[] supportedMethods, String msg) {
        super(msg);
        this.method = method;
        this.supportedMethods = supportedMethods;
    }

    public String getMethod() {
        return this.method;
    }

    @Nullable
    public String[] getSupportedMethods() {
        return this.supportedMethods;
    }

    @Nullable
    public Set<HttpMethod> getSupportedHttpMethods() {
        String[] strArr;
        if (this.supportedMethods == null) {
            return null;
        }
        List<HttpMethod> supportedMethods = new LinkedList<>();
        for (String value : this.supportedMethods) {
            HttpMethod resolved = HttpMethod.resolve(value);
            if (resolved != null) {
                supportedMethods.add(resolved);
            }
        }
        return EnumSet.copyOf((Collection) supportedMethods);
    }
}