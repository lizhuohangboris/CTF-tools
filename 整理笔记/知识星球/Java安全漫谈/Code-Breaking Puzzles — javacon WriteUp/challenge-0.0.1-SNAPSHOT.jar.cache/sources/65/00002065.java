package org.springframework.http.client;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/Netty4ClientHttpResponse.class */
class Netty4ClientHttpResponse extends AbstractClientHttpResponse {
    private final ChannelHandlerContext context;
    private final FullHttpResponse nettyResponse;
    private final ByteBufInputStream body;
    @Nullable
    private volatile HttpHeaders headers;

    public Netty4ClientHttpResponse(ChannelHandlerContext context, FullHttpResponse nettyResponse) {
        Assert.notNull(context, "ChannelHandlerContext must not be null");
        Assert.notNull(nettyResponse, "FullHttpResponse must not be null");
        this.context = context;
        this.nettyResponse = nettyResponse;
        this.body = new ByteBufInputStream(this.nettyResponse.content());
        this.nettyResponse.retain();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public int getRawStatusCode() throws IOException {
        return this.nettyResponse.getStatus().code();
    }

    @Override // org.springframework.http.client.ClientHttpResponse
    public String getStatusText() throws IOException {
        return this.nettyResponse.getStatus().reasonPhrase();
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        HttpHeaders headers = this.headers;
        if (headers == null) {
            headers = new HttpHeaders();
            Iterator it = this.nettyResponse.headers().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) it.next();
                headers.add(entry.getKey(), entry.getValue());
            }
            this.headers = headers;
        }
        return headers;
    }

    @Override // org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        return this.body;
    }

    @Override // org.springframework.http.client.ClientHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.nettyResponse.release();
        this.context.close();
    }
}