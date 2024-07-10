package org.springframework.http.server.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServerHttpResponse.class */
public interface ServerHttpResponse extends ReactiveHttpOutputMessage {
    boolean setStatusCode(@Nullable HttpStatus httpStatus);

    @Nullable
    HttpStatus getStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();

    void addCookie(ResponseCookie responseCookie);
}