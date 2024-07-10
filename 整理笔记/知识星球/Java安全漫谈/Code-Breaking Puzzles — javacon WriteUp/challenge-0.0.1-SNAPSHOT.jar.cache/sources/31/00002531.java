package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/MultipartHttpServletRequest.class */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {
    @Nullable
    HttpMethod getRequestMethod();

    HttpHeaders getRequestHeaders();

    @Nullable
    HttpHeaders getMultipartHeaders(String str);
}