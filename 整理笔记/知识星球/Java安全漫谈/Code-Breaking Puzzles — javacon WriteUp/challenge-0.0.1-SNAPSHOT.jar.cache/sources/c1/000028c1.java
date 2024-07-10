package org.thymeleaf.spring5.context.webmvc;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.context.Theme;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webmvc/SpringWebMvcThymeleafRequestContext.class */
public class SpringWebMvcThymeleafRequestContext implements IThymeleafRequestContext {
    private final RequestContext requestContext;
    private final HttpServletRequest httpServletRequest;
    private final SpringWebMvcThymeleafRequestDataValueProcessor thymeleafRequestDataValueProcessor;

    public SpringWebMvcThymeleafRequestContext(RequestContext requestContext, HttpServletRequest httpServletRequest) {
        Validate.notNull(requestContext, "Spring WebMVC RequestContext cannot be null");
        Validate.notNull(httpServletRequest, "HttpServletRequest cannot be null");
        this.requestContext = requestContext;
        this.httpServletRequest = httpServletRequest;
        this.thymeleafRequestDataValueProcessor = new SpringWebMvcThymeleafRequestDataValueProcessor(this.requestContext.getRequestDataValueProcessor(), httpServletRequest);
    }

    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public MessageSource getMessageSource() {
        return this.requestContext.getMessageSource();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Map<String, Object> getModel() {
        return this.requestContext.getModel();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Locale getLocale() {
        return this.requestContext.getLocale();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public TimeZone getTimeZone() {
        return this.requestContext.getTimeZone();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public void changeLocale(Locale locale) {
        this.requestContext.changeLocale(locale);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public void changeLocale(Locale locale, TimeZone timeZone) {
        this.requestContext.changeLocale(locale, timeZone);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public void setDefaultHtmlEscape(boolean defaultHtmlEscape) {
        this.requestContext.setDefaultHtmlEscape(defaultHtmlEscape);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public boolean isDefaultHtmlEscape() {
        return this.requestContext.isDefaultHtmlEscape();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Boolean getDefaultHtmlEscape() {
        return this.requestContext.getDefaultHtmlEscape();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getContextPath() {
        return this.requestContext.getContextPath();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getContextUrl(String relativeUrl) {
        return this.requestContext.getContextUrl(relativeUrl);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getContextUrl(String relativeUrl, Map<String, ?> params) {
        return this.requestContext.getContextUrl(relativeUrl, params);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getRequestPath() {
        return this.requestContext.getRequestUri();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getQueryString() {
        return this.requestContext.getQueryString();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, String defaultMessage) {
        return this.requestContext.getMessage(code, defaultMessage);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, Object[] args, String defaultMessage) {
        return this.requestContext.getMessage(code, args, defaultMessage);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, List<?> args, String defaultMessage) {
        return this.requestContext.getMessage(code, args, defaultMessage);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, Object[] args, String defaultMessage, boolean htmlEscape) {
        return this.requestContext.getMessage(code, args, defaultMessage, htmlEscape);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code) throws NoSuchMessageException {
        return this.requestContext.getMessage(code);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, Object[] args) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, List<?> args) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(String code, Object[] args, boolean htmlEscape) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args, htmlEscape);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return this.requestContext.getMessage(resolvable);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException {
        return this.requestContext.getMessage(resolvable, htmlEscape);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Optional<Errors> getErrors(String name) {
        return Optional.ofNullable(this.requestContext.getErrors(name));
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Optional<Errors> getErrors(String name, boolean htmlEscape) {
        return Optional.ofNullable(this.requestContext.getErrors(name, htmlEscape));
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public Theme getTheme() {
        return this.requestContext.getTheme();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public IThymeleafRequestDataValueProcessor getRequestDataValueProcessor() {
        return this.thymeleafRequestDataValueProcessor;
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public IThymeleafBindStatus getBindStatus(String path) throws IllegalStateException {
        return (IThymeleafBindStatus) Optional.ofNullable(this.requestContext.getBindStatus(path)).map(SpringWebMvcThymeleafBindStatus::new).orElse(null);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestContext
    public IThymeleafBindStatus getBindStatus(String path, boolean htmlEscape) throws IllegalStateException {
        return (IThymeleafBindStatus) Optional.ofNullable(this.requestContext.getBindStatus(path, htmlEscape)).map(SpringWebMvcThymeleafBindStatus::new).orElse(null);
    }

    public String toString() {
        return this.requestContext.toString();
    }
}