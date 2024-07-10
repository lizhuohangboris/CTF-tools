package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/JspAwareRequestContext.class */
public class JspAwareRequestContext extends RequestContext {
    private PageContext pageContext;

    public JspAwareRequestContext(PageContext pageContext) {
        this(pageContext, null);
    }

    public JspAwareRequestContext(PageContext pageContext, @Nullable Map<String, Object> model) {
        super((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse(), pageContext.getServletContext(), model);
        this.pageContext = pageContext;
    }

    protected final PageContext getPageContext() {
        return this.pageContext;
    }

    @Override // org.springframework.web.servlet.support.RequestContext
    protected Locale getFallbackLocale() {
        Locale locale;
        if (jstlPresent && (locale = JstlPageLocaleResolver.getJstlLocale(getPageContext())) != null) {
            return locale;
        }
        return getRequest().getLocale();
    }

    @Override // org.springframework.web.servlet.support.RequestContext
    protected TimeZone getFallbackTimeZone() {
        TimeZone timeZone;
        if (jstlPresent && (timeZone = JstlPageLocaleResolver.getJstlTimeZone(getPageContext())) != null) {
            return timeZone;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/JspAwareRequestContext$JstlPageLocaleResolver.class */
    private static class JstlPageLocaleResolver {
        private JstlPageLocaleResolver() {
        }

        @Nullable
        public static Locale getJstlLocale(PageContext pageContext) {
            Object localeObject = Config.find(pageContext, "javax.servlet.jsp.jstl.fmt.locale");
            if (localeObject instanceof Locale) {
                return (Locale) localeObject;
            }
            return null;
        }

        @Nullable
        public static TimeZone getJstlTimeZone(PageContext pageContext) {
            Object timeZoneObject = Config.find(pageContext, "javax.servlet.jsp.jstl.fmt.timeZone");
            if (timeZoneObject instanceof TimeZone) {
                return (TimeZone) timeZoneObject;
            }
            return null;
        }
    }
}