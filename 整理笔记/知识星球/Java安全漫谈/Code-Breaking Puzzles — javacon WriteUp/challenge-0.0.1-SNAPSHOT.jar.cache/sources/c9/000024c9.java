package org.springframework.web.cors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/CorsUtils.class */
public abstract class CorsUtils {
    public static boolean isCorsRequest(HttpServletRequest request) {
        return request.getHeader("Origin") != null;
    }

    public static boolean isPreFlightRequest(HttpServletRequest request) {
        return isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod()) && request.getHeader("Access-Control-Request-Method") != null;
    }
}