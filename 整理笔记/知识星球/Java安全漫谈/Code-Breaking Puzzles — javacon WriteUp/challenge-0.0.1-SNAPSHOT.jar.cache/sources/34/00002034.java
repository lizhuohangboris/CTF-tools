package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/StreamingHttpOutputMessage.class */
public interface StreamingHttpOutputMessage extends HttpOutputMessage {

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/StreamingHttpOutputMessage$Body.class */
    public interface Body {
        void writeTo(OutputStream outputStream) throws IOException;
    }

    void setBody(Body body);
}