package org.springframework.web.servlet.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/RedirectViewControllerRegistration.class */
public class RedirectViewControllerRegistration {
    private final String urlPath;
    private final RedirectView redirectView;
    private final ParameterizableViewController controller = new ParameterizableViewController();

    public RedirectViewControllerRegistration(String urlPath, String redirectUrl) {
        Assert.notNull(urlPath, "'urlPath' is required.");
        Assert.notNull(redirectUrl, "'redirectUrl' is required.");
        this.urlPath = urlPath;
        this.redirectView = new RedirectView(redirectUrl);
        this.redirectView.setContextRelative(true);
        this.controller.setView(this.redirectView);
    }

    public RedirectViewControllerRegistration setStatusCode(HttpStatus statusCode) {
        Assert.isTrue(statusCode.is3xxRedirection(), "Not a redirect status code");
        this.redirectView.setStatusCode(statusCode);
        return this;
    }

    public RedirectViewControllerRegistration setContextRelative(boolean contextRelative) {
        this.redirectView.setContextRelative(contextRelative);
        return this;
    }

    public RedirectViewControllerRegistration setKeepQueryParams(boolean propagate) {
        this.redirectView.setPropagateQueryParams(propagate);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.controller.setApplicationContext(applicationContext);
        this.redirectView.setApplicationContext(applicationContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getUrlPath() {
        return this.urlPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ParameterizableViewController getViewController() {
        return this.controller;
    }
}