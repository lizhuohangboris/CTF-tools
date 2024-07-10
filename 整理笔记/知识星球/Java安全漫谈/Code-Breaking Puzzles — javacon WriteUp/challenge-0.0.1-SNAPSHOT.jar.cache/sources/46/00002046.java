package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/ClientHttpRequestFactory.class */
public interface ClientHttpRequestFactory {
    ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;
}