package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsStreamingClientHttpRequest.class */
final class HttpComponentsStreamingClientHttpRequest extends AbstractClientHttpRequest implements StreamingHttpOutputMessage {
    private final HttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;
    @Nullable
    private StreamingHttpOutputMessage.Body body;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpComponentsStreamingClientHttpRequest(HttpClient client, HttpUriRequest request, HttpContext context) {
        this.httpClient = client;
        this.httpRequest = request;
        this.httpContext = context;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.httpRequest.getMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        return this.httpRequest.getURI();
    }

    @Override // org.springframework.http.StreamingHttpOutputMessage
    public void setBody(StreamingHttpOutputMessage.Body body) {
        assertNotExecuted();
        this.body = body;
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        throw new UnsupportedOperationException("getBody not supported");
    }

    @Override // org.springframework.http.client.AbstractClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if ((this.httpRequest instanceof HttpEntityEnclosingRequest) && this.body != null) {
            HttpEntityEnclosingRequest entityEnclosingRequest = this.httpRequest;
            HttpEntity requestEntity = new StreamingHttpEntity(getHeaders(), this.body);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsStreamingClientHttpRequest$StreamingHttpEntity.class */
    private static class StreamingHttpEntity implements HttpEntity {
        private final HttpHeaders headers;
        private final StreamingHttpOutputMessage.Body body;

        public StreamingHttpEntity(HttpHeaders headers, StreamingHttpOutputMessage.Body body) {
            this.headers = headers;
            this.body = body;
        }

        public boolean isRepeatable() {
            return false;
        }

        public boolean isChunked() {
            return false;
        }

        public long getContentLength() {
            return this.headers.getContentLength();
        }

        @Nullable
        public Header getContentType() {
            MediaType contentType = this.headers.getContentType();
            if (contentType != null) {
                return new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
            }
            return null;
        }

        @Nullable
        public Header getContentEncoding() {
            String contentEncoding = this.headers.getFirst(HttpHeaders.CONTENT_ENCODING);
            if (contentEncoding != null) {
                return new BasicHeader(HttpHeaders.CONTENT_ENCODING, contentEncoding);
            }
            return null;
        }

        public InputStream getContent() throws IOException, IllegalStateException {
            throw new IllegalStateException("No content available");
        }

        public void writeTo(OutputStream outputStream) throws IOException {
            this.body.writeTo(outputStream);
        }

        public boolean isStreaming() {
            return true;
        }

        @Deprecated
        public void consumeContent() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}