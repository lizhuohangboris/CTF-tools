package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBufAllocator;
import java.net.URI;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/reactive/ReactorClientHttpConnector.class */
public class ReactorClientHttpConnector implements ClientHttpConnector {
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> {
        return client.compress(true);
    };
    private final HttpClient httpClient;

    public ReactorClientHttpConnector() {
        this.httpClient = defaultInitializer.apply(HttpClient.create());
    }

    public ReactorClientHttpConnector(ReactorResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
        this.httpClient = (HttpClient) defaultInitializer.andThen(mapper).apply(initHttpClient(factory));
    }

    private static HttpClient initHttpClient(ReactorResourceFactory resourceFactory) {
        ConnectionProvider provider = resourceFactory.getConnectionProvider();
        LoopResources resources = resourceFactory.getLoopResources();
        Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
        Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
        return HttpClient.create(provider).tcpConfiguration(tcpClient -> {
            return tcpClient.runOn(resources);
        });
    }

    public ReactorClientHttpConnector(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient is required");
        this.httpClient = httpClient;
    }

    @Override // org.springframework.http.client.reactive.ClientHttpConnector
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        if (!uri.isAbsolute()) {
            return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
        }
        return this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name())).uri(uri.toString()).send(request, outbound -> {
            return (Mono) requestCallback.apply(adaptRequest(method, uri, request, outbound));
        }).responseConnection(res, con -> {
            return Mono.just(adaptResponse(res, con.inbound(), con.outbound().alloc()));
        }).next();
    }

    private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
        return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
    }

    private ClientHttpResponse adaptResponse(HttpClientResponse response, NettyInbound nettyInbound, ByteBufAllocator allocator) {
        return new ReactorClientHttpResponse(response, nettyInbound, allocator);
    }
}