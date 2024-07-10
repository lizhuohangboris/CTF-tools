package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/FixedLocaleResolver.class */
public class FixedLocaleResolver extends AbstractLocaleContextResolver {
    public FixedLocaleResolver() {
        setDefaultLocale(Locale.getDefault());
    }

    public FixedLocaleResolver(Locale locale) {
        setDefaultLocale(locale);
    }

    public FixedLocaleResolver(Locale locale, TimeZone timeZone) {
        setDefaultLocale(locale);
        setDefaultTimeZone(timeZone);
    }

    @Override // org.springframework.web.servlet.i18n.AbstractLocaleContextResolver, org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = getDefaultLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.servlet.i18n.FixedLocaleResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            @Nullable
            public Locale getLocale() {
                return FixedLocaleResolver.this.getDefaultLocale();
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            public TimeZone getTimeZone() {
                return FixedLocaleResolver.this.getDefaultTimeZone();
            }
        };
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        throw new UnsupportedOperationException("Cannot change fixed locale - use a different locale resolution strategy");
    }
}