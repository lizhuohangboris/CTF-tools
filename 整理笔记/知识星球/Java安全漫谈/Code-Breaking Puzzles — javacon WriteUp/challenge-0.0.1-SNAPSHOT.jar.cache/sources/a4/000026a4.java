package org.springframework.web.servlet.support;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/RequestContext.class */
public class RequestContext {
    public static final String DEFAULT_THEME_NAME = "theme";
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = RequestContext.class.getName() + ".CONTEXT";
    protected static final boolean jstlPresent = ClassUtils.isPresent("javax.servlet.jsp.jstl.core.Config", RequestContext.class.getClassLoader());
    private HttpServletRequest request;
    @Nullable
    private HttpServletResponse response;
    @Nullable
    private Map<String, Object> model;
    private WebApplicationContext webApplicationContext;
    @Nullable
    private Locale locale;
    @Nullable
    private TimeZone timeZone;
    @Nullable
    private Theme theme;
    @Nullable
    private Boolean defaultHtmlEscape;
    @Nullable
    private Boolean responseEncodedHtmlEscape;
    private UrlPathHelper urlPathHelper;
    @Nullable
    private RequestDataValueProcessor requestDataValueProcessor;
    @Nullable
    private Map<String, Errors> errorsMap;

    public RequestContext(HttpServletRequest request) {
        this(request, null, null, null);
    }

    public RequestContext(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, null, null);
    }

    public RequestContext(HttpServletRequest request, @Nullable ServletContext servletContext) {
        this(request, null, servletContext, null);
    }

    public RequestContext(HttpServletRequest request, @Nullable Map<String, Object> model) {
        this(request, null, null, model);
    }

    public RequestContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable ServletContext servletContext, @Nullable Map<String, Object> model) {
        this.request = request;
        this.response = response;
        this.model = model;
        WebApplicationContext wac = (WebApplicationContext) request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (wac == null) {
            wac = RequestContextUtils.findWebApplicationContext(request, servletContext);
            if (wac == null) {
                throw new IllegalStateException("No WebApplicationContext found: not in a DispatcherServlet request and no ContextLoaderListener registered?");
            }
        }
        this.webApplicationContext = wac;
        Locale locale = null;
        TimeZone timeZone = null;
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver instanceof LocaleContextResolver) {
            LocaleContext localeContext = ((LocaleContextResolver) localeResolver).resolveLocaleContext(request);
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }
        } else if (localeResolver != null) {
            locale = localeResolver.resolveLocale(request);
        }
        this.locale = locale;
        this.timeZone = timeZone;
        this.defaultHtmlEscape = WebUtils.getDefaultHtmlEscape(this.webApplicationContext.getServletContext());
        this.responseEncodedHtmlEscape = WebUtils.getResponseEncodedHtmlEscape(this.webApplicationContext.getServletContext());
        this.urlPathHelper = new UrlPathHelper();
        if (this.webApplicationContext.containsBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
            this.requestDataValueProcessor = (RequestDataValueProcessor) this.webApplicationContext.getBean(RequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, RequestDataValueProcessor.class);
        }
    }

    public final HttpServletRequest getRequest() {
        return this.request;
    }

    @Nullable
    public final ServletContext getServletContext() {
        return this.webApplicationContext.getServletContext();
    }

    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }

    public final MessageSource getMessageSource() {
        return this.webApplicationContext;
    }

    @Nullable
    public final Map<String, Object> getModel() {
        return this.model;
    }

    public final Locale getLocale() {
        return this.locale != null ? this.locale : getFallbackLocale();
    }

    @Nullable
    public TimeZone getTimeZone() {
        return this.timeZone != null ? this.timeZone : getFallbackTimeZone();
    }

    protected Locale getFallbackLocale() {
        Locale locale;
        if (jstlPresent && (locale = JstlLocaleResolver.getJstlLocale(getRequest(), getServletContext())) != null) {
            return locale;
        }
        return getRequest().getLocale();
    }

    @Nullable
    protected TimeZone getFallbackTimeZone() {
        TimeZone timeZone;
        if (jstlPresent && (timeZone = JstlLocaleResolver.getJstlTimeZone(getRequest(), getServletContext())) != null) {
            return timeZone;
        }
        return null;
    }

    public void changeLocale(Locale locale) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(this.request);
        if (localeResolver == null) {
            throw new IllegalStateException("Cannot change locale if no LocaleResolver configured");
        }
        localeResolver.setLocale(this.request, this.response, locale);
        this.locale = locale;
    }

    public void changeLocale(Locale locale, TimeZone timeZone) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(this.request);
        if (!(localeResolver instanceof LocaleContextResolver)) {
            throw new IllegalStateException("Cannot change locale context if no LocaleContextResolver configured");
        }
        ((LocaleContextResolver) localeResolver).setLocaleContext(this.request, this.response, new SimpleTimeZoneAwareLocaleContext(locale, timeZone));
        this.locale = locale;
        this.timeZone = timeZone;
    }

    public Theme getTheme() {
        if (this.theme == null) {
            this.theme = RequestContextUtils.getTheme(this.request);
            if (this.theme == null) {
                this.theme = getFallbackTheme();
            }
        }
        return this.theme;
    }

    protected Theme getFallbackTheme() {
        ThemeSource themeSource = RequestContextUtils.getThemeSource(getRequest());
        if (themeSource == null) {
            themeSource = new ResourceBundleThemeSource();
        }
        Theme theme = themeSource.getTheme("theme");
        if (theme == null) {
            throw new IllegalStateException("No theme defined and no fallback theme found");
        }
        return theme;
    }

    public void changeTheme(@Nullable Theme theme) {
        ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(this.request);
        if (themeResolver == null) {
            throw new IllegalStateException("Cannot change theme if no ThemeResolver configured");
        }
        themeResolver.setThemeName(this.request, this.response, theme != null ? theme.getName() : null);
        this.theme = theme;
    }

    public void changeTheme(String themeName) {
        ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(this.request);
        if (themeResolver == null) {
            throw new IllegalStateException("Cannot change theme if no ThemeResolver configured");
        }
        themeResolver.setThemeName(this.request, this.response, themeName);
        this.theme = null;
    }

    public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
        this.defaultHtmlEscape = Boolean.valueOf(defaultHtmlEscape);
    }

    public boolean isDefaultHtmlEscape() {
        return this.defaultHtmlEscape != null && this.defaultHtmlEscape.booleanValue();
    }

    @Nullable
    public Boolean getDefaultHtmlEscape() {
        return this.defaultHtmlEscape;
    }

    public boolean isResponseEncodedHtmlEscape() {
        return this.responseEncodedHtmlEscape == null || this.responseEncodedHtmlEscape.booleanValue();
    }

    @Nullable
    public Boolean getResponseEncodedHtmlEscape() {
        return this.responseEncodedHtmlEscape;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Nullable
    public RequestDataValueProcessor getRequestDataValueProcessor() {
        return this.requestDataValueProcessor;
    }

    public String getContextPath() {
        return this.urlPathHelper.getOriginatingContextPath(this.request);
    }

    public String getContextUrl(String relativeUrl) {
        String url = getContextPath() + relativeUrl;
        if (this.response != null) {
            url = this.response.encodeURL(url);
        }
        return url;
    }

    public String getContextUrl(String relativeUrl, Map<String, ?> params) {
        UriTemplate template = new UriTemplate(getContextPath() + relativeUrl);
        String url = template.expand(params).toASCIIString();
        if (this.response != null) {
            url = this.response.encodeURL(url);
        }
        return url;
    }

    public String getPathToServlet() {
        String path = this.urlPathHelper.getOriginatingContextPath(this.request);
        if (StringUtils.hasText(this.urlPathHelper.getPathWithinServletMapping(this.request))) {
            path = path + this.urlPathHelper.getOriginatingServletPath(this.request);
        }
        return path;
    }

    public String getRequestUri() {
        return this.urlPathHelper.getOriginatingRequestUri(this.request);
    }

    public String getQueryString() {
        return this.urlPathHelper.getOriginatingQueryString(this.request);
    }

    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable Object[] args, String defaultMessage) {
        return getMessage(code, args, defaultMessage, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable List<?> args, String defaultMessage) {
        return getMessage(code, args != null ? args.toArray() : null, defaultMessage, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable Object[] args, String defaultMessage, boolean htmlEscape) {
        String msg = this.webApplicationContext.getMessage(code, args, defaultMessage, getLocale());
        if (msg == null) {
            return "";
        }
        return htmlEscape ? HtmlUtils.htmlEscape(msg) : msg;
    }

    public String getMessage(String code) throws NoSuchMessageException {
        return getMessage(code, (Object[]) null, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable Object[] args) throws NoSuchMessageException {
        return getMessage(code, args, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable List<?> args) throws NoSuchMessageException {
        return getMessage(code, args != null ? args.toArray() : null, isDefaultHtmlEscape());
    }

    public String getMessage(String code, @Nullable Object[] args, boolean htmlEscape) throws NoSuchMessageException {
        String msg = this.webApplicationContext.getMessage(code, args, getLocale());
        return htmlEscape ? HtmlUtils.htmlEscape(msg) : msg;
    }

    public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return getMessage(resolvable, isDefaultHtmlEscape());
    }

    public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException {
        String msg = this.webApplicationContext.getMessage(resolvable, getLocale());
        return htmlEscape ? HtmlUtils.htmlEscape(msg) : msg;
    }

    public String getThemeMessage(String code, String defaultMessage) {
        String msg = getTheme().getMessageSource().getMessage(code, null, defaultMessage, getLocale());
        return msg != null ? msg : "";
    }

    public String getThemeMessage(String code, @Nullable Object[] args, String defaultMessage) {
        String msg = getTheme().getMessageSource().getMessage(code, args, defaultMessage, getLocale());
        return msg != null ? msg : "";
    }

    public String getThemeMessage(String code, @Nullable List<?> args, String defaultMessage) {
        String msg = getTheme().getMessageSource().getMessage(code, args != null ? args.toArray() : null, defaultMessage, getLocale());
        return msg != null ? msg : "";
    }

    public String getThemeMessage(String code) throws NoSuchMessageException {
        return getTheme().getMessageSource().getMessage(code, null, getLocale());
    }

    public String getThemeMessage(String code, @Nullable Object[] args) throws NoSuchMessageException {
        return getTheme().getMessageSource().getMessage(code, args, getLocale());
    }

    public String getThemeMessage(String code, @Nullable List<?> args) throws NoSuchMessageException {
        return getTheme().getMessageSource().getMessage(code, args != null ? args.toArray() : null, getLocale());
    }

    public String getThemeMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return getTheme().getMessageSource().getMessage(resolvable, getLocale());
    }

    @Nullable
    public Errors getErrors(String name) {
        return getErrors(name, isDefaultHtmlEscape());
    }

    @Nullable
    public Errors getErrors(String name, boolean htmlEscape) {
        if (this.errorsMap == null) {
            this.errorsMap = new HashMap();
        }
        Errors errors = this.errorsMap.get(name);
        boolean put = false;
        if (errors == null) {
            errors = (Errors) getModelObject(BindingResult.MODEL_KEY_PREFIX + name);
            if (errors instanceof BindException) {
                errors = ((BindException) errors).getBindingResult();
            }
            if (errors == null) {
                return null;
            }
            put = true;
        }
        if (htmlEscape && !(errors instanceof EscapedErrors)) {
            errors = new EscapedErrors(errors);
            put = true;
        } else if (!htmlEscape && (errors instanceof EscapedErrors)) {
            errors = ((EscapedErrors) errors).getSource();
            put = true;
        }
        if (put) {
            this.errorsMap.put(name, errors);
        }
        return errors;
    }

    @Nullable
    public Object getModelObject(String modelName) {
        if (this.model != null) {
            return this.model.get(modelName);
        }
        return this.request.getAttribute(modelName);
    }

    public BindStatus getBindStatus(String path) throws IllegalStateException {
        return new BindStatus(this, path, isDefaultHtmlEscape());
    }

    public BindStatus getBindStatus(String path, boolean htmlEscape) throws IllegalStateException {
        return new BindStatus(this, path, htmlEscape);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/support/RequestContext$JstlLocaleResolver.class */
    public static class JstlLocaleResolver {
        private JstlLocaleResolver() {
        }

        @Nullable
        public static Locale getJstlLocale(HttpServletRequest request, @Nullable ServletContext servletContext) {
            Object localeObject = Config.get(request, "javax.servlet.jsp.jstl.fmt.locale");
            if (localeObject == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    localeObject = Config.get(session, "javax.servlet.jsp.jstl.fmt.locale");
                }
                if (localeObject == null && servletContext != null) {
                    localeObject = Config.get(servletContext, "javax.servlet.jsp.jstl.fmt.locale");
                }
            }
            if (localeObject instanceof Locale) {
                return (Locale) localeObject;
            }
            return null;
        }

        @Nullable
        public static TimeZone getJstlTimeZone(HttpServletRequest request, @Nullable ServletContext servletContext) {
            Object timeZoneObject = Config.get(request, "javax.servlet.jsp.jstl.fmt.timeZone");
            if (timeZoneObject == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    timeZoneObject = Config.get(session, "javax.servlet.jsp.jstl.fmt.timeZone");
                }
                if (timeZoneObject == null && servletContext != null) {
                    timeZoneObject = Config.get(servletContext, "javax.servlet.jsp.jstl.fmt.timeZone");
                }
            }
            if (timeZoneObject instanceof TimeZone) {
                return (TimeZone) timeZoneObject;
            }
            return null;
        }
    }
}