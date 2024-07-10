package org.apache.tomcat.websocket.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/DefaultServerEndpointConfigurator.class */
public class DefaultServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
    @Override // javax.websocket.server.ServerEndpointConfig.Configurator
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        try {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InstantiationException e) {
            throw e;
        } catch (ReflectiveOperationException e2) {
            InstantiationException ie = new InstantiationException();
            ie.initCause(e2);
            throw ie;
        }
    }

    @Override // javax.websocket.server.ServerEndpointConfig.Configurator
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
        for (String request : requested) {
            if (supported.contains(request)) {
                return request;
            }
        }
        return "";
    }

    @Override // javax.websocket.server.ServerEndpointConfig.Configurator
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
        Set<String> installedNames = new HashSet<>();
        for (Extension e : installed) {
            installedNames.add(e.getName());
        }
        List<Extension> result = new ArrayList<>();
        for (Extension request : requested) {
            if (installedNames.contains(request.getName())) {
                result.add(request);
            }
        }
        return result;
    }

    @Override // javax.websocket.server.ServerEndpointConfig.Configurator
    public boolean checkOrigin(String originHeaderValue) {
        return true;
    }

    @Override // javax.websocket.server.ServerEndpointConfig.Configurator
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    }
}