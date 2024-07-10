package org.springframework.http.server.reactive;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.net.ssl.SSLSession;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ReactorServerHttpRequest.class */
public class ReactorServerHttpRequest extends AbstractServerHttpRequest {
    private final HttpServerRequest request;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
        super(initUri(request), "", initHeaders(request));
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        return new URI(resolveBaseUrl(request).toString() + resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        int portIndex;
        String scheme = getScheme(request);
        String header = request.requestHeaders().get(HttpHeaderNames.HOST);
        if (header != null) {
            if (header.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                portIndex = header.indexOf(58, header.indexOf(93));
            } else {
                portIndex = header.indexOf(58);
            }
            if (portIndex != -1) {
                try {
                    return new URI(scheme, null, header.substring(0, portIndex), Integer.parseInt(header.substring(portIndex + 1)), null, null, null);
                } catch (NumberFormatException e) {
                    throw new URISyntaxException(header, "Unable to parse port", portIndex);
                }
            }
            return new URI(scheme, header, null, null);
        }
        InetSocketAddress localAddress = request.hostAddress();
        return new URI(scheme, null, localAddress.getHostString(), localAddress.getPort(), null, null, null);
    }

    private static String getScheme(HttpServerRequest request) {
        return request.scheme();
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        char c;
        String uri = request.uri();
        for (int i = 0; i < uri.length() && (c = uri.charAt(i)) != '/' && c != '?' && c != '#'; i++) {
            if (c == ':' && i + 2 < uri.length() && uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
                for (int j = i + 3; j < uri.length(); j++) {
                    char c2 = uri.charAt(j);
                    if (c2 == '/' || c2 == '?' || c2 == '#') {
                        return uri.substring(j);
                    }
                }
                return "";
            }
        }
        return uri;
    }

    private static HttpHeaders initHeaders(HttpServerRequest channel) {
        NettyHeadersAdapter headersMap = new NettyHeadersAdapter(channel.requestHeaders());
        return new HttpHeaders(headersMap);
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.request.method().name();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (CharSequence name : this.request.cookies().keySet()) {
            for (Cookie cookie : (Set) this.request.cookies().get(name)) {
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        SslHandler sslHandler = this.request.channel().pipeline().get(SslHandler.class);
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        ByteBufFlux retain = this.request.receive().retain();
        NettyDataBufferFactory nettyDataBufferFactory = this.bufferFactory;
        nettyDataBufferFactory.getClass();
        return retain.map(this::wrap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected String initId() {
        if (this.request instanceof Connection) {
            return this.request.channel().id().asShortText();
        }
        return null;
    }
}