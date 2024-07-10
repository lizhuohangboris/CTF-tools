package org.springframework.web.cors;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/cors/CorsProcessor.class */
public interface CorsProcessor {
    boolean processRequest(@Nullable CorsConfiguration corsConfiguration, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
}