package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/ApplicationBufferHandler.class */
public interface ApplicationBufferHandler {
    void setByteBuffer(ByteBuffer byteBuffer);

    ByteBuffer getByteBuffer();

    void expand(int i);
}