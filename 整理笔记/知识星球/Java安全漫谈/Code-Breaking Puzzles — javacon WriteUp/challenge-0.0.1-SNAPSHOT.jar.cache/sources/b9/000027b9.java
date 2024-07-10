package org.thymeleaf.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IWebContext.class */
public interface IWebContext extends IContext {
    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    HttpSession getSession();

    ServletContext getServletContext();
}