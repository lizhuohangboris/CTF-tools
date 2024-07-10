package org.springframework.http.server.reactive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpResponse.class */
public class ServletServerHttpResponse extends AbstractListenerServerHttpResponse {
    private final HttpServletResponse response;
    private final ServletOutputStream outputStream;
    private final int bufferSize;
    @Nullable
    private volatile ResponseBodyFlushProcessor bodyFlushProcessor;
    @Nullable
    private volatile ResponseBodyProcessor bodyProcessor;
    private volatile boolean flushOnNext;
    private final ServletServerHttpRequest request;

    public ServletServerHttpResponse(HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        this(new HttpHeaders(), response, asyncContext, bufferFactory, bufferSize, request);
    }

    public ServletServerHttpResponse(HttpHeaders headers, HttpServletResponse response, AsyncContext asyncContext, DataBufferFactory bufferFactory, int bufferSize, ServletServerHttpRequest request) throws IOException {
        super(bufferFactory, headers);
        Assert.notNull(response, "HttpServletResponse must not be null");
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        Assert.isTrue(bufferSize > 0, "Buffer size must be greater than 0");
        this.response = response;
        this.outputStream = response.getOutputStream();
        this.bufferSize = bufferSize;
        this.request = request;
        asyncContext.addListener(new ResponseAsyncListener());
        response.getOutputStream().setWriteListener(new ResponseBodyWriteListener());
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    public <T> T getNativeResponse() {
        return (T) this.response;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyStatusCode() {
        Integer statusCode = getStatusCodeValue();
        if (statusCode != null) {
            this.response.setStatus(statusCode.intValue());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyHeaders() {
        getHeaders().forEach(headerName, headerValues -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                this.response.addHeader(headerName, headerValue);
            }
        });
        MediaType contentType = getHeaders().getContentType();
        if (this.response.getContentType() == null && contentType != null) {
            this.response.setContentType(contentType.toString());
        }
        Charset charset = contentType != null ? contentType.getCharset() : null;
        if (this.response.getCharacterEncoding() == null && charset != null) {
            this.response.setCharacterEncoding(charset.name());
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpResponse
    protected void applyCookies() {
        for (String name : getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List) getCookies().get(name)) {
                Cookie cookie = new Cookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge((int) httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                this.response.addCookie(cookie);
            }
        }
    }

    @Override // org.springframework.http.server.reactive.AbstractListenerServerHttpResponse
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        ResponseBodyFlushProcessor processor = new ResponseBodyFlushProcessor();
        this.bodyFlushProcessor = processor;
        return processor;
    }

    protected int writeToOutputStream(DataBuffer dataBuffer) throws IOException {
        int bytesRead;
        ServletOutputStream outputStream = this.outputStream;
        InputStream input = dataBuffer.asInputStream();
        int bytesWritten = 0;
        byte[] buffer = new byte[this.bufferSize];
        while (outputStream.isReady() && (bytesRead = input.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            bytesWritten += bytesRead;
        }
        return bytesWritten;
    }

    public void flush() throws IOException {
        ServletOutputStream outputStream = this.outputStream;
        if (outputStream.isReady()) {
            try {
                outputStream.flush();
                this.flushOnNext = false;
                return;
            } catch (IOException ex) {
                this.flushOnNext = true;
                throw ex;
            }
        }
        this.flushOnNext = true;
    }

    public boolean isWritePossible() {
        return this.outputStream.isReady();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseAsyncListener.class */
    public final class ResponseAsyncListener implements AsyncListener {
        private ResponseAsyncListener() {
            ServletServerHttpResponse.this = r4;
        }

        @Override // javax.servlet.AsyncListener
        public void onStartAsync(AsyncEvent event) {
        }

        @Override // javax.servlet.AsyncListener
        public void onTimeout(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            handleError(ex != null ? ex : new IllegalStateException("Async operation timeout."));
        }

        @Override // javax.servlet.AsyncListener
        public void onError(AsyncEvent event) {
            handleError(event.getThrowable());
        }

        void handleError(Throwable ex) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                flushProcessor.onError(ex);
            }
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor != null) {
                processor.cancel();
                processor.onError(ex);
            }
        }

        @Override // javax.servlet.AsyncListener
        public void onComplete(AsyncEvent event) {
            ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
            if (flushProcessor != null) {
                flushProcessor.cancel();
                flushProcessor.onComplete();
            }
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor != null) {
                processor.cancel();
                processor.onComplete();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyWriteListener.class */
    public class ResponseBodyWriteListener implements WriteListener {
        private ResponseBodyWriteListener() {
            ServletServerHttpResponse.this = r4;
        }

        @Override // javax.servlet.WriteListener
        public void onWritePossible() throws IOException {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor == null) {
                ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
                if (flushProcessor != null) {
                    flushProcessor.onFlushPossible();
                    return;
                }
                return;
            }
            processor.onWritePossible();
        }

        @Override // javax.servlet.WriteListener
        public void onError(Throwable ex) {
            ResponseBodyProcessor processor = ServletServerHttpResponse.this.bodyProcessor;
            if (processor == null) {
                ResponseBodyFlushProcessor flushProcessor = ServletServerHttpResponse.this.bodyFlushProcessor;
                if (flushProcessor != null) {
                    flushProcessor.cancel();
                    flushProcessor.onError(ex);
                    return;
                }
                return;
            }
            processor.cancel();
            processor.onError(ex);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyFlushProcessor.class */
    public class ResponseBodyFlushProcessor extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ResponseBodyFlushProcessor() {
            super(r4.request.getLogPrefix());
            ServletServerHttpResponse.this = r4;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            ResponseBodyProcessor processor = new ResponseBodyProcessor();
            ServletServerHttpResponse.this.bodyProcessor = processor;
            return processor;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected void flush() throws IOException {
            if (rsWriteFlushLogger.isTraceEnabled()) {
                rsWriteFlushLogger.trace(getLogPrefix() + "Flush attempt");
            }
            ServletServerHttpResponse.this.flush();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor
        protected boolean isFlushPending() {
            return ServletServerHttpResponse.this.flushOnNext;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/reactive/ServletServerHttpResponse$ResponseBodyProcessor.class */
    public class ResponseBodyProcessor extends AbstractListenerWriteProcessor<DataBuffer> {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ResponseBodyProcessor() {
            super(r4.request.getLogPrefix());
            ServletServerHttpResponse.this = r4;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected boolean isWritePossible() {
            return ServletServerHttpResponse.this.isWritePossible();
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public boolean write(DataBuffer dataBuffer) throws IOException {
            if (ServletServerHttpResponse.this.flushOnNext) {
                if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "Flush attempt");
                }
                ServletServerHttpResponse.this.flush();
            }
            boolean ready = ServletServerHttpResponse.this.isWritePossible();
            int remaining = dataBuffer.readableByteCount();
            if (ready && remaining > 0) {
                int written = ServletServerHttpResponse.this.writeToOutputStream(dataBuffer);
                if (ServletServerHttpResponse.this.logger.isTraceEnabled()) {
                    ServletServerHttpResponse.this.logger.trace(getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes");
                } else if (rsWriteLogger.isTraceEnabled()) {
                    rsWriteLogger.trace(getLogPrefix() + "Wrote " + written + " of " + remaining + " bytes");
                }
                if (written == remaining) {
                    DataBufferUtils.release(dataBuffer);
                    return true;
                }
                return false;
            } else if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace(getLogPrefix() + "ready: " + ready + ", remaining: " + remaining);
                return false;
            } else {
                return false;
            }
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        protected void writingComplete() {
            ServletServerHttpResponse.this.bodyProcessor = null;
        }

        @Override // org.springframework.http.server.reactive.AbstractListenerWriteProcessor
        public void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }
    }
}