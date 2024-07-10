package org.springframework.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/theme/SessionThemeResolver.class */
public class SessionThemeResolver extends AbstractThemeResolver {
    public static final String THEME_SESSION_ATTRIBUTE_NAME = SessionThemeResolver.class.getName() + ".THEME";

    @Override // org.springframework.web.servlet.ThemeResolver
    public String resolveThemeName(HttpServletRequest request) {
        String themeName = (String) WebUtils.getSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME);
        return themeName != null ? themeName : getDefaultThemeName();
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        WebUtils.setSessionAttribute(request, THEME_SESSION_ATTRIBUTE_NAME, StringUtils.hasText(themeName) ? themeName : null);
    }
}