package org.springframework.web.context.support;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/HttpRequestHandlerServlet.class */
public class HttpRequestHandlerServlet extends HttpServlet {
    @Nullable
    private HttpRequestHandler target;

    @Override // javax.servlet.GenericServlet
    public void init() throws ServletException {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.target = (HttpRequestHandler) wac.getBean(getServletName(), HttpRequestHandler.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state(this.target != null, "No HttpRequestHandler available");
        LocaleContextHolder.setLocale(request.getLocale());
        try {
            this.target.handleRequest(request, response);
        } catch (HttpRequestMethodNotSupportedException ex) {
            String[] supportedMethods = ex.getSupportedMethods();
            if (supportedMethods != null) {
                response.setHeader(HttpHeaders.ALLOW, StringUtils.arrayToDelimitedString(supportedMethods, ", "));
            }
            response.sendError(405, ex.getMessage());
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}