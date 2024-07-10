package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.thymeleaf.IEngineConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebExpressionContext.class */
public final class WebExpressionContext extends AbstractExpressionContext implements IWebContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;

    public WebExpressionContext(IEngineConfiguration configuration, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        super(configuration);
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public WebExpressionContext(IEngineConfiguration configuration, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Locale locale) {
        super(configuration, locale);
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public WebExpressionContext(IEngineConfiguration configuration, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Locale locale, Map<String, Object> variables) {
        super(configuration, locale, variables);
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpSession getSession() {
        return this.request.getSession(false);
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @Override // org.thymeleaf.context.IWebContext
    public ServletContext getServletContext() {
        return this.servletContext;
    }
}