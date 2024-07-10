package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/SessionLocaleResolver.class */
public class SessionLocaleResolver extends AbstractLocaleContextResolver {
    public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";
    private String localeAttributeName = LOCALE_SESSION_ATTRIBUTE_NAME;
    private String timeZoneAttributeName = TIME_ZONE_SESSION_ATTRIBUTE_NAME;

    public void setLocaleAttributeName(String localeAttributeName) {
        this.localeAttributeName = localeAttributeName;
    }

    public void setTimeZoneAttributeName(String timeZoneAttributeName) {
        this.timeZoneAttributeName = timeZoneAttributeName;
    }

    @Override // org.springframework.web.servlet.i18n.AbstractLocaleContextResolver, org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, this.localeAttributeName);
        if (locale == null) {
            locale = determineDefaultLocale(request);
        }
        return locale;
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.servlet.i18n.SessionLocaleResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            public Locale getLocale() {
                Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.this.localeAttributeName);
                if (locale == null) {
                    locale = SessionLocaleResolver.this.determineDefaultLocale(request);
                }
                return locale;
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            @Nullable
            public TimeZone getTimeZone() {
                TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, SessionLocaleResolver.this.timeZoneAttributeName);
                if (timeZone == null) {
                    timeZone = SessionLocaleResolver.this.determineDefaultTimeZone(request);
                }
                return timeZone;
            }
        };
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }
        }
        WebUtils.setSessionAttribute(request, this.localeAttributeName, locale);
        WebUtils.setSessionAttribute(request, this.timeZoneAttributeName, timeZone);
    }

    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }

    @Nullable
    protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
        return getDefaultTimeZone();
    }
}