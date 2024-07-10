package org.apache.tomcat.websocket.pojo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholeBinary.class */
public class PojoMessageHandlerWholeBinary extends PojoMessageHandlerWholeBase<ByteBuffer> {
    private static final StringManager sm = StringManager.getManager(PojoMessageHandlerWholeBinary.class);
    private final List<Decoder> decoders;
    private final boolean isForInputStream;

    public PojoMessageHandlerWholeBinary(Object pojo, Method method, Session session, EndpointConfig config, List<Class<? extends Decoder>> decoderClazzes, Object[] params, int indexPayload, boolean convert, int indexSession, boolean isForInputStream, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.decoders = new ArrayList();
        if (maxMessageSize > -1 && maxMessageSize > session.getMaxBinaryMessageBufferSize()) {
            if (maxMessageSize > 2147483647L) {
                throw new IllegalArgumentException(sm.getString("pojoMessageHandlerWhole.maxBufferSize"));
            }
            session.setMaxBinaryMessageBufferSize((int) maxMessageSize);
        }
        if (decoderClazzes != null) {
            try {
                for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                    if (Decoder.Binary.class.isAssignableFrom(decoderClazz)) {
                        Decoder.Binary<?> decoder = (Decoder.Binary) decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                        decoder.init(config);
                        this.decoders.add(decoder);
                    } else if (Decoder.BinaryStream.class.isAssignableFrom(decoderClazz)) {
                        Decoder.BinaryStream<?> decoder2 = (Decoder.BinaryStream) decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                        decoder2.init(config);
                        this.decoders.add(decoder2);
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.isForInputStream = isForInputStream;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object decode(ByteBuffer message) throws DecodeException {
        for (Decoder decoder : this.decoders) {
            if (decoder instanceof Decoder.Binary) {
                if (((Decoder.Binary) decoder).willDecode(message)) {
                    return ((Decoder.Binary) decoder).decode(message);
                }
            } else {
                byte[] array = new byte[message.limit() - message.position()];
                message.get(array);
                ByteArrayInputStream bais = new ByteArrayInputStream(array);
                try {
                    return ((Decoder.BinaryStream) decoder).decode(bais);
                } catch (IOException ioe) {
                    throw new DecodeException(message, sm.getString("pojoMessageHandlerWhole.decodeIoFail"), ioe);
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object convert(ByteBuffer message) {
        byte[] array = new byte[message.remaining()];
        message.get(array);
        if (this.isForInputStream) {
            return new ByteArrayInputStream(array);
        }
        return array;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public void onClose() {
        for (Decoder decoder : this.decoders) {
            decoder.destroy();
        }
    }
}