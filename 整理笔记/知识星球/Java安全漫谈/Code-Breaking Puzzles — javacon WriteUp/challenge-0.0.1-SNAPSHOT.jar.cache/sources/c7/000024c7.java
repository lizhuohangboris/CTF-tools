package org.springframework.web.cors;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/CorsConfigurationSource.class */
public interface CorsConfigurationSource {
    @Nullable
    CorsConfiguration getCorsConfiguration(HttpServletRequest httpServletRequest);
}