package org.apache.tomcat.websocket.pojo;

import java.util.Map;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoEndpointServer.class */
public class PojoEndpointServer extends PojoEndpointBase {
    private static final StringManager sm = StringManager.getManager(PojoEndpointServer.class);

    @Override // javax.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        ServerEndpointConfig sec = (ServerEndpointConfig) endpointConfig;
        try {
            Object pojo = sec.getConfigurator().getEndpointInstance(sec.getEndpointClass());
            setPojo(pojo);
            Map<String, String> pathParameters = (Map) sec.getUserProperties().get(Constants.POJO_PATH_PARAM_KEY);
            setPathParameters(pathParameters);
            PojoMethodMapping methodMapping = (PojoMethodMapping) sec.getUserProperties().get(Constants.POJO_METHOD_MAPPING_KEY);
            setMethodMapping(methodMapping);
            doOnOpen(session, endpointConfig);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(sm.getString("pojoEndpointServer.getPojoInstanceFail", sec.getEndpointClass().getName()), e);
        }
    }
}