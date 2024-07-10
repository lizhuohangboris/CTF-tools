package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import javax.websocket.DecodeException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerPartialBase.class */
public abstract class PojoMessageHandlerPartialBase<T> extends PojoMessageHandlerBase<T> implements MessageHandler.Partial<T> {
    private final int indexBoolean;

    public PojoMessageHandlerPartialBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.indexBoolean = indexBoolean;
    }

    @Override // javax.websocket.MessageHandler.Partial
    public final void onMessage(T message, boolean last) {
        if (this.params.length == 1 && (this.params[0] instanceof DecodeException)) {
            ((WsSession) this.session).getLocal().onError(this.session, (DecodeException) this.params[0]);
            return;
        }
        Object[] parameters = (Object[]) this.params.clone();
        if (this.indexBoolean != -1) {
            parameters[this.indexBoolean] = Boolean.valueOf(last);
        }
        if (this.indexSession != -1) {
            parameters[this.indexSession] = this.session;
        }
        if (this.convert) {
            parameters[this.indexPayload] = ((ByteBuffer) message).array();
        } else {
            parameters[this.indexPayload] = message;
        }
        Object result = null;
        try {
            result = this.method.invoke(this.pojo, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            handlePojoMethodException(e);
        }
        processResult(result);
    }
}