package org.apache.coyote;

import java.io.IOException;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/InputBuffer.class */
public interface InputBuffer {
    int doRead(ApplicationBufferHandler applicationBufferHandler) throws IOException;
}