package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.reactive.client.ContentChunk;
import org.eclipse.jetty.reactive.client.ReactiveRequest;
import org.eclipse.jetty.util.Callback;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/JettyClientHttpRequest.class */
public class JettyClientHttpRequest extends AbstractClientHttpRequest {
    private final Request jettyRequest;
    private final DataBufferFactory bufferFactory;
    @Nullable
    private ReactiveRequest reactiveRequest;

    public JettyClientHttpRequest(Request jettyRequest, DataBufferFactory bufferFactory) {
        this.jettyRequest = jettyRequest;
        this.bufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public HttpMethod getMethod() {
        HttpMethod method = HttpMethod.resolve(this.jettyRequest.getMethod());
        Assert.state(method != null, "Method must not be null");
        return method;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpRequest
    public URI getURI() {
        return this.jettyRequest.getURI();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return doCommit(this::completes);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        Flux<ContentChunk> chunks = Flux.from(body).map(this::toContentChunk);
        ReactiveRequest.Content content = ReactiveRequest.Content.fromPublisher(chunks, getContentType());
        this.reactiveRequest = ReactiveRequest.newBuilder(this.jettyRequest).content(content).build();
        return doCommit(this::completes);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        Flux<ContentChunk> chunks = Flux.from(body).flatMap(Function.identity()).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            DataBufferUtils.release(v0);
        }).map(this::toContentChunk);
        ReactiveRequest.Content content = ReactiveRequest.Content.fromPublisher(chunks, getContentType());
        this.reactiveRequest = ReactiveRequest.newBuilder(this.jettyRequest).content(content).build();
        return doCommit(this::completes);
    }

    private String getContentType() {
        MediaType contentType = getHeaders().getContentType();
        return contentType != null ? contentType.toString() : "application/octet-stream";
    }

    private Mono<Void> completes() {
        return Mono.empty();
    }

    private ContentChunk toContentChunk(final DataBuffer buffer) {
        return new ContentChunk(buffer.asByteBuffer(), new Callback() { // from class: org.springframework.http.client.reactive.JettyClientHttpRequest.1
            {
                JettyClientHttpRequest.this = this;
            }

            public void succeeded() {
                DataBufferUtils.release(buffer);
            }

            public void failed(Throwable x) {
                DataBufferUtils.release(buffer);
                throw Exceptions.propagate(x);
            }
        });
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyCookies() {
        Stream map = getCookies().values().stream().flatMap((v0) -> {
            return v0.stream();
        }).map(cookie -> {
            return new HttpCookie(cookie.getName(), cookie.getValue());
        });
        Request request = this.jettyRequest;
        request.getClass();
        map.forEach(this::cookie);
    }

    @Override // org.springframework.http.client.reactive.AbstractClientHttpRequest
    protected void applyHeaders() {
        HttpHeaders headers = getHeaders();
        headers.forEach(key, value -> {
            value.forEach(v -> {
                this.jettyRequest.header(key, v);
            });
        });
        if (!headers.containsKey(HttpHeaders.ACCEPT)) {
            this.jettyRequest.header(HttpHeaders.ACCEPT, "*/*");
        }
    }

    public ReactiveRequest getReactiveRequest() {
        if (this.reactiveRequest == null) {
            this.reactiveRequest = ReactiveRequest.newBuilder(this.jettyRequest).build();
        }
        return this.reactiveRequest;
    }
}