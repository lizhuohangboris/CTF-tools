package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/ClientHttpRequestExecution.class */
public interface ClientHttpRequestExecution {
    ClientHttpResponse execute(HttpRequest httpRequest, byte[] bArr) throws IOException;
}