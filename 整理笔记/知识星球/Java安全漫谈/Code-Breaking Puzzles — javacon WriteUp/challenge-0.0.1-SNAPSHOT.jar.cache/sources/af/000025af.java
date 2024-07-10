package org.springframework.web.servlet.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/annotation/ViewControllerRegistration.class */
public class ViewControllerRegistration {
    private final String urlPath;
    private final ParameterizableViewController controller = new ParameterizableViewController();

    public ViewControllerRegistration(String urlPath) {
        Assert.notNull(urlPath, "'urlPath' is required.");
        this.urlPath = urlPath;
    }

    public ViewControllerRegistration setStatusCode(HttpStatus statusCode) {
        this.controller.setStatusCode(statusCode);
        return this;
    }

    public void setViewName(String viewName) {
        this.controller.setViewName(viewName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
        this.controller.setApplicationContext(applicationContext);
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