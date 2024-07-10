package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/HandlerAdapter.class */
public interface HandlerAdapter {
    boolean supports(Object obj);

    @Nullable
    ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object obj) throws Exception;

    long getLastModified(HttpServletRequest httpServletRequest, Object obj);
}