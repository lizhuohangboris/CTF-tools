package org.thymeleaf.standard.expression;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/RestrictedRequestAccessUtils.class */
public final class RestrictedRequestAccessUtils {
    public static Object wrapRequestObject(Object obj) {
        if (obj == null || !(obj instanceof HttpServletRequest)) {
            return obj;
        }
        return new RestrictedRequestWrapper((HttpServletRequest) obj);
    }

    private RestrictedRequestAccessUtils() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/RestrictedRequestAccessUtils$RestrictedRequestWrapper.class */
    private static class RestrictedRequestWrapper extends HttpServletRequestWrapper {
        public RestrictedRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public String getParameter(String name) {
            throw createRestrictedParameterAccessException();
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Map getParameterMap() {
            throw createRestrictedParameterAccessException();
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public String[] getParameterValues(String name) {
            throw createRestrictedParameterAccessException();
        }

        @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
        public String getQueryString() {
            throw createRestrictedParameterAccessException();
        }

        private static TemplateProcessingException createRestrictedParameterAccessException() {
            return new TemplateProcessingException("Access to request parameters is forbidden in this context. Note some restrictions apply to variable access. For example, direct access to request parameters is forbidden in preprocessing and unescaped expressions, in TEXT template mode, in fragment insertion specifications and in some specific attribute processors.");
        }
    }
}