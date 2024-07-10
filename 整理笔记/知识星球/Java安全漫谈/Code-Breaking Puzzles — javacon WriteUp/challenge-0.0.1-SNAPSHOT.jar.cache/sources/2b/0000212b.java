package org.springframework.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/ServletServerHttpResponse.class */
public class ServletServerHttpResponse implements ServerHttpResponse {
    private final HttpServletResponse servletResponse;
    private final HttpHeaders headers;
    private boolean headersWritten = false;
    private boolean bodyUsed = false;

    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull(servletResponse, "HttpServletResponse must not be null");
        this.servletResponse = servletResponse;
        this.headers = new ServletResponseHttpHeaders();
    }

    public HttpServletResponse getServletResponse() {
        return this.servletResponse;
    }

    @Override // org.springframework.http.server.ServerHttpResponse
    public void setStatusCode(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        this.servletResponse.setStatus(status.value());
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override // org.springframework.http.HttpOutputMessage
    public OutputStream getBody() throws IOException {
        this.bodyUsed = true;
        writeHeaders();
        return this.servletResponse.getOutputStream();
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Flushable
    public void flush() throws IOException {
        writeHeaders();
        if (this.bodyUsed) {
            this.servletResponse.flushBuffer();
        }
    }

    @Override // org.springframework.http.server.ServerHttpResponse, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        writeHeaders();
    }

    private void writeHeaders() {
        if (!this.headersWritten) {
            getHeaders().forEach(headerName, headerValues -> {
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue = (String) it.next();
                    this.servletResponse.addHeader(headerName, headerValue);
                }
            });
            if (this.servletResponse.getContentType() == null && this.headers.getContentType() != null) {
                this.servletResponse.setContentType(this.headers.getContentType().toString());
            }
            if (this.servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
                this.servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
            }
            this.headersWritten = true;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/ServletServerHttpResponse$ServletResponseHttpHeaders.class */
    private class ServletResponseHttpHeaders extends HttpHeaders {
        private static final long serialVersionUID = 3410708522401046302L;

        private ServletResponseHttpHeaders() {
        }

        @Override // org.springframework.http.HttpHeaders, java.util.Map
        public boolean containsKey(Object key) {
            return super.containsKey(key) || get(key) != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.HttpHeaders, org.springframework.util.MultiValueMap
        @Nullable
        public String getFirst(String headerName) {
            String value = ServletServerHttpResponse.this.servletResponse.getHeader(headerName);
            if (value != null) {
                return value;
            }
            return super.getFirst(headerName);
        }

        @Override // org.springframework.http.HttpHeaders, java.util.Map
        public List<String> get(Object key) {
            Assert.isInstanceOf(String.class, key, "Key must be a String-based header name");
            Collection<? extends String> values1 = ServletServerHttpResponse.this.servletResponse.getHeaders((String) key);
            if (ServletServerHttpResponse.this.headersWritten) {
                return new ArrayList(values1);
            }
            boolean isEmpty1 = CollectionUtils.isEmpty(values1);
            Collection<? extends String> values2 = super.get(key);
            boolean isEmpty2 = CollectionUtils.isEmpty(values2);
            if (isEmpty1 && isEmpty2) {
                return null;
            }
            List<String> values = new ArrayList<>();
            if (!isEmpty1) {
                values.addAll(values1);
            }
            if (!isEmpty2) {
                values.addAll(values2);
            }
            return values;
        }
    }
}