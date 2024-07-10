package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.Request;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/AbstractSpringPreparerFactory.class */
public abstract class AbstractSpringPreparerFactory implements PreparerFactory {
    protected abstract ViewPreparer getPreparer(String str, WebApplicationContext webApplicationContext) throws TilesException;

    public ViewPreparer getPreparer(String name, Request context) {
        WebApplicationContext webApplicationContext = (WebApplicationContext) context.getContext("request").get(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            webApplicationContext = (WebApplicationContext) context.getContext("application").get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (webApplicationContext == null) {
                throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
            }
        }
        return getPreparer(name, webApplicationContext);
    }
}