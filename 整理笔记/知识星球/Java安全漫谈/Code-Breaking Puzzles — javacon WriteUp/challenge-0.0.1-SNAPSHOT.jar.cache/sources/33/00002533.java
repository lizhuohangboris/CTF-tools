package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/MultipartResolver.class */
public interface MultipartResolver {
    boolean isMultipart(HttpServletRequest httpServletRequest);

    MultipartHttpServletRequest resolveMultipart(HttpServletRequest httpServletRequest) throws MultipartException;

    void cleanupMultipart(MultipartHttpServletRequest multipartHttpServletRequest);
}