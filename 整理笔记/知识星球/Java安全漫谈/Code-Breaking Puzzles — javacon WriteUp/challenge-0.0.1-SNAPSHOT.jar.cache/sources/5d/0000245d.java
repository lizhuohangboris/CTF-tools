package org.springframework.web.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/ResponseErrorHandler.class */
public interface ResponseErrorHandler {
    boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException;

    void handleError(ClientHttpResponse clientHttpResponse) throws IOException;

    default void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        handleError(response);
    }
}