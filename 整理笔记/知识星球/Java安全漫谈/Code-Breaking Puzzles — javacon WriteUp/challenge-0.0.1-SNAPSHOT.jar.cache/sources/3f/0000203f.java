package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/AsyncClientHttpRequestFactory.class */
public interface AsyncClientHttpRequestFactory {
    AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException;
}