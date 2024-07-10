package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/StreamingResponseBody.class */
public interface StreamingResponseBody {
    void writeTo(OutputStream outputStream) throws IOException;
}