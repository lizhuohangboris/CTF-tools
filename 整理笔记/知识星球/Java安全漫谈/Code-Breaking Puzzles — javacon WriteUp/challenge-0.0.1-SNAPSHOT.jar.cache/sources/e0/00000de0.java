package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import javax.websocket.Extension;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/Transformation.class */
public interface Transformation {
    void setNext(Transformation transformation);

    boolean validateRsvBits(int i);

    Extension getExtensionResponse();

    TransformationResult getMoreData(byte b, boolean z, int i, ByteBuffer byteBuffer) throws IOException;

    boolean validateRsv(int i, byte b);

    List<MessagePart> sendMessagePart(List<MessagePart> list);

    void close();
}