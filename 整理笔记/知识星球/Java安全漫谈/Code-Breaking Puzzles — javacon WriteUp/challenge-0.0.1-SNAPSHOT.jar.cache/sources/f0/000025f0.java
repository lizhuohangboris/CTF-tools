package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/LastModified.class */
public interface LastModified {
    long getLastModified(HttpServletRequest httpServletRequest);
}