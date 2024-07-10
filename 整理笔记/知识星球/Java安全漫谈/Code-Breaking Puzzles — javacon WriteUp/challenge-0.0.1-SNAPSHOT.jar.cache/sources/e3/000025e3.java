package org.springframework.web.servlet.i18n;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/i18n/AcceptHeaderLocaleResolver.class */
public class AcceptHeaderLocaleResolver implements LocaleResolver {
    private final List<Locale> supportedLocales = new ArrayList(4);
    @Nullable
    private Locale defaultLocale;

    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public Locale resolveLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && request.getHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
            return defaultLocale;
        }
        Locale requestLocale = request.getLocale();
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return defaultLocale != null ? defaultLocale : requestLocale;
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration<Locale> requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    return locale;
                }
            } else if (languageMatch == null) {
                Iterator<Locale> it = supportedLocales.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Locale candidate = it.next();
                        if (!StringUtils.hasLength(candidate.getCountry()) && candidate.getLanguage().equals(locale.getLanguage())) {
                            languageMatch = candidate;
                            break;
                        }
                    }
                }
            }
        }
        return languageMatch;
    }

    @Override // org.springframework.web.servlet.LocaleResolver
    public void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale resolution strategy");
    }
}