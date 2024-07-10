package org.springframework.web.servlet.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/theme/FixedThemeResolver.class */
public class FixedThemeResolver extends AbstractThemeResolver {
    @Override // org.springframework.web.servlet.ThemeResolver
    public String resolveThemeName(HttpServletRequest request) {
        return getDefaultThemeName();
    }

    @Override // org.springframework.web.servlet.ThemeResolver
    public void setThemeName(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String themeName) {
        throw new UnsupportedOperationException("Cannot change theme - use a different theme resolution strategy");
    }
}