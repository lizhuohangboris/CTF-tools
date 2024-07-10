package org.springframework.web.servlet.theme;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/theme/ThemeChangeInterceptor.class */
public class ThemeChangeInterceptor extends HandlerInterceptorAdapter {
    public static final String DEFAULT_PARAM_NAME = "theme";
    private String paramName = "theme";

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    @Override // org.springframework.web.servlet.handler.HandlerInterceptorAdapter, org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        String newTheme = request.getParameter(this.paramName);
        if (newTheme != null) {
            ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
            if (themeResolver == null) {
                throw new IllegalStateException("No ThemeResolver found: not in a DispatcherServlet request?");
            }
            themeResolver.setThemeName(request, response, newTheme);
            return true;
        }
        return true;
    }
}