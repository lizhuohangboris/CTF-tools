package org.springframework.web.servlet.support;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/JstlUtils.class */
public abstract class JstlUtils {
    public static MessageSource getJstlAwareMessageSource(@Nullable ServletContext servletContext, MessageSource messageSource) {
        String jstlInitParam;
        if (servletContext != null && (jstlInitParam = servletContext.getInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext")) != null) {
            ResourceBundleMessageSource jstlBundleWrapper = new ResourceBundleMessageSource();
            jstlBundleWrapper.setBasename(jstlInitParam);
            jstlBundleWrapper.setParentMessageSource(messageSource);
            return jstlBundleWrapper;
        }
        return messageSource;
    }

    public static void exposeLocalizationContext(HttpServletRequest request, @Nullable MessageSource messageSource) {
        Locale jstlLocale = RequestContextUtils.getLocale(request);
        Config.set(request, "javax.servlet.jsp.jstl.fmt.locale", jstlLocale);
        TimeZone timeZone = RequestContextUtils.getTimeZone(request);
        if (timeZone != null) {
            Config.set(request, "javax.servlet.jsp.jstl.fmt.timeZone", timeZone);
        }
        if (messageSource != null) {
            LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, request);
            Config.set(request, "javax.servlet.jsp.jstl.fmt.localizationContext", jstlContext);
        }
    }

    public static void exposeLocalizationContext(RequestContext requestContext) {
        Config.set(requestContext.getRequest(), "javax.servlet.jsp.jstl.fmt.locale", requestContext.getLocale());
        TimeZone timeZone = requestContext.getTimeZone();
        if (timeZone != null) {
            Config.set(requestContext.getRequest(), "javax.servlet.jsp.jstl.fmt.timeZone", timeZone);
        }
        MessageSource messageSource = getJstlAwareMessageSource(requestContext.getServletContext(), requestContext.getMessageSource());
        LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, requestContext.getRequest());
        Config.set(requestContext.getRequest(), "javax.servlet.jsp.jstl.fmt.localizationContext", jstlContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/JstlUtils$SpringLocalizationContext.class */
    public static class SpringLocalizationContext extends LocalizationContext {
        private final MessageSource messageSource;
        private final HttpServletRequest request;

        public SpringLocalizationContext(MessageSource messageSource, HttpServletRequest request) {
            this.messageSource = messageSource;
            this.request = request;
        }

        public ResourceBundle getResourceBundle() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object lcObject = Config.get(session, "javax.servlet.jsp.jstl.fmt.localizationContext");
                if (lcObject instanceof LocalizationContext) {
                    ResourceBundle lcBundle = ((LocalizationContext) lcObject).getResourceBundle();
                    return new MessageSourceResourceBundle(this.messageSource, getLocale(), lcBundle);
                }
            }
            return new MessageSourceResourceBundle(this.messageSource, getLocale());
        }

        public Locale getLocale() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object localeObject = Config.get(session, "javax.servlet.jsp.jstl.fmt.locale");
                if (localeObject instanceof Locale) {
                    return (Locale) localeObject;
                }
            }
            return RequestContextUtils.getLocale(this.request);
        }
    }
}