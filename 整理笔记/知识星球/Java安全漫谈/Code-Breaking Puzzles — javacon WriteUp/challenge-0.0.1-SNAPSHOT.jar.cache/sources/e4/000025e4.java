package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/CookieLocaleResolver.class */
public class CookieLocaleResolver extends CookieGenerator implements LocaleContextResolver {
    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    public static final String TIME_ZONE_REQUEST_ATTRIBUTE_NAME = CookieLocaleResolver.class.getName() + ".TIME_ZONE";
    public static final String DEFAULT_COOKIE_NAME = CookieLocaleResolver.class.getName() + ".LOCALE";
    private boolean languageTagCompliant = true;
    @Nullable
    private Locale defaultLocale;
    @Nullable
    private TimeZone defaultTimeZone;

    public CookieLocaleResolver() {
        setCookieName(DEFAULT_COOKIE_NAME);
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    public boolean isLanguageTagCompliant() {
        return this.languageTagCompliant;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setDefaultTimeZone(@Nullable TimeZone defaultTimeZone) {
        this.defaultTimeZone = defaultTimeZone;
    }

    @Nullable
    protected TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() { // from class: org.springframework.web.servlet.i18n.CookieLocaleResolver.1
            @Override // org.springframework.context.i18n.LocaleContext
            @Nullable
            public Locale getLocale() {
                return (Locale) request.getAttribute(CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
            @Nullable
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(CookieLocaleResolver.TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        Cookie cookie;
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Locale locale = null;
            TimeZone timeZone = null;
            String cookieName = getCookieName();
            if (cookieName != null && (cookie = WebUtils.getCookie(request, cookieName)) != null) {
                String value = cookie.getValue();
                String localePart = value;
                String timeZonePart = null;
                int separatorIndex = localePart.indexOf(47);
                if (separatorIndex == -1) {
                    separatorIndex = localePart.indexOf(32);
                }
                if (separatorIndex >= 0) {
                    localePart = value.substring(0, separatorIndex);
                    timeZonePart = value.substring(separatorIndex + 1);
                }
                try {
                    locale = !"-".equals(localePart) ? parseLocaleValue(localePart) : null;
                    if (timeZonePart != null) {
                        timeZone = StringUtils.parseTimeZoneString(timeZonePart);
                    }
                } catch (IllegalArgumentException ex) {
                    String cookieDescription = "invalid locale cookie '" + cookieName + "': [" + value + "] due to: " + ex.getMessage();
                    if (request.getAttribute("javax.servlet.error.exception") != null) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Ignoring " + cookieDescription);
                        }
                    } else {
                        throw new IllegalStateException("Encountered " + cookieDescription);
                    }
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale + "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }
            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : determineDefaultLocale(request));
            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone != null ? timeZone : determineDefaultTimeZone(request));
        }
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        setLocaleContext(request, response, locale != null ? new SimpleLocaleContext(locale) : null);
    }

    @Override // org.springframework.web.servlet.LocaleContextResolver
    public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable LocaleContext localeContext) {
        Assert.notNull(response, "HttpServletResponse is required for CookieLocaleResolver");
        Locale locale = null;
        TimeZone timeZone = null;
        if (localeContext != null) {
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }
            addCookie(response, (locale != null ? toLocaleValue(locale) : "-") + (timeZone != null ? '/' + timeZone.getID() : ""));
        } else {
            removeCookie(response);
        }
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : determineDefaultLocale(request));
        request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone != null ? timeZone : determineDefaultTimeZone(request));
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }

    protected String toLocaleValue(Locale locale) {
        return isLanguageTagCompliant() ? locale.toLanguageTag() : locale.toString();
    }

    @Nullable
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