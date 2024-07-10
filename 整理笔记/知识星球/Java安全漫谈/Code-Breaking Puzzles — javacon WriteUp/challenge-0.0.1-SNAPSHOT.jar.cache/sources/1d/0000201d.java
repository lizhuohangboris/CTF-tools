package org.springframework.http;

import java.net.URI;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpRequest.class */
public interface HttpRequest extends HttpMessage {
    String getMethodValue();

    URI getURI();

    @Nullable
    default HttpMethod getMethod() {
        return HttpMethod.resolve(getMethodValue());
    }
}