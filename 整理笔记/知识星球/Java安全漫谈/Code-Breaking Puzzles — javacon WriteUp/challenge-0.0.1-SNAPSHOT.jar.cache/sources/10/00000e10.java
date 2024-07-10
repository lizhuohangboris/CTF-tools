package org.apache.tomcat.websocket.pojo;

import java.util.Collections;
import java.util.List;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/pojo/PojoEndpointClient.class */
public class PojoEndpointClient extends PojoEndpointBase {
    public PojoEndpointClient(Object pojo, List<Class<? extends Decoder>> decoders) throws DeploymentException {
        setPojo(pojo);
        setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null));
        setPathParameters(Collections.emptyMap());
    }

    @Override // javax.websocket.Endpoint
    public void onOpen(Session session, EndpointConfig config) {
        doOnOpen(session, config);
    }
}