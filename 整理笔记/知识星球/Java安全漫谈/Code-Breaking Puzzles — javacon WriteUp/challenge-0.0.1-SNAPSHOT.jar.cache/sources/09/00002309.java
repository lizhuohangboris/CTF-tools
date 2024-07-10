package org.springframework.ui.context;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ui/context/HierarchicalThemeSource.class */
public interface HierarchicalThemeSource extends ThemeSource {
    void setParentThemeSource(@Nullable ThemeSource themeSource);

    @Nullable
    ThemeSource getParentThemeSource();
}