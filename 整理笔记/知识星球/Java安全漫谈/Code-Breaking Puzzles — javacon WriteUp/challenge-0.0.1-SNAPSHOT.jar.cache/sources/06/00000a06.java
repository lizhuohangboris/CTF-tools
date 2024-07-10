package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/OutputBuffer.class */
public interface OutputBuffer {
    int doWrite(ByteBuffer byteBuffer) throws IOException;

    long getBytesWritten();
}