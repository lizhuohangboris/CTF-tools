package org.springframework.web.context.request.async;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.WebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/async/WebAsyncUtils.class */
public abstract class WebAsyncUtils {
    public static final String WEB_ASYNC_MANAGER_ATTRIBUTE = WebAsyncManager.class.getName() + ".WEB_ASYNC_MANAGER";

    public static WebAsyncManager getAsyncManager(ServletRequest servletRequest) {
        WebAsyncManager asyncManager = null;
        Object asyncManagerAttr = servletRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE);
        if (asyncManagerAttr instanceof WebAsyncManager) {
            asyncManager = (WebAsyncManager) asyncManagerAttr;
        }
        if (asyncManager == null) {
            asyncManager = new WebAsyncManager();
            servletRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager);
        }
        return asyncManager;
    }

    public static WebAsyncManager getAsyncManager(WebRequest webRequest) {
        WebAsyncManager asyncManager = null;
        Object asyncManagerAttr = webRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, 0);
        if (asyncManagerAttr instanceof WebAsyncManager) {
            asyncManager = (WebAsyncManager) asyncManagerAttr;
        }
        if (asyncManager == null) {
            asyncManager = new WebAsyncManager();
            webRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager, 0);
        }
        return asyncManager;
    }

    public static AsyncWebRequest createAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
        return new StandardServletAsyncWebRequest(request, response);
    }
}