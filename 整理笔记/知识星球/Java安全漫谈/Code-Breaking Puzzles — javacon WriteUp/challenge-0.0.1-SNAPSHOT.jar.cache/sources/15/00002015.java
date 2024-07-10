package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpInputMessage.class */
public interface HttpInputMessage extends HttpMessage {
    InputStream getBody() throws IOException;
}