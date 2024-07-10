package org.apache.tomcat.websocket.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.TransformationFactory;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WsHandshakeResponse;
import org.apache.tomcat.websocket.pojo.PojoEndpointServer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/UpgradeUtil.class */
public class UpgradeUtil {
    private static final StringManager sm = StringManager.getManager(UpgradeUtil.class.getPackage().getName());
    private static final byte[] WS_ACCEPT = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes(StandardCharsets.ISO_8859_1);

    private UpgradeUtil() {
    }

    public static boolean isWebSocketUpgradeRequest(ServletRequest request, ServletResponse response) {
        return (request instanceof HttpServletRequest) && (response instanceof HttpServletResponse) && headerContainsToken((HttpServletRequest) request, "Upgrade", org.apache.tomcat.websocket.Constants.UPGRADE_HEADER_VALUE) && "GET".equals(((HttpServletRequest) request).getMethod());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void doUpgrade(WsServerContainer sc, HttpServletRequest req, HttpServletResponse resp, ServerEndpointConfig sec, Map<String, String> pathParams) throws ServletException, IOException {
        List<Extension> installedExtensions;
        List<Extension> negotiatedExtensionsPhase2;
        Endpoint ep;
        if (!headerContainsToken(req, "Connection", org.apache.tomcat.websocket.Constants.CONNECTION_HEADER_VALUE)) {
            resp.sendError(400);
        } else if (!headerContainsToken(req, "Sec-WebSocket-Version", org.apache.tomcat.websocket.Constants.WS_VERSION_HEADER_VALUE)) {
            resp.setStatus(426);
            resp.setHeader("Sec-WebSocket-Version", org.apache.tomcat.websocket.Constants.WS_VERSION_HEADER_VALUE);
        } else {
            String key = req.getHeader("Sec-WebSocket-Key");
            if (key == null) {
                resp.sendError(400);
                return;
            }
            String origin = req.getHeader("Origin");
            if (!sec.getConfigurator().checkOrigin(origin)) {
                resp.sendError(403);
                return;
            }
            List<String> subProtocols = getTokensFromHeader(req, "Sec-WebSocket-Protocol");
            String subProtocol = sec.getConfigurator().getNegotiatedSubprotocol(sec.getSubprotocols(), subProtocols);
            List<Extension> extensionsRequested = new ArrayList<>();
            Enumeration<String> extHeaders = req.getHeaders("Sec-WebSocket-Extensions");
            while (extHeaders.hasMoreElements()) {
                Util.parseExtensionHeader(extensionsRequested, extHeaders.nextElement());
            }
            if (sec.getExtensions().size() == 0) {
                installedExtensions = org.apache.tomcat.websocket.Constants.INSTALLED_EXTENSIONS;
            } else {
                List<Extension> installedExtensions2 = new ArrayList<>();
                installedExtensions2.addAll(sec.getExtensions());
                installedExtensions2.addAll(org.apache.tomcat.websocket.Constants.INSTALLED_EXTENSIONS);
                installedExtensions = installedExtensions2;
            }
            List<Extension> negotiatedExtensionsPhase1 = sec.getConfigurator().getNegotiatedExtensions(installedExtensions, extensionsRequested);
            List<Transformation> transformations = createTransformations(negotiatedExtensionsPhase1);
            if (transformations.isEmpty()) {
                negotiatedExtensionsPhase2 = Collections.emptyList();
            } else {
                negotiatedExtensionsPhase2 = new ArrayList<>(transformations.size());
                for (Transformation t : transformations) {
                    negotiatedExtensionsPhase2.add(t.getExtensionResponse());
                }
            }
            Transformation transformation = null;
            StringBuilder responseHeaderExtensions = new StringBuilder();
            boolean first = true;
            for (Transformation t2 : transformations) {
                if (first) {
                    first = false;
                } else {
                    responseHeaderExtensions.append(',');
                }
                append(responseHeaderExtensions, t2.getExtensionResponse());
                if (transformation == null) {
                    transformation = t2;
                } else {
                    transformation.setNext(t2);
                }
            }
            if (transformation != null && !transformation.validateRsvBits(0)) {
                throw new ServletException(sm.getString("upgradeUtil.incompatibleRsv"));
            }
            resp.setHeader("Upgrade", org.apache.tomcat.websocket.Constants.UPGRADE_HEADER_VALUE);
            resp.setHeader("Connection", org.apache.tomcat.websocket.Constants.CONNECTION_HEADER_VALUE);
            resp.setHeader(HandshakeResponse.SEC_WEBSOCKET_ACCEPT, getWebSocketAccept(key));
            if (subProtocol != null && subProtocol.length() > 0) {
                resp.setHeader("Sec-WebSocket-Protocol", subProtocol);
            }
            if (!transformations.isEmpty()) {
                resp.setHeader("Sec-WebSocket-Extensions", responseHeaderExtensions.toString());
            }
            WsHandshakeRequest wsRequest = new WsHandshakeRequest(req, pathParams);
            WsHandshakeResponse wsResponse = new WsHandshakeResponse();
            WsPerSessionServerEndpointConfig perSessionServerEndpointConfig = new WsPerSessionServerEndpointConfig(sec);
            sec.getConfigurator().modifyHandshake(perSessionServerEndpointConfig, wsRequest, wsResponse);
            wsRequest.finished();
            for (Map.Entry<String, List<String>> entry : wsResponse.getHeaders().entrySet()) {
                for (String headerValue : entry.getValue()) {
                    resp.addHeader(entry.getKey(), headerValue);
                }
            }
            try {
                Class<?> clazz = sec.getEndpointClass();
                if (Endpoint.class.isAssignableFrom(clazz)) {
                    ep = (Endpoint) sec.getConfigurator().getEndpointInstance(clazz);
                } else {
                    ep = new PojoEndpointServer();
                    perSessionServerEndpointConfig.getUserProperties().put(org.apache.tomcat.websocket.pojo.Constants.POJO_PATH_PARAM_KEY, pathParams);
                }
                WsHttpUpgradeHandler wsHandler = (WsHttpUpgradeHandler) req.upgrade(WsHttpUpgradeHandler.class);
                wsHandler.preInit(ep, perSessionServerEndpointConfig, sc, wsRequest, negotiatedExtensionsPhase2, subProtocol, transformation, pathParams, req.isSecure());
            } catch (InstantiationException e) {
                throw new ServletException(e);
            }
        }
    }

    private static List<Transformation> createTransformations(List<Extension> negotiatedExtensions) {
        TransformationFactory factory = TransformationFactory.getInstance();
        LinkedHashMap<String, List<List<Extension.Parameter>>> extensionPreferences = new LinkedHashMap<>();
        List<Transformation> result = new ArrayList<>(negotiatedExtensions.size());
        for (Extension extension : negotiatedExtensions) {
            List<List<Extension.Parameter>> preferences = extensionPreferences.get(extension.getName());
            if (preferences == null) {
                preferences = new ArrayList<>();
                extensionPreferences.put(extension.getName(), preferences);
            }
            preferences.add(extension.getParameters());
        }
        for (Map.Entry<String, List<List<Extension.Parameter>>> entry : extensionPreferences.entrySet()) {
            Transformation transformation = factory.create(entry.getKey(), entry.getValue(), true);
            if (transformation != null) {
                result.add(transformation);
            }
        }
        return result;
    }

    private static void append(StringBuilder sb, Extension extension) {
        if (extension == null || extension.getName() == null || extension.getName().length() == 0) {
            return;
        }
        sb.append(extension.getName());
        for (Extension.Parameter p : extension.getParameters()) {
            sb.append(';');
            sb.append(p.getName());
            if (p.getValue() != null) {
                sb.append('=');
                sb.append(p.getValue());
            }
        }
    }

    private static boolean headerContainsToken(HttpServletRequest req, String headerName, String target) {
        Enumeration<String> headers = req.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String[] tokens = header.split(",");
            for (String token : tokens) {
                if (target.equalsIgnoreCase(token.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> getTokensFromHeader(HttpServletRequest req, String headerName) {
        List<String> result = new ArrayList<>();
        Enumeration<String> headers = req.getHeaders(headerName);
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String[] tokens = header.split(",");
            for (String token : tokens) {
                result.add(token.trim());
            }
        }
        return result;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [byte[], byte[][]] */
    private static String getWebSocketAccept(String key) {
        byte[] digest = ConcurrentMessageDigest.digestSHA1(new byte[]{key.getBytes(StandardCharsets.ISO_8859_1), WS_ACCEPT});
        return Base64.encodeBase64String(digest);
    }
}