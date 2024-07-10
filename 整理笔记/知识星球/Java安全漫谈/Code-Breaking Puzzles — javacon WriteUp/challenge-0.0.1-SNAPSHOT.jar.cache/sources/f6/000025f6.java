package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/SimpleControllerHandlerAdapter.class */
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    @Override // org.springframework.web.servlet.HandlerAdapter
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    @Nullable
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ((Controller) handler).handleRequest(request, response);
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public long getLastModified(HttpServletRequest request, Object handler) {
        if (handler instanceof LastModified) {
            return ((LastModified) handler).getLastModified(request);
        }
        return -1L;
    }
}