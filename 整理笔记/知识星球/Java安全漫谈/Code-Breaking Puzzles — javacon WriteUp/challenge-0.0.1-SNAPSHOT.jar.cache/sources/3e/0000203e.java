package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AsyncClientHttpRequestExecution.class */
public interface AsyncClientHttpRequestExecution {
    ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest httpRequest, byte[] bArr) throws IOException;
}