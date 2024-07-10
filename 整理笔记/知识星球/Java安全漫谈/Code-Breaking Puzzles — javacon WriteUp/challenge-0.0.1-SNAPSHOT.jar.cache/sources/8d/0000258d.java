package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/RequestToViewNameTranslator.class */
public interface RequestToViewNameTranslator {
    @Nullable
    String getViewName(HttpServletRequest httpServletRequest) throws Exception;
}