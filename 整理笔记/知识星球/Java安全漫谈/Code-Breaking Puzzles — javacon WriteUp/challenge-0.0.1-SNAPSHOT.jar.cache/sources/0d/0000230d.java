package org.springframework.ui.context.support;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ui/context/support/DelegatingThemeSource.class */
public class DelegatingThemeSource implements HierarchicalThemeSource {
    @Nullable
    private ThemeSource parentThemeSource;

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    public void setParentThemeSource(@Nullable ThemeSource parentThemeSource) {
        this.parentThemeSource = parentThemeSource;
    }

    @Override // org.springframework.ui.context.HierarchicalThemeSource
    @Nullable
    public ThemeSource getParentThemeSource() {
        return this.parentThemeSource;
    }

    @Override // org.springframework.ui.context.ThemeSource
    @Nullable
    public Theme getTheme(String themeName) {
        if (this.parentThemeSource != null) {
            return this.parentThemeSource.getTheme(themeName);
        }
        return null;
    }
}