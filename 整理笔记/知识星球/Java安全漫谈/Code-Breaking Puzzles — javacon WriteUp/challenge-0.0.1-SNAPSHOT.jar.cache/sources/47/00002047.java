package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/ClientHttpRequestInterceptor.class */
public interface ClientHttpRequestInterceptor {
    ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bArr, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException;
}