package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/ResponseExtractor.class */
public interface ResponseExtractor<T> {
    @Nullable
    T extractData(ClientHttpResponse clientHttpResponse) throws IOException;
}