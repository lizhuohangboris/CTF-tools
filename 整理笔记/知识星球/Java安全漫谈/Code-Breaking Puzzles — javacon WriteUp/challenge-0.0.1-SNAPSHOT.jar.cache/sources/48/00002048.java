package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/ClientHttpResponse.class */
public interface ClientHttpResponse extends HttpInputMessage, Closeable {
    HttpStatus getStatusCode() throws IOException;

    int getRawStatusCode() throws IOException;

    String getStatusText() throws IOException;

    void close();
}