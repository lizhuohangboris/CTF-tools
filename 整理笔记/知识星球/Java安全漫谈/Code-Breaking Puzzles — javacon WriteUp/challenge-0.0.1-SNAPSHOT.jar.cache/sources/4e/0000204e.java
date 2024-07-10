package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsClientHttpRequest.class */
final class HttpComponentsClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final HttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpComponentsClientHttpRequest(HttpClient client, HttpUriRequest request, HttpContext context) {
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

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = this.httpRequest;
            entityEnclosingRequest.setEntity(new ByteArrayEntity(bufferedOutput));
        }
        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addHeaders(HttpUriRequest httpRequest, HttpHeaders headers) {
        headers.forEach(headerName, headerValues -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {
                String headerValue = StringUtils.collectionToDelimitedString(headerValues, "; ");
                httpRequest.addHeader(headerName, headerValue);
            } else if (!HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(headerName) && !"Transfer-Encoding".equalsIgnoreCase(headerName)) {
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue2 = (String) it.next();
                    httpRequest.addHeader(headerName, headerValue2);
                }
            }
        });
    }
}