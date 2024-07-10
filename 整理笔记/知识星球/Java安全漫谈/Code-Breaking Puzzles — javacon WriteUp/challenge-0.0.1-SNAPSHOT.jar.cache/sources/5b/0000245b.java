package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/RequestCallback.class */
public interface RequestCallback {
    void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException;
}