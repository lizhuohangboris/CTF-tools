package org.springframework.web.servlet.i18n;

import java.util.Locale;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/AbstractLocaleResolver.class */
public abstract class AbstractLocaleResolver implements LocaleResolver {
    @Nullable
    private Locale defaultLocale;

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }
}