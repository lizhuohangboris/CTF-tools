package org.apache.tomcat.websocket.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.WsWebSocketContainer;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsServerContainer.class */
public class WsServerContainer extends WsWebSocketContainer implements ServerContainer {
    private static final StringManager sm = StringManager.getManager(WsServerContainer.class);
    private static final CloseReason AUTHENTICATED_HTTP_SESSION_CLOSED = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "This connection was established under an authenticated HTTP session that has ended.");
    private final ServletContext servletContext;
    private final WsWriteTimeout wsWriteTimeout = new WsWriteTimeout();
    private final Map<String, ServerEndpointConfig> configExactMatchMap = new ConcurrentHashMap();
    private final Map<Integer, SortedSet<TemplatePathMatch>> configTemplateMatchMap = new ConcurrentHashMap();
    private volatile boolean enforceNoAddAfterHandshake = org.apache.tomcat.websocket.Constants.STRICT_SPEC_COMPLIANCE;
    private volatile boolean addAllowed = true;
    private final Map<String, Set<WsSession>> authenticatedSessions = new ConcurrentHashMap();
    private volatile boolean endpointsRegistered = false;

    public WsServerContainer(ServletContext servletContext) {
        this.servletContext = servletContext;
        setInstanceManager((InstanceManager) servletContext.getAttribute(InstanceManager.class.getName()));
        String value = servletContext.getInitParameter(Constants.BINARY_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM);
        if (value != null) {
            setDefaultMaxBinaryMessageBufferSize(Integer.parseInt(value));
        }
        String value2 = servletContext.getInitParameter(Constants.TEXT_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM);
        if (value2 != null) {
            setDefaultMaxTextMessageBufferSize(Integer.parseInt(value2));
        }
        String value3 = servletContext.getInitParameter(Constants.ENFORCE_NO_ADD_AFTER_HANDSHAKE_CONTEXT_INIT_PARAM);
        if (value3 != null) {
            setEnforceNoAddAfterHandshake(Boolean.parseBoolean(value3));
        }
        FilterRegistration.Dynamic fr = servletContext.addFilter("Tomcat WebSocket (JSR356) Filter", new WsFilter());
        fr.setAsyncSupported(true);
        EnumSet<DispatcherType> types = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        fr.addMappingForUrlPatterns(types, true, "/*");
    }

    @Override // javax.websocket.server.ServerContainer
    public void addEndpoint(ServerEndpointConfig sec) throws DeploymentException {
        if (this.enforceNoAddAfterHandshake && !this.addAllowed) {
            throw new DeploymentException(sm.getString("serverContainer.addNotAllowed"));
        }
        if (this.servletContext == null) {
            throw new DeploymentException(sm.getString("serverContainer.servletContextMissing"));
        }
        String path = sec.getPath();
        PojoMethodMapping methodMapping = new PojoMethodMapping(sec.getEndpointClass(), sec.getDecoders(), path);
        if (methodMapping.getOnClose() != null || methodMapping.getOnOpen() != null || methodMapping.getOnError() != null || methodMapping.hasMessageHandlers()) {
            sec.getUserProperties().put(org.apache.tomcat.websocket.pojo.Constants.POJO_METHOD_MAPPING_KEY, methodMapping);
        }
        UriTemplate uriTemplate = new UriTemplate(path);
        if (uriTemplate.hasParameters()) {
            Integer key = Integer.valueOf(uriTemplate.getSegmentCount());
            SortedSet<TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
            if (templateMatches == null) {
                this.configTemplateMatchMap.putIfAbsent(key, new TreeSet<>(TemplatePathMatchComparator.getInstance()));
                templateMatches = this.configTemplateMatchMap.get(key);
            }
            if (!templateMatches.add(new TemplatePathMatch(sec, uriTemplate))) {
                throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", path, sec.getEndpointClass(), sec.getEndpointClass()));
            }
        } else {
            ServerEndpointConfig old = this.configExactMatchMap.put(path, sec);
            if (old != null) {
                throw new DeploymentException(sm.getString("serverContainer.duplicatePaths", path, old.getEndpointClass(), sec.getEndpointClass()));
            }
        }
        this.endpointsRegistered = true;
    }

    @Override // javax.websocket.server.ServerContainer
    public void addEndpoint(Class<?> pojo) throws DeploymentException {
        ServerEndpoint annotation = (ServerEndpoint) pojo.getAnnotation(ServerEndpoint.class);
        if (annotation == null) {
            throw new DeploymentException(sm.getString("serverContainer.missingAnnotation", pojo.getName()));
        }
        String path = annotation.value();
        validateEncoders(annotation.encoders());
        Class<? extends ServerEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
        ServerEndpointConfig.Configurator configurator = null;
        if (!configuratorClazz.equals(ServerEndpointConfig.Configurator.class)) {
            try {
                configurator = annotation.configurator().getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("serverContainer.configuratorFail", annotation.configurator().getName(), pojo.getClass().getName()), e);
            }
        }
        ServerEndpointConfig sec = ServerEndpointConfig.Builder.create(pojo, path).decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).subprotocols(Arrays.asList(annotation.subprotocols())).configurator(configurator).build();
        addEndpoint(sec);
    }

    public boolean areEndpointsRegistered() {
        return this.endpointsRegistered;
    }

    public void doUpgrade(HttpServletRequest request, HttpServletResponse response, ServerEndpointConfig sec, Map<String, String> pathParams) throws ServletException, IOException {
        UpgradeUtil.doUpgrade(this, request, response, sec, pathParams);
    }

    public WsMappingResult findMapping(String path) {
        if (this.addAllowed) {
            this.addAllowed = false;
        }
        ServerEndpointConfig sec = this.configExactMatchMap.get(path);
        if (sec != null) {
            return new WsMappingResult(sec, Collections.emptyMap());
        }
        try {
            UriTemplate pathUriTemplate = new UriTemplate(path);
            Integer key = Integer.valueOf(pathUriTemplate.getSegmentCount());
            SortedSet<TemplatePathMatch> templateMatches = this.configTemplateMatchMap.get(key);
            if (templateMatches == null) {
                return null;
            }
            Map<String, String> pathParams = null;
            Iterator<TemplatePathMatch> it = templateMatches.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TemplatePathMatch templateMatch = it.next();
                pathParams = templateMatch.getUriTemplate().match(pathUriTemplate);
                if (pathParams != null) {
                    sec = templateMatch.getConfig();
                    break;
                }
            }
            if (sec == null) {
                return null;
            }
            return new WsMappingResult(sec, pathParams);
        } catch (DeploymentException e) {
            return null;
        }
    }

    public boolean isEnforceNoAddAfterHandshake() {
        return this.enforceNoAddAfterHandshake;
    }

    public void setEnforceNoAddAfterHandshake(boolean enforceNoAddAfterHandshake) {
        this.enforceNoAddAfterHandshake = enforceNoAddAfterHandshake;
    }

    public WsWriteTimeout getTimeout() {
        return this.wsWriteTimeout;
    }

    @Override // org.apache.tomcat.websocket.WsWebSocketContainer
    public void registerSession(Endpoint endpoint, WsSession wsSession) {
        super.registerSession(endpoint, wsSession);
        if (wsSession.isOpen() && wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            registerAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.websocket.WsWebSocketContainer
    public void unregisterSession(Endpoint endpoint, WsSession wsSession) {
        if (wsSession.getUserPrincipal() != null && wsSession.getHttpSessionId() != null) {
            unregisterAuthenticatedSession(wsSession, wsSession.getHttpSessionId());
        }
        super.unregisterSession(endpoint, wsSession);
    }

    private void registerAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions == null) {
            this.authenticatedSessions.putIfAbsent(httpSessionId, Collections.newSetFromMap(new ConcurrentHashMap()));
            wsSessions = this.authenticatedSessions.get(httpSessionId);
        }
        wsSessions.add(wsSession);
    }

    private void unregisterAuthenticatedSession(WsSession wsSession, String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.get(httpSessionId);
        if (wsSessions != null) {
            wsSessions.remove(wsSession);
        }
    }

    public void closeAuthenticatedSession(String httpSessionId) {
        Set<WsSession> wsSessions = this.authenticatedSessions.remove(httpSessionId);
        if (wsSessions != null && !wsSessions.isEmpty()) {
            for (WsSession wsSession : wsSessions) {
                try {
                    wsSession.close(AUTHENTICATED_HTTP_SESSION_CLOSED);
                } catch (IOException e) {
                }
            }
        }
    }

    private static void validateEncoders(Class<? extends Encoder>[] encoders) throws DeploymentException {
        for (Class<? extends Encoder> encoder : encoders) {
            try {
                encoder.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("serverContainer.encoderFail", encoder.getName()), e);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsServerContainer$TemplatePathMatch.class */
    public static class TemplatePathMatch {
        private final ServerEndpointConfig config;
        private final UriTemplate uriTemplate;

        public TemplatePathMatch(ServerEndpointConfig config, UriTemplate uriTemplate) {
            this.config = config;
            this.uriTemplate = uriTemplate;
        }

        public ServerEndpointConfig getConfig() {
            return this.config;
        }

        public UriTemplate getUriTemplate() {
            return this.uriTemplate;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsServerContainer$TemplatePathMatchComparator.class */
    public static class TemplatePathMatchComparator implements Comparator<TemplatePathMatch> {
        private static final TemplatePathMatchComparator INSTANCE = new TemplatePathMatchComparator();

        public static TemplatePathMatchComparator getInstance() {
            return INSTANCE;
        }

        private TemplatePathMatchComparator() {
        }

        @Override // java.util.Comparator
        public int compare(TemplatePathMatch tpm1, TemplatePathMatch tpm2) {
            return tpm1.getUriTemplate().getNormalizedPath().compareTo(tpm2.getUriTemplate().getNormalizedPath());
        }
    }
}