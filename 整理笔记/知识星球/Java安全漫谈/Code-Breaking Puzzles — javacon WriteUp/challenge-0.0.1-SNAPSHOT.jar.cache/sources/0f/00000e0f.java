package org.apache.tomcat.websocket.pojo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoEndpointBase.class */
public abstract class PojoEndpointBase extends Endpoint {
    private final Log log = LogFactory.getLog(PojoEndpointBase.class);
    private static final StringManager sm = StringManager.getManager(PojoEndpointBase.class);
    private Object pojo;
    private Map<String, String> pathParameters;
    private PojoMethodMapping methodMapping;

    /* JADX INFO: Access modifiers changed from: protected */
    public final void doOnOpen(Session session, EndpointConfig config) {
        PojoMethodMapping methodMapping = getMethodMapping();
        Object pojo = getPojo();
        Map<String, String> pathParameters = getPathParameters();
        for (MessageHandler mh : methodMapping.getMessageHandlers(pojo, pathParameters, session, config)) {
            session.addMessageHandler(mh);
        }
        if (methodMapping.getOnOpen() != null) {
            try {
                methodMapping.getOnOpen().invoke(pojo, methodMapping.getOnOpenArgs(pathParameters, session, config));
            } catch (IllegalAccessException e) {
                this.log.error(sm.getString("pojoEndpointBase.onOpenFail", pojo.getClass().getName()), e);
                handleOnOpenOrCloseError(session, e);
            } catch (InvocationTargetException e2) {
                Throwable cause = e2.getCause();
                handleOnOpenOrCloseError(session, cause);
            } catch (Throwable t) {
                handleOnOpenOrCloseError(session, t);
            }
        }
    }

    private void handleOnOpenOrCloseError(Session session, Throwable t) {
        ExceptionUtils.handleThrowable(t);
        onError(session, t);
        try {
            session.close();
        } catch (IOException ioe) {
            this.log.warn(sm.getString("pojoEndpointBase.closeSessionFail"), ioe);
        }
    }

    @Override // javax.websocket.Endpoint
    public final void onClose(Session session, CloseReason closeReason) {
        if (this.methodMapping.getOnClose() != null) {
            try {
                this.methodMapping.getOnClose().invoke(this.pojo, this.methodMapping.getOnCloseArgs(this.pathParameters, session, closeReason));
            } catch (Throwable t) {
                this.log.error(sm.getString("pojoEndpointBase.onCloseFail", this.pojo.getClass().getName()), t);
                handleOnOpenOrCloseError(session, t);
            }
        }
        Set<MessageHandler> messageHandlers = session.getMessageHandlers();
        for (MessageHandler messageHandler : messageHandlers) {
            if (messageHandler instanceof PojoMessageHandlerWholeBase) {
                ((PojoMessageHandlerWholeBase) messageHandler).onClose();
            }
        }
    }

    @Override // javax.websocket.Endpoint
    public final void onError(Session session, Throwable throwable) {
        if (this.methodMapping.getOnError() == null) {
            this.log.error(sm.getString("pojoEndpointBase.onError", this.pojo.getClass().getName()), throwable);
            return;
        }
        try {
            this.methodMapping.getOnError().invoke(this.pojo, this.methodMapping.getOnErrorArgs(this.pathParameters, session, throwable));
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.log.error(sm.getString("pojoEndpointBase.onErrorFail", this.pojo.getClass().getName()), t);
        }
    }

    protected Object getPojo() {
        return this.pojo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPojo(Object pojo) {
        this.pojo = pojo;
    }

    protected Map<String, String> getPathParameters() {
        return this.pathParameters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    protected PojoMethodMapping getMethodMapping() {
        return this.methodMapping;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMethodMapping(PojoMethodMapping methodMapping) {
        this.methodMapping = methodMapping;
    }
}