package org.springframework.web.servlet.i18n;

import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/LocaleChangeInterceptor.class */
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {
    public static final String DEFAULT_PARAM_NAME = "locale";
    @Nullable
    private String[] httpMethods;
    protected final Log logger = LogFactory.getLog(getClass());
    private String paramName = "locale";
    private boolean ignoreInvalidLocale = false;

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(@Nullable String... httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Nullable
    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    @Deprecated
    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        if (!languageTagCompliant) {
            throw new IllegalArgumentException("LocaleChangeInterceptor always accepts BCP 47 language tags");
        }
    }

    @Deprecated
    public boolean isLanguageTagCompliant() {
        return true;
    }

    @Override // org.springframework.web.servlet.handler.HandlerInterceptorAdapter, org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newLocale = request.getParameter(getParamName());
        if (newLocale != null && checkHttpMethod(request.getMethod())) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
                return true;
            } catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
                    return true;
                }
                throw ex;
            }
        }
        return true;
    }

    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty((Object[]) configuredMethods)) {
            return true;
        }
        for (String configuredMethod : configuredMethods) {
            if (configuredMethod.equalsIgnoreCase(currentMethod)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}