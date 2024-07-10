package org.apache.tomcat.websocket.pojo;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholeText.class */
public class PojoMessageHandlerWholeText extends PojoMessageHandlerWholeBase<String> {
    private static final StringManager sm = StringManager.getManager(PojoMessageHandlerWholeText.class);
    private final List<Decoder> decoders;
    private final Class<?> primitiveType;

    public PojoMessageHandlerWholeText(Object pojo, Method method, Session session, EndpointConfig config, List<Class<? extends Decoder>> decoderClazzes, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.decoders = new ArrayList();
        if (maxMessageSize > -1 && maxMessageSize > session.getMaxTextMessageBufferSize()) {
            if (maxMessageSize > 2147483647L) {
                throw new IllegalArgumentException(sm.getString("pojoMessageHandlerWhole.maxBufferSize"));
            }
            session.setMaxTextMessageBufferSize((int) maxMessageSize);
        }
        Class<?> type = method.getParameterTypes()[indexPayload];
        if (Util.isPrimitive(type)) {
            this.primitiveType = type;
            return;
        }
        this.primitiveType = null;
        if (decoderClazzes != null) {
            try {
                for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                    if (Decoder.Text.class.isAssignableFrom(decoderClazz)) {
                        Decoder.Text<?> decoder = (Decoder.Text) decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                        decoder.init(config);
                        this.decoders.add(decoder);
                    } else if (Decoder.TextStream.class.isAssignableFrom(decoderClazz)) {
                        Decoder.TextStream<?> decoder2 = (Decoder.TextStream) decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                        decoder2.init(config);
                        this.decoders.add(decoder2);
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object decode(String message) throws DecodeException {
        if (this.primitiveType != null) {
            return Util.coerceToType(this.primitiveType, message);
        }
        for (Decoder decoder : this.decoders) {
            if (decoder instanceof Decoder.Text) {
                if (((Decoder.Text) decoder).willDecode(message)) {
                    return ((Decoder.Text) decoder).decode(message);
                }
            } else {
                StringReader r = new StringReader(message);
                try {
                    return ((Decoder.TextStream) decoder).decode(r);
                } catch (IOException ioe) {
                    throw new DecodeException(message, sm.getString("pojoMessageHandlerWhole.decodeIoFail"), ioe);
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public Object convert(String message) {
        return new StringReader(message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase
    public void onClose() {
        for (Decoder decoder : this.decoders) {
            decoder.destroy();
        }
    }
}