package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/i18n/LocaleContextHolder.class */
public final class LocaleContextHolder {
    private static final ThreadLocal<LocaleContext> localeContextHolder = new NamedThreadLocal("LocaleContext");
    private static final ThreadLocal<LocaleContext> inheritableLocaleContextHolder = new NamedInheritableThreadLocal("LocaleContext");
    @Nullable
    private static Locale defaultLocale;
    @Nullable
    private static TimeZone defaultTimeZone;

    private LocaleContextHolder() {
    }

    public static void resetLocaleContext() {
        localeContextHolder.remove();
        inheritableLocaleContextHolder.remove();
    }

    public static void setLocaleContext(@Nullable LocaleContext localeContext) {
        setLocaleContext(localeContext, false);
    }

    public static void setLocaleContext(@Nullable LocaleContext localeContext, boolean inheritable) {
        if (localeContext == null) {
            resetLocaleContext();
        } else if (inheritable) {
            inheritableLocaleContextHolder.set(localeContext);
            localeContextHolder.remove();
        } else {
            localeContextHolder.set(localeContext);
            inheritableLocaleContextHolder.remove();
        }
    }

    @Nullable
    public static LocaleContext getLocaleContext() {
        LocaleContext localeContext = localeContextHolder.get();
        if (localeContext == null) {
            localeContext = inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }

    public static void setLocale(@Nullable Locale locale) {
        setLocale(locale, false);
    }

    public static void setLocale(@Nullable Locale locale, boolean inheritable) {
        LocaleContext localeContext;
        LocaleContext localeContext2 = getLocaleContext();
        TimeZone timeZone = localeContext2 instanceof TimeZoneAwareLocaleContext ? ((TimeZoneAwareLocaleContext) localeContext2).getTimeZone() : null;
        if (timeZone != null) {
            localeContext = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        } else if (locale != null) {
            localeContext = new SimpleLocaleContext(locale);
        } else {
            localeContext = null;
        }
        setLocaleContext(localeContext, inheritable);
    }

    public static void setDefaultLocale(@Nullable Locale locale) {
        defaultLocale = locale;
    }

    public static Locale getLocale() {
        return getLocale(getLocaleContext());
    }

    public static Locale getLocale(@Nullable LocaleContext localeContext) {
        Locale locale;
        if (localeContext == null || (locale = localeContext.getLocale()) == null) {
            return defaultLocale != null ? defaultLocale : Locale.getDefault();
        }
        return locale;
    }

    public static void setTimeZone(@Nullable TimeZone timeZone) {
        setTimeZone(timeZone, false);
    }

    public static void setTimeZone(@Nullable TimeZone timeZone, boolean inheritable) {
        LocaleContext localeContext;
        LocaleContext localeContext2 = getLocaleContext();
        Locale locale = localeContext2 != null ? localeContext2.getLocale() : null;
        if (timeZone != null) {
            localeContext = new SimpleTimeZoneAwareLocaleContext(locale, timeZone);
        } else if (locale != null) {
            localeContext = new SimpleLocaleContext(locale);
        } else {
            localeContext = null;
        }
        setLocaleContext(localeContext, inheritable);
    }

    public static void setDefaultTimeZone(@Nullable TimeZone timeZone) {
        defaultTimeZone = timeZone;
    }

    public static TimeZone getTimeZone() {
        return getTimeZone(getLocaleContext());
    }

    public static TimeZone getTimeZone(@Nullable LocaleContext localeContext) {
        TimeZone timeZone;
        if (!(localeContext instanceof TimeZoneAwareLocaleContext) || (timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone()) == null) {
            return defaultTimeZone != null ? defaultTimeZone : TimeZone.getDefault();
        }
        return timeZone;
    }
}