package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/MatchableHandlerMapping.class */
public interface MatchableHandlerMapping extends HandlerMapping {
    @Nullable
    RequestMatchResult match(HttpServletRequest httpServletRequest, String str);
}