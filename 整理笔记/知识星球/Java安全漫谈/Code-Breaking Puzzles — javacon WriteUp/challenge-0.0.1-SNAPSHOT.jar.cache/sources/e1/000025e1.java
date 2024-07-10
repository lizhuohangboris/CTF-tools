package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleContextResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/AbstractLocaleContextResolver.class */
public abstract class AbstractLocaleContextResolver extends AbstractLocaleResolver implements LocaleContextResolver {
    @Nullable
    private TimeZone defaultTimeZone;

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = resolveLocaleContext(request).getLocale();
        return locale != null ? locale : request.getLocale();
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        setLocaleContext(request, response, locale != null ? new SimpleLocaleContext(locale) : null);
    }
}