package org.springframework.web.servlet.theme;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/theme/CookieThemeResolver.class */
public class CookieThemeResolver extends CookieGenerator implements ThemeResolver {
    public static final String ORIGINAL_DEFAULT_THEME_NAME = "theme";
    public static final String THEME_REQUEST_ATTRIBUTE_NAME = CookieThemeResolver.class.getName() + ".THEME";
    public static final String DEFAULT_COOKIE_NAME = CookieThemeResolver.class.getName() + ".THEME";
    private String defaultThemeName = "theme";

    public CookieThemeResolver() {
        setCookieName(DEFAULT_COOKIE_NAME);
    }

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    public String getDefaultThemeName() {
        return this.defaultThemeName;
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public String resolveThemeName(HttpServletRequest request) {
        Cookie cookie;
        String themeName = (String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME);
        if (themeName != null) {
            return themeName;
        }
        String cookieName = getCookieName();
        if (cookieName != null && (cookie = WebUtils.getCookie(request, cookieName)) != null) {
            String value = cookie.getValue();
            if (StringUtils.hasText(value)) {
                themeName = value;
            }
        }
        if (themeName == null) {
            themeName = getDefaultThemeName();
        }
        request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
        return themeName;
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        Assert.notNull(response, "HttpServletResponse is required for CookieThemeResolver");
        if (StringUtils.hasText(themeName)) {
            request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
            addCookie(response, themeName);
            return;
        }
        request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, getDefaultThemeName());
        removeCookie(response);
    }
}