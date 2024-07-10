package org.thymeleaf.spring5.webflow.view;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/webflow/view/AjaxThymeleafViewResolver.class */
public class AjaxThymeleafViewResolver extends ThymeleafViewResolver {
    private static final Logger vrlogger = LoggerFactory.getLogger(AjaxThymeleafViewResolver.class);
    private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    public void setAjaxHandler(AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.spring5.view.ThymeleafViewResolver, org.springframework.web.servlet.view.AbstractCachingViewResolver
    public View createView(String viewName, Locale locale) throws Exception {
        if (!canHandle(viewName, locale)) {
            return null;
        }
        if (this.ajaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " + AjaxThymeleafViewResolver.class.getSimpleName() + " instance is null.");
        }
        if (viewName.startsWith("redirect:")) {
            vrlogger.trace("[THYMELEAF] View {} is a redirect. An AJAX-enabled RedirectView implementation will be handling the request.", viewName);
            String redirectUrl = viewName.substring("redirect:".length());
            return new AjaxRedirectView(this.ajaxHandler, redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
        }
        View view = super.createView(viewName, locale);
        if (view instanceof AjaxEnabledView) {
            AjaxEnabledView ajaxEnabledView = (AjaxEnabledView) view;
            if (ajaxEnabledView.getAjaxHandler() == null && getAjaxHandler() != null) {
                ajaxEnabledView.setAjaxHandler(getAjaxHandler());
            }
        }
        return view;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/webflow/view/AjaxThymeleafViewResolver$AjaxRedirectView.class */
    private static class AjaxRedirectView extends RedirectView {
        private static final Logger vlogger = LoggerFactory.getLogger(AjaxRedirectView.class);
        private AjaxHandler ajaxHandler;

        AjaxRedirectView(AjaxHandler ajaxHandler, String redirectUrl, boolean redirectContextRelative, boolean redirectHttp10Compatible) {
            super(redirectUrl, redirectContextRelative, redirectHttp10Compatible);
            this.ajaxHandler = new SpringJavascriptAjaxHandler();
            this.ajaxHandler = ajaxHandler;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.web.servlet.view.RedirectView
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible) throws IOException {
            if (this.ajaxHandler == null) {
                throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " + AjaxThymeleafViewResolver.class.getSimpleName() + " instance is null.");
            }
            if (this.ajaxHandler.isAjaxRequest(request, response)) {
                if (vlogger.isTraceEnabled()) {
                    vlogger.trace("[THYMELEAF] RedirectView for URL \"{}\" is an AJAX request. AjaxHandler of class {} will be in charge of processing the request.", targetUrl, this.ajaxHandler.getClass().getName());
                }
                this.ajaxHandler.sendAjaxRedirect(targetUrl, request, response, false);
                return;
            }
            vlogger.trace("[THYMELEAF] RedirectView for URL \"{}\" is not an AJAX request. Request will be handled as a normal redirect", targetUrl);
            super.sendRedirect(request, response, targetUrl, http10Compatible);
        }
    }
}