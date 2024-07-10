package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.OutputBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/HttpOutputBuffer.class */
public interface HttpOutputBuffer extends OutputBuffer {
    void end() throws IOException;

    void flush() throws IOException;
}