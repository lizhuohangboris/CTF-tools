package org.springframework.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/ServerHttpRequest.class */
public interface ServerHttpRequest extends HttpRequest, HttpInputMessage {
    Principal getPrincipal();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse serverHttpResponse);
}