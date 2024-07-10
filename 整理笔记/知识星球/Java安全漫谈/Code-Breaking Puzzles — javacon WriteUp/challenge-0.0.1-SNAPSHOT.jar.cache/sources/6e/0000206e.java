package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/SimpleBufferingClientHttpRequest.class */
final class SimpleBufferingClientHttpRequest extends AbstractBufferingClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleBufferingClientHttpRequest(HttpURLConnection connection, boolean outputStreaming) {
        this.connection = connection;
        this.outputStreaming = outputStreaming;
    }

    @Override // org.springframework.http.HttpRequest
    public String getMethodValue() {
        return this.connection.getRequestMethod();
    }

    @Override // org.springframework.http.HttpRequest
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override // org.springframework.http.client.AbstractBufferingClientHttpRequest
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        addHeaders(this.connection, headers);
        if (getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
            this.connection.setDoOutput(false);
        }
        if (this.connection.getDoOutput() && this.outputStreaming) {
            this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
        }
        this.connection.connect();
        if (this.connection.getDoOutput()) {
            FileCopyUtils.copy(bufferedOutput, this.connection.getOutputStream());
        } else {
            this.connection.getResponseCode();
        }
        return new SimpleClientHttpResponse(this.connection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
        headers.forEach(headerName, headerValues -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {
                connection.setRequestProperty(headerName, StringUtils.collectionToDelimitedString(headerValues, "; "));
                return;
            }
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                String actualHeaderValue = headerValue != null ? headerValue : "";
                connection.addRequestProperty(headerName, actualHeaderValue);
            }
        });
    }
}