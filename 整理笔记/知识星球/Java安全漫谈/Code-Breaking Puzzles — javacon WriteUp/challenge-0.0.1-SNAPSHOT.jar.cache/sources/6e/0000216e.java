package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpRequest.class */
public class ServletServerHttpRequest extends AbstractServerHttpRequest {
    static final DataBuffer EOF_BUFFER = new DefaultDataBufferFactory().allocateBuffer(0);
    private final HttpServletRequest request;
    private final RequestBodyPublisher bodyPublisher;
    private final Object cookieLock;
    private final DataBufferFactory bufferFactory;
    private final byte[] buffer;

    public ServletServerHttpRequest(HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        this(createDefaultHttpHeaders(request), request, asyncContext, servletPath, bufferFactory, bufferSize);
    }

    public ServletServerHttpRequest(HttpHeaders headers, HttpServletRequest request, AsyncContext asyncContext, String servletPath, DataBufferFactory bufferFactory, int bufferSize) throws IOException, URISyntaxException {
        super(initUri(request), request.getContextPath() + servletPath, initHeaders(headers, request));
        this.cookieLock = new Object();
        Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be higher than 0");
        this.request = request;
        this.bufferFactory = bufferFactory;
        this.buffer = new byte[bufferSize];
        asyncContext.addListener(new RequestAsyncListener());
        ServletInputStream inputStream = request.getInputStream();
        this.bodyPublisher = new RequestBodyPublisher(inputStream);
        this.bodyPublisher.registerReadListener();
    }

    private static HttpHeaders createDefaultHttpHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<?> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<?> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                headers.add(name, values.nextElement());
            }
        }
        return headers;
    }

    private static URI initUri(HttpServletRequest request) throws URISyntaxException {
        Assert.notNull(request, "'request' must not be null");
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            url.append('?').append(query);
        }
        return new URI(url.toString());
    }

    private static HttpHeaders initHeaders(HttpHeaders headers, HttpServletRequest request) {
        int contentLength;
        MediaType contentType = headers.getContentType();
        if (contentType == null) {
            String requestContentType = request.getContentType();
            if (StringUtils.hasLength(requestContentType)) {
                contentType = MediaType.parseMediaType(requestContentType);
                headers.setContentType(contentType);
            }
        }
        if (contentType != null && contentType.getCharset() == null) {
            String encoding = request.getCharacterEncoding();
            if (StringUtils.hasLength(encoding)) {
                Charset charset = Charset.forName(encoding);
                Map<String, String> params = new LinkedCaseInsensitiveMap<>();
                params.putAll(contentType.getParameters());
                params.put(BasicAuthenticator.charsetparam, charset.toString());
                headers.setContentType(new MediaType(contentType.getType(), contentType.getSubtype(), params));
            }
        }
        if (headers.getContentLength() == -1 && (contentLength = request.getContentLength()) != -1) {
            headers.setContentLength(contentLength);
        }
        return headers;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.request.getMethod();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        Cookie[] cookies;
        MultiValueMap<String, HttpCookie> httpCookies = new LinkedMultiValueMap<>();
        synchronized (this.cookieLock) {
            cookies = this.request.getCookies();
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
                httpCookies.add(name, httpCookie);
            }
        }
        return httpCookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.request.getRemoteHost(), this.request.getRemotePort());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        X509Certificate[] certificates = getX509Certificates();
        if (certificates != null) {
            return new DefaultSslInfo(getSslSessionId(), certificates);
        }
        return null;
    }

    @Nullable
    private String getSslSessionId() {
        return (String) this.request.getAttribute("javax.servlet.request.ssl_session_id");
    }

    @Nullable
    private X509Certificate[] getX509Certificates() {
        return (X509Certificate[]) this.request.getAttribute("javax.servlet.request.X509Certificate");
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        return Flux.from(this.bodyPublisher);
    }

    @Nullable
    DataBuffer readFromInputStream() throws IOException {
        int read = this.request.getInputStream().read(this.buffer);
        logBytesRead(read);
        if (read > 0) {
            DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
            dataBuffer.write(this.buffer, 0, read);
            return dataBuffer;
        } else if (read == -1) {
            return EOF_BUFFER;
        } else {
            return null;
        }
    }

    public final void logBytesRead(int read) {
        Log rsReadLogger = AbstractListenerReadPublisher.rsReadLogger;
        if (rsReadLogger.isTraceEnabled()) {
            rsReadLogger.trace(getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : ""));
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestAsyncListener.class */
    public final class RequestAsyncListener implements AsyncListener {
        private RequestAsyncListener() {
            ServletServerHttpRequest.this = r4;
        }

        @Override // javax.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // javax.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            ServletServerHttpRequest.this.bodyPublisher.onError(ex != null ? ex : new IllegalStateException("Async operation timeout."));
        }

        @Override // javax.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onError(event.getThrowable());
        }

        @Override // javax.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            ServletServerHttpRequest.this.bodyPublisher.onAllDataRead();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestBodyPublisher.class */
    public class RequestBodyPublisher extends AbstractListenerReadPublisher<DataBuffer> {
        private final ServletInputStream inputStream;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public RequestBodyPublisher(ServletInputStream inputStream) {
            super(r4.getLogPrefix());
            ServletServerHttpRequest.this = r4;
            this.inputStream = inputStream;
        }

        public void registerReadListener() throws IOException {
            this.inputStream.setReadListener(new RequestBodyPublisherReadListener());
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void checkOnDataAvailable() {
            if (this.inputStream.isReady() && !this.inputStream.isFinished()) {
                onDataAvailable();
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        @Nullable
        public DataBuffer read() throws IOException {
            if (this.inputStream.isReady()) {
                DataBuffer dataBuffer = ServletServerHttpRequest.this.readFromInputStream();
                if (dataBuffer == ServletServerHttpRequest.EOF_BUFFER) {
                    onAllDataRead();
                    dataBuffer = null;
                }
                return dataBuffer;
            }
            return null;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void readingPaused() {
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerReadPublisher
        protected void discardData() {
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpRequest$RequestBodyPublisher$RequestBodyPublisherReadListener.class */
        public class RequestBodyPublisherReadListener implements ReadListener {
            private RequestBodyPublisherReadListener() {
                RequestBodyPublisher.this = r4;
            }

            @Override // javax.servlet.ReadListener
            public void onDataAvailable() throws IOException {
                RequestBodyPublisher.this.onDataAvailable();
            }

            @Override // javax.servlet.ReadListener
            public void onAllDataRead() throws IOException {
                RequestBodyPublisher.this.onAllDataRead();
            }

            @Override // javax.servlet.ReadListener
            public void onError(Throwable throwable) {
                RequestBodyPublisher.this.onError(throwable);
            }
        }
    }
}