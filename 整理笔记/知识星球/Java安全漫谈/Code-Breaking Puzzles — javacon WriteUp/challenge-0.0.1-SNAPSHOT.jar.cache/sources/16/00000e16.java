package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.websocket.DecodeException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.tomcat.websocket.WsSession;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoMessageHandlerWholeBase.class */
public abstract class PojoMessageHandlerWholeBase<T> extends PojoMessageHandlerBase<T> implements MessageHandler.Whole<T> {
    protected abstract Object decode(T t) throws DecodeException;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void onClose();

    public PojoMessageHandlerWholeBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // javax.websocket.MessageHandler.Whole
    public final void onMessage(T message) {
        if (this.params.length == 1 && (this.params[0] instanceof DecodeException)) {
            ((WsSession) this.session).getLocal().onError(this.session, (DecodeException) this.params[0]);
            return;
        }
        try {
            Object payload = decode(message);
            if (payload == null) {
                if (this.convert) {
                    payload = convert(message);
                } else {
                    payload = message;
                }
            }
            Object[] parameters = (Object[]) this.params.clone();
            if (this.indexSession != -1) {
                parameters[this.indexSession] = this.session;
            }
            parameters[this.indexPayload] = payload;
            Object result = null;
            try {
                result = this.method.invoke(this.pojo, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                handlePojoMethodException(e);
            }
            processResult(result);
        } catch (DecodeException de) {
            ((WsSession) this.session).getLocal().onError(this.session, de);
        }
    }

    protected Object convert(T message) {
        return message;
    }
}