package org.springframework.http.server.reactive;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter.class */
public class TomcatHttpHandlerAdapter extends ServletHttpHandlerAdapter {
    public TomcatHttpHandlerAdapter(HttpHandler httpHandler) {
        super(httpHandler);
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext asyncContext) throws IOException, URISyntaxException {
        Assert.notNull(getServletPath(), "Servlet path is not initialized");
        return new TomcatServerHttpRequest(request, asyncContext, getServletPath(), getDataBufferFactory(), getBufferSize());
    }

    @Override // org.springframework.http.server.reactive.ServletHttpHandlerAdapter
    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext asyncContext, ServletServerHttpRequest request) throws IOException {
        return new TomcatServerHttpResponse(response, asyncContext, getDataBufferFactory(), getBufferSize(), request);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter$TomcatServerHttpRequest.class */
    private static final class TomcatServerHttpRequest extends ServletServerHttpRequest {
        private static final Field COYOTE_REQUEST_FIELD;
        private final int bufferSize;
        private final DataBufferFactory factory;

        static {
            Field field = ReflectionUtils.findField(RequestFacade.class, "request");
            Assert.state(field != null, "Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible(field);
            COYOTE_REQUEST_FIELD = field;
        }

        TomcatServerHttpRequest(HttpServletRequest request, AsyncContext context, String servletPath, DataBufferFactory factory, int bufferSize) throws IOException, URISyntaxException {
            super(createTomcatHttpHeaders(request), request, context, servletPath, factory, bufferSize);
            this.factory = factory;
            this.bufferSize = bufferSize;
        }

        private static HttpHeaders createTomcatHttpHeaders(HttpServletRequest request) {
            Request connectorRequest = (Request) ReflectionUtils.getField(COYOTE_REQUEST_FIELD, request);
            Assert.state(connectorRequest != null, "No Tomcat connector request");
            org.apache.coyote.Request tomcatRequest = connectorRequest.getCoyoteRequest();
            TomcatHeadersAdapter headers = new TomcatHeadersAdapter(tomcatRequest.getMimeHeaders());
            return new HttpHeaders(headers);
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpRequest
        protected DataBuffer readFromInputStream() throws IOException {
            int capacity = this.bufferSize;
            DataBuffer dataBuffer = this.factory.allocateBuffer(capacity);
            try {
                ByteBuffer byteBuffer = dataBuffer.asByteBuffer(0, capacity);
                ServletRequest request = (ServletRequest) getNativeRequest();
                int read = ((CoyoteInputStream) request.getInputStream()).read(byteBuffer);
                logBytesRead(read);
                if (read > 0) {
                    dataBuffer.writePosition(read);
                    if (0 != 0) {
                        DataBufferUtils.release(dataBuffer);
                    }
                    return dataBuffer;
                } else if (read == -1) {
                    DataBuffer dataBuffer2 = EOF_BUFFER;
                    if (1 != 0) {
                        DataBufferUtils.release(dataBuffer);
                    }
                    return dataBuffer2;
                } else {
                    return null;
                }
            } finally {
                if (1 != 0) {
                    DataBufferUtils.release(dataBuffer);
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/TomcatHttpHandlerAdapter$TomcatServerHttpResponse.class */
    private static final class TomcatServerHttpResponse extends ServletServerHttpResponse {
        private static final Field COYOTE_RESPONSE_FIELD;

        static {
            Field field = ReflectionUtils.findField(ResponseFacade.class, StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME);
            Assert.state(field != null, "Incompatible Tomcat implementation");
            ReflectionUtils.makeAccessible(field);
            COYOTE_RESPONSE_FIELD = field;
        }

        TomcatServerHttpResponse(HttpServletResponse response, AsyncContext context, DataBufferFactory factory, int bufferSize, ServletServerHttpRequest request) throws IOException {
            super(createTomcatHttpHeaders(response), response, context, factory, bufferSize, request);
        }

        private static HttpHeaders createTomcatHttpHeaders(HttpServletResponse response) {
            Response connectorResponse = (Response) ReflectionUtils.getField(COYOTE_RESPONSE_FIELD, response);
            Assert.state(connectorResponse != null, "No Tomcat connector response");
            org.apache.coyote.Response tomcatResponse = connectorResponse.getCoyoteResponse();
            TomcatHeadersAdapter headers = new TomcatHeadersAdapter(tomcatResponse.getMimeHeaders());
            return new HttpHeaders(headers);
        }

        @Override // org.springframework.http.server.reactive.ServletServerHttpResponse, org.springframework.http.server.reactive.AbstractServerHttpResponse
        protected void applyHeaders() {
            HttpServletResponse response = (HttpServletResponse) getNativeResponse();
            MediaType contentType = getHeaders().getContentType();
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
            ((CoyoteOutputStream) response.getOutputStream()).write(input);
            return len;
        }
    }
}