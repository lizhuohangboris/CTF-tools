package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.reactive.client.ContentChunk;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/JettyClientHttpConnector.class */
public class JettyClientHttpConnector implements ClientHttpConnector {
    private final HttpClient httpClient;
    private DataBufferFactory bufferFactory;

    public JettyClientHttpConnector() {
        this(new HttpClient());
    }

    public JettyClientHttpConnector(JettyResourceFactory resourceFactory, @Nullable Consumer<HttpClient> customizer) {
        this.bufferFactory = new DefaultDataBufferFactory();
        HttpClient httpClient = new HttpClient();
        httpClient.setExecutor(resourceFactory.getExecutor());
        httpClient.setByteBufferPool(resourceFactory.getByteBufferPool());
        httpClient.setScheduler(resourceFactory.getScheduler());
        if (customizer != null) {
            customizer.accept(httpClient);
        }
        this.httpClient = httpClient;
    }

    public JettyClientHttpConnector(HttpClient httpClient) {
        this.bufferFactory = new DefaultDataBufferFactory();
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        this.bufferFactory = bufferFactory;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        if (!uri.isAbsolute()) {
            return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
        }
        if (!this.httpClient.isStarted()) {
            try {
                this.httpClient.start();
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        }
        JettyClientHttpRequest clientHttpRequest = new JettyClientHttpRequest(this.httpClient.newRequest(uri).method(method.toString()), this.bufferFactory);
        return requestCallback.apply(clientHttpRequest).then(Mono.from(clientHttpRequest.getReactiveRequest().response(response, chunks -> {
            Flux<DataBuffer> content = Flux.from(chunks).map(this::toDataBuffer);
            return Mono.just(new JettyClientHttpResponse(response, content));
        })));
    }

    private DataBuffer toDataBuffer(ContentChunk chunk) {
        DataBuffer buffer = this.bufferFactory.allocateBuffer(chunk.buffer.capacity());
        buffer.write(chunk.buffer);
        chunk.callback.succeeded();
        return buffer;
    }
}