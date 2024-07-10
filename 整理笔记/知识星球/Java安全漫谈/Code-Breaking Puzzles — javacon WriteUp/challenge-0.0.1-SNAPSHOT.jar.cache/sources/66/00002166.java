package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServerHttpRequest.class */
public interface ServerHttpRequest extends HttpRequest, ReactiveHttpInputMessage {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServerHttpRequest$Builder.class */
    public interface Builder {
        Builder method(HttpMethod httpMethod);

        Builder uri(URI uri);

        Builder path(String str);

        Builder contextPath(String str);

        Builder header(String str, String str2);

        Builder headers(Consumer<HttpHeaders> consumer);

        Builder sslInfo(SslInfo sslInfo);

        ServerHttpRequest build();
    }

    String getId();

    RequestPath getPath();

    MultiValueMap<String, String> getQueryParams();

    MultiValueMap<String, HttpCookie> getCookies();

    @Nullable
    default InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Nullable
    default SslInfo getSslInfo() {
        return null;
    }

    default Builder mutate() {
        return new DefaultServerHttpRequestBuilder(this);
    }
}