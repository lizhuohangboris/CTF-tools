package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpOutputMessage.class */
public interface HttpOutputMessage extends HttpMessage {
    OutputStream getBody() throws IOException;
}