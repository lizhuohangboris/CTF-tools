package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter.class */
public class JettyHttpHandlerAdapter extends ServletHttpHandlerAdapter {
    public JettyHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        Assert.notNull(getServletPath(), "Servlet path is not initialized");
        return new JettyServerHttpRequest(request, context, getServletPath(), getDataBufferFactory(), getBufferSize());
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        return new JettyServerHttpResponse(response, context, getDataBufferFactory(), getBufferSize(), request);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$JettyServerHttpRequest.class */
    private static final class JettyServerHttpRequest extends ServletServerHttpRequest {
        JettyServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
            super(createHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
        }

        private static HttpHeaders createHeaders(HttpServletRequest request) {
            HttpFields fields = ((Request) request).getMetaData().getFields();
            return new HttpHeaders(new JettyHeadersAdapter(fields));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/JettyHttpHandlerAdapter$JettyServerHttpResponse.class */
    private static final class JettyServerHttpResponse extends ServletServerHttpResponse {
        JettyServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(createHeaders(response), response, asyncContext, bufferFactory, bufferSize, request);
        }

        private static HttpHeaders createHeaders(HttpServletResponse response) {
            HttpFields fields = ((Response) response).getHttpFields();
            return new HttpHeaders(new JettyHeadersAdapter(fields));
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse, org.springframework.http.server.reactive.AbstractServerHttpResponse
        protected void applyHeaders() {
            MediaType contentType = getHeaders().getContentType();
            HttpServletResponse response = (HttpServletResponse) getNativeResponse();
            if (response.getContentType() == null && contentType != null) {
                response.setContentType(contentType.toString());
            }
            Charset charset = contentType != null ? contentType.getCharset() : null;
            if (response.getCharacterEncoding() == null && charset != null) {
                response.setCharacterEncoding(charset.name());
            }
            long contentLength = getHeaders().getContentLength();
            if (contentLength != -1) {
                response.setContentLengthLong(contentLength);
            }
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse
        protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
            ByteBuffer input = dataBuffer.asByteBuffer();
            int len = input.remaining();
            ServletResponse response = (ServletResponse) getNativeResponse();
            response.getOutputStream().write(input);
            return len;
        }
    }
}