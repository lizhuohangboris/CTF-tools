package org.springframework.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import org.apache.coyote.http11.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/Netty4ClientHttpRequest.class */
class Netty4ClientHttpRequest extends AbstractAsyncClientHttpRequest implements ClientHttpRequest {
    private final Bootstrap bootstrap;
    private final URI uri;
    private final HttpMethod method;
    private final ByteBufOutputStream body = new ByteBufOutputStream(Unpooled.buffer(1024));

    public Netty4ClientHttpRequest(Bootstrap bootstrap, URI uri, HttpMethod method) {
        this.bootstrap = bootstrap;
        this.uri = uri;
        this.method = method;
    }

    @Override // org.springframework.http.HttpRequest
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.method.name();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // org.springframework.http.client.ClientHttpRequest
    public ClientHttpResponse execute() throws IOException {
        try {
            return executeAsync().get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during request execution", ex);
        } catch (ExecutionException ex2) {
            if (ex2.getCause() instanceof IOException) {
                throw ((IOException) ex2.getCause());
            }
            throw new IOException(ex2.getMessage(), ex2.getCause());
        }
    }

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.body;
    }

    @Override // org.springframework.http.client.AbstractAsyncClientHttpRequest
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        SettableListenableFuture<ClientHttpResponse> responseFuture = new SettableListenableFuture<>();
        ChannelFutureListener connectionListener = future -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                channel.pipeline().addLast(new ChannelHandler[]{new RequestExecuteHandler(responseFuture)});
                FullHttpRequest nettyRequest = createFullHttpRequest(headers);
                channel.writeAndFlush(nettyRequest);
                return;
            }
            responseFuture.setException(future.cause());
        };
        this.bootstrap.connect(this.uri.getHost(), getPort(this.uri)).addListener(connectionListener);
        return responseFuture;
    }

    private FullHttpRequest createFullHttpRequest(HttpHeaders headers) {
        io.netty.handler.codec.http.HttpMethod nettyMethod = io.netty.handler.codec.http.HttpMethod.valueOf(this.method.name());
        String authority = this.uri.getRawAuthority();
        String path = this.uri.toString().substring(this.uri.toString().indexOf(authority) + authority.length());
        DefaultFullHttpRequest defaultFullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, nettyMethod, path, this.body.buffer());
        defaultFullHttpRequest.headers().set("Host", this.uri.getHost() + ":" + getPort(this.uri));
        defaultFullHttpRequest.headers().set("Connection", Constants.CLOSE);
        headers.forEach(headerName, headerValues -> {
            defaultFullHttpRequest.headers().add(headerName, headerValues);
        });
        if (!defaultFullHttpRequest.headers().contains(HttpHeaders.CONTENT_LENGTH) && this.body.buffer().readableBytes() > 0) {
            defaultFullHttpRequest.headers().set(HttpHeaders.CONTENT_LENGTH, Integer.valueOf(this.body.buffer().readableBytes()));
        }
        return defaultFullHttpRequest;
    }

    private static int getPort(URI uri) {
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(uri.getScheme())) {
                port = 80;
            } else if ("https".equalsIgnoreCase(uri.getScheme())) {
                port = 443;
            }
        }
        return port;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/Netty4ClientHttpRequest$RequestExecuteHandler.class */
    private static class RequestExecuteHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
        private final SettableListenableFuture<ClientHttpResponse> responseFuture;

        public RequestExecuteHandler(SettableListenableFuture<ClientHttpResponse> responseFuture) {
            this.responseFuture = responseFuture;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void channelRead0(ChannelHandlerContext context, FullHttpResponse response) throws Exception {
            this.responseFuture.set(new Netty4ClientHttpResponse(context, response));
        }

        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
            this.responseFuture.setException(cause);
        }
    }
}