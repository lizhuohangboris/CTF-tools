package org.springframework.boot.web.servlet.error;

import java.util.Map;
import org.springframework.web.context.request.WebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/error/ErrorAttributes.class */
public interface ErrorAttributes {
    Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace);

    Throwable getError(WebRequest webRequest);
}