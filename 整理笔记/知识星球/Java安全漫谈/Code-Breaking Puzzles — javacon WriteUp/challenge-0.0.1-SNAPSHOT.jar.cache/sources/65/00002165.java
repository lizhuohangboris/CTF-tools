package org.springframework.http.server.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.nio.file.Path;
import java.util.List;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ReactorServerHttpResponse.class */
public class ReactorServerHttpResponse extends AbstractServerHttpResponse implements ZeroCopyHttpOutputMessage {
    private final HttpServerResponse response;

    public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(bufferFactory, new HttpHeaders(new NettyHeadersAdapter(response.responseHeaders())));
        Assert.notNull(response, "HttpServerResponse must not be null");
        this.response = response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        Integer statusCode = getStatusCodeValue();
        if (statusCode != null) {
            this.response.status(HttpResponseStatus.valueOf(statusCode.intValue()));
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        return this.response.send(toByteBufs(publisher)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
        return this.response.sendGroups(Flux.from(publisher).map(this::toByteBufs)).then();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                DefaultCookie defaultCookie = new DefaultCookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    defaultCookie.setMaxAge(httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    defaultCookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    defaultCookie.setPath(httpCookie.getPath());
                }
                defaultCookie.setSecure(httpCookie.isSecure());
                defaultCookie.setHttpOnly(httpCookie.isHttpOnly());
                this.response.addCookie(defaultCookie);
            }
        }
    }

    @Override // org.springframework.http.ZeroCopyHttpOutputMessage
    public Mono<Void> writeWith(Path file, long position, long count) {
        return doCommit(() -> {
            return this.response.sendFile(file, position, count).then();
        });
    }

    private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }
}