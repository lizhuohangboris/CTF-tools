package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.AsyncClientHttpRequest;

@FunctionalInterface
@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/AsyncRequestCallback.class */
public interface AsyncRequestCallback {
    void doWithRequest(AsyncClientHttpRequest asyncClientHttpRequest) throws IOException;
}