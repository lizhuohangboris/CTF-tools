package org.springframework.http.client.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/ClientHttpResponse.class */
public interface ClientHttpResponse extends ReactiveHttpInputMessage {
    HttpStatus getStatusCode();

    int getRawStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();
}