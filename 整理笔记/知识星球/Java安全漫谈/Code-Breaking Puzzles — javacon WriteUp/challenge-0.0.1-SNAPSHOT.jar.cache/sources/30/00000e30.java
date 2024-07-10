package org.apache.tomcat.websocket.server;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

@HandlesTypes({ServerEndpoint.class, ServerApplicationConfig.class, Endpoint.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsSci.class */
public class WsSci implements ServletContainerInitializer {
    @Override // javax.servlet.ServletContainerInitializer
    public void onStartup(Set<Class<?>> clazzes, ServletContext ctx) throws ServletException {
        WsServerContainer sc = init(ctx, true);
        if (clazzes == null || clazzes.size() == 0) {
            return;
        }
        Set<ServerApplicationConfig> serverApplicationConfigs = new HashSet<>();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        try {
            String wsPackage = ContainerProvider.class.getName();
            String wsPackage2 = wsPackage.substring(0, wsPackage.lastIndexOf(46) + 1);
            for (Class<?> clazz : clazzes) {
                int modifiers = clazz.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && !clazz.getName().startsWith(wsPackage2)) {
                    if (ServerApplicationConfig.class.isAssignableFrom(clazz)) {
                        serverApplicationConfigs.add((ServerApplicationConfig) clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
                    }
                    if (Endpoint.class.isAssignableFrom(clazz)) {
                        hashSet.add(clazz);
                    }
                    if (clazz.isAnnotationPresent(ServerEndpoint.class)) {
                        hashSet2.add(clazz);
                    }
                }
            }
            Set<ServerEndpointConfig> filteredEndpointConfigs = new HashSet<>();
            Set<Class<?>> filteredPojoEndpoints = new HashSet<>();
            if (serverApplicationConfigs.isEmpty()) {
                filteredPojoEndpoints.addAll(hashSet2);
            } else {
                for (ServerApplicationConfig config : serverApplicationConfigs) {
                    Set<ServerEndpointConfig> configFilteredEndpoints = config.getEndpointConfigs(hashSet);
                    if (configFilteredEndpoints != null) {
                        filteredEndpointConfigs.addAll(configFilteredEndpoints);
                    }
                    Set<Class<?>> configFilteredPojos = config.getAnnotatedEndpointClasses(hashSet2);
                    if (configFilteredPojos != null) {
                        filteredPojoEndpoints.addAll(configFilteredPojos);
                    }
                }
            }
            try {
                for (ServerEndpointConfig config2 : filteredEndpointConfigs) {
                    sc.addEndpoint(config2);
                }
                for (Class<?> clazz2 : filteredPojoEndpoints) {
                    sc.addEndpoint(clazz2);
                }
            } catch (DeploymentException e) {
                throw new ServletException(e);
            }
        } catch (ReflectiveOperationException e2) {
            throw new ServletException(e2);
        }
    }

    public static WsServerContainer init(ServletContext servletContext, boolean initBySciMechanism) {
        WsServerContainer sc = new WsServerContainer(servletContext);
        servletContext.setAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE, sc);
        servletContext.addListener((ServletContext) new WsSessionListener(sc));
        if (initBySciMechanism) {
            servletContext.addListener((ServletContext) new WsContextListener());
        }
        return sc;
    }
}