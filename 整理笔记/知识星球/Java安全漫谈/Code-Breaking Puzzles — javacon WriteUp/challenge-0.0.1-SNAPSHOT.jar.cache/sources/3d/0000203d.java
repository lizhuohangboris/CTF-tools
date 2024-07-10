package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AsyncClientHttpRequest.class */
public interface AsyncClientHttpRequest extends HttpRequest, HttpOutputMessage {
    ListenableFuture<ClientHttpResponse> executeAsync() throws IOException;
}