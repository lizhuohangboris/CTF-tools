package javax.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Encoder.class */
public interface Encoder {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Encoder$Binary.class */
    public interface Binary<T> extends Encoder {
        ByteBuffer encode(T t) throws EncodeException;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Encoder$BinaryStream.class */
    public interface BinaryStream<T> extends Encoder {
        void encode(T t, OutputStream outputStream) throws EncodeException, IOException;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Encoder$Text.class */
    public interface Text<T> extends Encoder {
        String encode(T t) throws EncodeException;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Encoder$TextStream.class */
    public interface TextStream<T> extends Encoder {
        void encode(T t, Writer writer) throws EncodeException, IOException;
    }

    void init(EndpointConfig endpointConfig);

    void destroy();
}