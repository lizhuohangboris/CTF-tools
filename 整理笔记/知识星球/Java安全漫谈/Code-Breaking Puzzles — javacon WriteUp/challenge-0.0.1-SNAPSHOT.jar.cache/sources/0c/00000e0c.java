package org.apache.tomcat.websocket;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsWebSocketContainer.class */
public class WsWebSocketContainer implements WebSocketContainer, BackgroundProcess {
    private static final StringManager sm = StringManager.getManager(WsWebSocketContainer.class);
    private static final Random RANDOM = new Random();
    private static final byte[] CRLF = {13, 10};
    private static final byte[] GET_BYTES = "GET ".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] ROOT_URI_BYTES = "/".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] HTTP_VERSION_BYTES = " HTTP/1.1\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private volatile AsynchronousChannelGroup asynchronousChannelGroup = null;
    private final Object asynchronousChannelGroupLock = new Object();
    private final Log log = LogFactory.getLog(WsWebSocketContainer.class);
    private final Map<Endpoint, Set<WsSession>> endpointSessionMap = new HashMap();
    private final Map<WsSession, WsSession> sessions = new ConcurrentHashMap();
    private final Object endPointSessionMapLock = new Object();
    private long defaultAsyncTimeout = -1;
    private int maxBinaryMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private int maxTextMessageBufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private volatile long defaultMaxSessionIdleTimeout = 0;
    private int backgroundProcessCount = 0;
    private int processPeriod = Constants.DEFAULT_PROCESS_PERIOD;
    private InstanceManager instanceManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InstanceManager getInstanceManager() {
        return this.instanceManager;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override // javax.websocket.WebSocketContainer
    public Session connectToServer(Object pojo, URI path) throws DeploymentException {
        ClientEndpoint annotation = (ClientEndpoint) pojo.getClass().getAnnotation(ClientEndpoint.class);
        if (annotation == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingAnnotation", pojo.getClass().getName()));
        }
        Endpoint ep = new PojoEndpointClient(pojo, Arrays.asList(annotation.decoders()));
        Class<? extends ClientEndpointConfig.Configurator> configuratorClazz = annotation.configurator();
        ClientEndpointConfig.Configurator configurator = null;
        if (!ClientEndpointConfig.Configurator.class.equals(configuratorClazz)) {
            try {
                configurator = configuratorClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e) {
                throw new DeploymentException(sm.getString("wsWebSocketContainer.defaultConfiguratorFail"), e);
            }
        }
        ClientEndpointConfig.Builder builder = ClientEndpointConfig.Builder.create();
        if (configurator != null) {
            builder.configurator(configurator);
        }
        ClientEndpointConfig config = builder.decoders(Arrays.asList(annotation.decoders())).encoders(Arrays.asList(annotation.encoders())).preferredSubprotocols(Arrays.asList(annotation.subprotocols())).build();
        return connectToServer(ep, config, path);
    }

    @Override // javax.websocket.WebSocketContainer
    public Session connectToServer(Class<?> annotatedEndpointClass, URI path) throws DeploymentException {
        try {
            Object pojo = annotatedEndpointClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            return connectToServer(pojo, path);
        } catch (ReflectiveOperationException e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.endpointCreateFail", annotatedEndpointClass.getName()), e);
        }
    }

    @Override // javax.websocket.WebSocketContainer
    public Session connectToServer(Class<? extends Endpoint> clazz, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        try {
            Endpoint endpoint = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            return connectToServer(endpoint, clientEndpointConfiguration, path);
        } catch (ReflectiveOperationException e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.endpointCreateFail", clazz.getName()), e);
        }
    }

    @Override // javax.websocket.WebSocketContainer
    public Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfiguration, URI path) throws DeploymentException {
        return connectToServerRecursive(endpoint, clientEndpointConfiguration, path, new HashSet());
    }

    private Session connectToServerRecursive(Endpoint endpoint, ClientEndpointConfig clientEndpointConfiguration, URI path, Set<URI> redirectSet) throws DeploymentException {
        URI proxyPath;
        String subProtocol;
        boolean secure = false;
        ByteBuffer proxyConnect = null;
        String scheme = path.getScheme();
        if ("ws".equalsIgnoreCase(scheme)) {
            proxyPath = URI.create("http" + path.toString().substring(2));
        } else if (!"wss".equalsIgnoreCase(scheme)) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.pathWrongScheme", scheme));
        } else {
            proxyPath = URI.create("https" + path.toString().substring(3));
            secure = true;
        }
        String host = path.getHost();
        if (host == null) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.pathNoHost"));
        }
        int port = path.getPort();
        SocketAddress sa = null;
        List<Proxy> proxies = ProxySelector.getDefault().select(proxyPath);
        Proxy selectedProxy = null;
        Iterator<Proxy> it = proxies.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Proxy proxy = it.next();
            if (proxy.type().equals(Proxy.Type.HTTP)) {
                sa = proxy.address();
                if (sa instanceof InetSocketAddress) {
                    InetSocketAddress inet = (InetSocketAddress) sa;
                    if (inet.isUnresolved()) {
                        sa = new InetSocketAddress(inet.getHostName(), inet.getPort());
                    }
                }
                selectedProxy = proxy;
            }
        }
        if (port == -1) {
            port = "ws".equalsIgnoreCase(scheme) ? 80 : 443;
        }
        if (sa == null) {
            sa = new InetSocketAddress(host, port);
        } else {
            proxyConnect = createProxyRequest(host, port);
        }
        Map<String, List<String>> reqHeaders = createRequestHeaders(host, port, clientEndpointConfiguration);
        clientEndpointConfiguration.getConfigurator().beforeRequest(reqHeaders);
        if (Constants.DEFAULT_ORIGIN_HEADER_VALUE != null && !reqHeaders.containsKey("Origin")) {
            List<String> originValues = new ArrayList<>(1);
            originValues.add(Constants.DEFAULT_ORIGIN_HEADER_VALUE);
            reqHeaders.put("Origin", originValues);
        }
        ByteBuffer request = createRequest(path, reqHeaders);
        try {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(getAsynchronousChannelGroup());
            Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
            String timeoutValue = (String) userProperties.get(Constants.IO_TIMEOUT_MS_PROPERTY);
            long timeout = timeoutValue != null ? Long.valueOf(timeoutValue).intValue() : 5000L;
            ByteBuffer response = ByteBuffer.allocate(getDefaultMaxBinaryMessageBufferSize());
            boolean success = false;
            List<Extension> extensionsAgreed = new ArrayList<>();
            Transformation transformation = null;
            Future<Void> fConnect = socketChannel.connect(sa);
            AsyncChannelWrapper channel = null;
            if (proxyConnect != null) {
                try {
                    fConnect.get(timeout, TimeUnit.MILLISECONDS);
                    channel = new AsyncChannelWrapperNonSecure(socketChannel);
                    writeRequest(channel, proxyConnect, timeout);
                    HttpResponse httpResponse = processResponse(response, channel, timeout);
                    if (httpResponse.getStatus() != 200) {
                        throw new DeploymentException(sm.getString("wsWebSocketContainer.proxyConnectFail", selectedProxy, Integer.toString(httpResponse.getStatus())));
                    }
                } catch (EOFException | InterruptedException | ExecutionException | TimeoutException e) {
                    if (channel != null) {
                        channel.close();
                    }
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.httpRequestFailed"), e);
                }
            }
            if (secure) {
                SSLEngine sslEngine = createSSLEngine(userProperties, host, port);
                channel = new AsyncChannelWrapperSecure(socketChannel, sslEngine);
            } else if (channel == null) {
                channel = new AsyncChannelWrapperNonSecure(socketChannel);
            }
            try {
                try {
                    fConnect.get(timeout, TimeUnit.MILLISECONDS);
                    Future<Void> fHandshake = channel.handshake();
                    fHandshake.get(timeout, TimeUnit.MILLISECONDS);
                    writeRequest(channel, request, timeout);
                    HttpResponse httpResponse2 = processResponse(response, channel, timeout);
                    String maxRedirectsValue = (String) userProperties.get(Constants.MAX_REDIRECTIONS_PROPERTY);
                    int maxRedirects = maxRedirectsValue != null ? Integer.parseInt(maxRedirectsValue) : 20;
                    if (httpResponse2.status == 101) {
                        HandshakeResponse handshakeResponse = httpResponse2.getHandshakeResponse();
                        clientEndpointConfiguration.getConfigurator().afterResponse(handshakeResponse);
                        List<String> protocolHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Protocol");
                        if (protocolHeaders == null || protocolHeaders.size() == 0) {
                            subProtocol = null;
                        } else if (protocolHeaders.size() != 1) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidSubProtocol"));
                        } else {
                            subProtocol = protocolHeaders.get(0);
                        }
                        List<String> extHeaders = handshakeResponse.getHeaders().get("Sec-WebSocket-Extensions");
                        if (extHeaders != null) {
                            for (String extHeader : extHeaders) {
                                Util.parseExtensionHeader(extensionsAgreed, extHeader);
                            }
                        }
                        TransformationFactory factory = TransformationFactory.getInstance();
                        for (Extension extension : extensionsAgreed) {
                            List<List<Extension.Parameter>> wrapper = new ArrayList<>(1);
                            wrapper.add(extension.getParameters());
                            Transformation t = factory.create(extension.getName(), wrapper, false);
                            if (t == null) {
                                throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidExtensionParameters"));
                            }
                            if (transformation == null) {
                                transformation = t;
                            } else {
                                transformation.setNext(t);
                            }
                        }
                        success = true;
                        WsRemoteEndpointImplClient wsRemoteEndpointClient = new WsRemoteEndpointImplClient(channel);
                        WsSession wsSession = new WsSession(endpoint, wsRemoteEndpointClient, this, null, null, null, null, null, extensionsAgreed, subProtocol, Collections.emptyMap(), secure, clientEndpointConfiguration);
                        WsFrameClient wsFrameClient = new WsFrameClient(response, channel, wsSession, transformation);
                        wsRemoteEndpointClient.setTransformation(wsFrameClient.getTransformation());
                        endpoint.onOpen(wsSession, clientEndpointConfiguration);
                        registerSession(endpoint, wsSession);
                        wsFrameClient.startInputProcessing();
                        return wsSession;
                    } else if (isRedirectStatus(httpResponse2.status)) {
                        List<String> locationHeader = httpResponse2.getHandshakeResponse().getHeaders().get("Location");
                        if (locationHeader == null || locationHeader.isEmpty() || locationHeader.get(0) == null || locationHeader.get(0).isEmpty()) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingLocationHeader", Integer.toString(httpResponse2.status)));
                        }
                        URI redirectLocation = URI.create(locationHeader.get(0)).normalize();
                        if (!redirectLocation.isAbsolute()) {
                            redirectLocation = path.resolve(redirectLocation);
                        }
                        String redirectScheme = redirectLocation.getScheme().toLowerCase();
                        if (redirectScheme.startsWith("http")) {
                            redirectLocation = new URI(redirectScheme.replace("http", "ws"), redirectLocation.getUserInfo(), redirectLocation.getHost(), redirectLocation.getPort(), redirectLocation.getPath(), redirectLocation.getQuery(), redirectLocation.getFragment());
                        }
                        if (!redirectSet.add(redirectLocation) || redirectSet.size() > maxRedirects) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.redirectThreshold", redirectLocation, Integer.toString(redirectSet.size()), Integer.toString(maxRedirects)));
                        }
                        Session connectToServerRecursive = connectToServerRecursive(endpoint, clientEndpointConfiguration, redirectLocation, redirectSet);
                        if (!success) {
                            channel.close();
                        }
                        return connectToServerRecursive;
                    } else if (httpResponse2.status == 401) {
                        if (userProperties.get("Authorization") != null) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.failedAuthentication", Integer.valueOf(httpResponse2.status)));
                        }
                        List<String> wwwAuthenticateHeaders = httpResponse2.getHandshakeResponse().getHeaders().get("WWW-Authenticate");
                        if (wwwAuthenticateHeaders == null || wwwAuthenticateHeaders.isEmpty() || wwwAuthenticateHeaders.get(0) == null || wwwAuthenticateHeaders.get(0).isEmpty()) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.missingWWWAuthenticateHeader", Integer.toString(httpResponse2.status)));
                        }
                        String authScheme = wwwAuthenticateHeaders.get(0).split("\\s+", 2)[0];
                        String requestUri = new String(request.array(), StandardCharsets.ISO_8859_1).split("\\s", 3)[1];
                        Authenticator auth = AuthenticatorFactory.getAuthenticator(authScheme);
                        if (auth == null) {
                            throw new DeploymentException(sm.getString("wsWebSocketContainer.unsupportedAuthScheme", Integer.valueOf(httpResponse2.status), authScheme));
                        }
                        userProperties.put("Authorization", auth.getAuthorization(requestUri, wwwAuthenticateHeaders.get(0), userProperties));
                        Session connectToServerRecursive2 = connectToServerRecursive(endpoint, clientEndpointConfiguration, path, redirectSet);
                        if (!success) {
                            channel.close();
                        }
                        return connectToServerRecursive2;
                    } else {
                        throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", Integer.toString(httpResponse2.status)));
                    }
                } catch (EOFException | InterruptedException | URISyntaxException | ExecutionException | TimeoutException | SSLException | AuthenticationException e2) {
                    throw new DeploymentException(sm.getString("wsWebSocketContainer.httpRequestFailed"), e2);
                }
            } finally {
                if (!success) {
                    channel.close();
                }
            }
        } catch (IOException ioe) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.asynchronousSocketChannelFail"), ioe);
        }
    }

    private static void writeRequest(AsyncChannelWrapper channel, ByteBuffer request, long timeout) throws TimeoutException, InterruptedException, ExecutionException {
        int toWrite = request.limit();
        Future<Integer> fWrite = channel.write(request);
        Integer thisWrite = fWrite.get(timeout, TimeUnit.MILLISECONDS);
        int i = toWrite;
        int intValue = thisWrite.intValue();
        while (true) {
            int toWrite2 = i - intValue;
            if (toWrite2 > 0) {
                Future<Integer> fWrite2 = channel.write(request);
                Integer thisWrite2 = fWrite2.get(timeout, TimeUnit.MILLISECONDS);
                i = toWrite2;
                intValue = thisWrite2.intValue();
            } else {
                return;
            }
        }
    }

    private static boolean isRedirectStatus(int httpResponseCode) {
        boolean isRedirect = false;
        switch (httpResponseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 305:
            case 307:
                isRedirect = true;
                break;
        }
        return isRedirect;
    }

    private static ByteBuffer createProxyRequest(String host, int port) {
        byte[] bytes = ("CONNECT " + host + ':' + port + " HTTP/1.1\r\nProxy-Connection: keep-alive\r\nConnection: keepalive\r\nHost: " + host + ':' + port + "\r\n\r\n").getBytes(StandardCharsets.ISO_8859_1);
        return ByteBuffer.wrap(bytes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerSession(Endpoint endpoint, WsSession wsSession) {
        if (!wsSession.isOpen()) {
            return;
        }
        synchronized (this.endPointSessionMapLock) {
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().register(this);
            }
            Set<WsSession> wsSessions = this.endpointSessionMap.get(endpoint);
            if (wsSessions == null) {
                wsSessions = new HashSet<>();
                this.endpointSessionMap.put(endpoint, wsSessions);
            }
            wsSessions.add(wsSession);
        }
        this.sessions.put(wsSession, wsSession);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void unregisterSession(Endpoint endpoint, WsSession wsSession) {
        synchronized (this.endPointSessionMapLock) {
            Set<WsSession> wsSessions = this.endpointSessionMap.get(endpoint);
            if (wsSessions != null) {
                wsSessions.remove(wsSession);
                if (wsSessions.size() == 0) {
                    this.endpointSessionMap.remove(endpoint);
                }
            }
            if (this.endpointSessionMap.size() == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
        this.sessions.remove(wsSession);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<Session> getOpenSessions(Endpoint endpoint) {
        HashSet<Session> result = new HashSet<>();
        synchronized (this.endPointSessionMapLock) {
            Set<WsSession> sessions = this.endpointSessionMap.get(endpoint);
            if (sessions != null) {
                result.addAll(sessions);
            }
        }
        return result;
    }

    private static Map<String, List<String>> createRequestHeaders(String host, int port, ClientEndpointConfig clientEndpointConfiguration) {
        Map<String, List<String>> headers = new HashMap<>();
        List<Extension> extensions = clientEndpointConfiguration.getExtensions();
        List<String> subProtocols = clientEndpointConfiguration.getPreferredSubprotocols();
        Map<String, Object> userProperties = clientEndpointConfiguration.getUserProperties();
        if (userProperties.get("Authorization") != null) {
            List<String> authValues = new ArrayList<>(1);
            authValues.add((String) userProperties.get("Authorization"));
            headers.put("Authorization", authValues);
        }
        List<String> hostValues = new ArrayList<>(1);
        if (port == -1) {
            hostValues.add(host);
        } else {
            hostValues.add(host + ':' + port);
        }
        headers.put("Host", hostValues);
        List<String> upgradeValues = new ArrayList<>(1);
        upgradeValues.add(Constants.UPGRADE_HEADER_VALUE);
        headers.put("Upgrade", upgradeValues);
        List<String> connectionValues = new ArrayList<>(1);
        connectionValues.add(Constants.CONNECTION_HEADER_VALUE);
        headers.put("Connection", connectionValues);
        List<String> wsVersionValues = new ArrayList<>(1);
        wsVersionValues.add(Constants.WS_VERSION_HEADER_VALUE);
        headers.put("Sec-WebSocket-Version", wsVersionValues);
        List<String> wsKeyValues = new ArrayList<>(1);
        wsKeyValues.add(generateWsKeyValue());
        headers.put("Sec-WebSocket-Key", wsKeyValues);
        if (subProtocols != null && subProtocols.size() > 0) {
            headers.put("Sec-WebSocket-Protocol", subProtocols);
        }
        if (extensions != null && extensions.size() > 0) {
            headers.put("Sec-WebSocket-Extensions", generateExtensionHeaders(extensions));
        }
        return headers;
    }

    private static List<String> generateExtensionHeaders(List<Extension> extensions) {
        List<String> result = new ArrayList<>(extensions.size());
        for (Extension extension : extensions) {
            StringBuilder header = new StringBuilder();
            header.append(extension.getName());
            for (Extension.Parameter param : extension.getParameters()) {
                header.append(';');
                header.append(param.getName());
                String value = param.getValue();
                if (value != null && value.length() > 0) {
                    header.append('=');
                    header.append(value);
                }
            }
            result.add(header.toString());
        }
        return result;
    }

    private static String generateWsKeyValue() {
        byte[] keyBytes = new byte[16];
        RANDOM.nextBytes(keyBytes);
        return Base64.encodeBase64String(keyBytes);
    }

    private static ByteBuffer createRequest(URI uri, Map<String, List<String>> reqHeaders) {
        ByteBuffer result = ByteBuffer.allocate(4096);
        result.put(GET_BYTES);
        if (null == uri.getPath() || "".equals(uri.getPath())) {
            result.put(ROOT_URI_BYTES);
        } else {
            result.put(uri.getRawPath().getBytes(StandardCharsets.ISO_8859_1));
        }
        String query = uri.getRawQuery();
        if (query != null) {
            result.put((byte) 63);
            result.put(query.getBytes(StandardCharsets.ISO_8859_1));
        }
        result.put(HTTP_VERSION_BYTES);
        for (Map.Entry<String, List<String>> entry : reqHeaders.entrySet()) {
            result = addHeader(result, entry.getKey(), entry.getValue());
        }
        result.put(CRLF);
        result.flip();
        return result;
    }

    private static ByteBuffer addHeader(ByteBuffer result, String key, List<String> values) {
        if (values.isEmpty()) {
            return result;
        }
        return putWithExpand(putWithExpand(putWithExpand(putWithExpand(result, key.getBytes(StandardCharsets.ISO_8859_1)), ": ".getBytes(StandardCharsets.ISO_8859_1)), StringUtils.join(values).getBytes(StandardCharsets.ISO_8859_1)), CRLF);
    }

    private static ByteBuffer putWithExpand(ByteBuffer input, byte[] bytes) {
        int newSize;
        if (bytes.length > input.remaining()) {
            if (bytes.length > input.capacity()) {
                newSize = 2 * bytes.length;
            } else {
                newSize = input.capacity() * 2;
            }
            ByteBuffer expanded = ByteBuffer.allocate(newSize);
            input.flip();
            expanded.put(input);
            input = expanded;
        }
        return input.put(bytes);
    }

    private HttpResponse processResponse(ByteBuffer response, AsyncChannelWrapper channel, long timeout) throws InterruptedException, ExecutionException, DeploymentException, EOFException, TimeoutException {
        Map<String, List<String>> headers = new CaseInsensitiveKeyMap<>();
        int status = 0;
        boolean readStatus = false;
        boolean readHeaders = false;
        String line = null;
        while (!readHeaders) {
            response.clear();
            Future<Integer> read = channel.read(response);
            Integer bytesRead = read.get(timeout, TimeUnit.MILLISECONDS);
            if (bytesRead.intValue() == -1) {
                throw new EOFException();
            }
            response.flip();
            while (response.hasRemaining() && !readHeaders) {
                if (line == null) {
                    line = readLine(response);
                } else {
                    line = line + readLine(response);
                }
                if ("\r\n".equals(line)) {
                    readHeaders = true;
                } else if (line.endsWith("\r\n")) {
                    if (readStatus) {
                        parseHeaders(line, headers);
                    } else {
                        status = parseStatus(line);
                        readStatus = true;
                    }
                    line = null;
                }
            }
        }
        return new HttpResponse(status, new WsHandshakeResponse(headers));
    }

    private int parseStatus(String line) throws DeploymentException {
        String[] parts = line.trim().split(" ");
        if (parts.length < 2 || (!org.apache.coyote.http11.Constants.HTTP_10.equals(parts[0]) && !org.apache.coyote.http11.Constants.HTTP_11.equals(parts[0]))) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", line));
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.invalidStatus", line));
        }
    }

    private void parseHeaders(String line, Map<String, List<String>> headers) {
        int index = line.indexOf(58);
        if (index == -1) {
            this.log.warn(sm.getString("wsWebSocketContainer.invalidHeader", line));
            return;
        }
        String headerName = line.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
        String headerValue = line.substring(index + 1).trim();
        List<String> values = headers.get(headerName);
        if (values == null) {
            values = new ArrayList<>(1);
            headers.put(headerName, values);
        }
        values.add(headerValue);
    }

    private String readLine(ByteBuffer response) {
        StringBuilder sb = new StringBuilder();
        while (response.hasRemaining()) {
            char c = (char) response.get();
            sb.append(c);
            if (c == '\n') {
                break;
            }
        }
        return sb.toString();
    }

    private SSLEngine createSSLEngine(Map<String, Object> userProperties, String host, int port) throws DeploymentException {
        try {
            SSLContext sslContext = (SSLContext) userProperties.get(Constants.SSL_CONTEXT_PROPERTY);
            if (sslContext == null) {
                sslContext = SSLContext.getInstance(org.apache.tomcat.util.net.Constants.SSL_PROTO_TLS);
                String sslTrustStoreValue = (String) userProperties.get(Constants.SSL_TRUSTSTORE_PROPERTY);
                if (sslTrustStoreValue != null) {
                    String sslTrustStorePwdValue = (String) userProperties.get(Constants.SSL_TRUSTSTORE_PWD_PROPERTY);
                    if (sslTrustStorePwdValue == null) {
                        sslTrustStorePwdValue = "changeit";
                    }
                    File keyStoreFile = new File(sslTrustStoreValue);
                    KeyStore ks = KeyStore.getInstance(SSL.DEFAULT_KEYSTORE_TYPE);
                    InputStream is = new FileInputStream(keyStoreFile);
                    ks.load(is, sslTrustStorePwdValue.toCharArray());
                    if (is != null) {
                        if (0 != 0) {
                            is.close();
                        } else {
                            is.close();
                        }
                    }
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(ks);
                    sslContext.init(null, tmf.getTrustManagers(), null);
                } else {
                    sslContext.init(null, null, null);
                }
            }
            SSLEngine engine = sslContext.createSSLEngine(host, port);
            String sslProtocolsValue = (String) userProperties.get(Constants.SSL_PROTOCOLS_PROPERTY);
            if (sslProtocolsValue != null) {
                engine.setEnabledProtocols(sslProtocolsValue.split(","));
            }
            engine.setUseClientMode(true);
            SSLParameters sslParams = engine.getSSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            engine.setSSLParameters(sslParams);
            return engine;
        } catch (Exception e) {
            throw new DeploymentException(sm.getString("wsWebSocketContainer.sslEngineFail"), e);
        }
    }

    @Override // javax.websocket.WebSocketContainer
    public long getDefaultMaxSessionIdleTimeout() {
        return this.defaultMaxSessionIdleTimeout;
    }

    @Override // javax.websocket.WebSocketContainer
    public void setDefaultMaxSessionIdleTimeout(long timeout) {
        this.defaultMaxSessionIdleTimeout = timeout;
    }

    @Override // javax.websocket.WebSocketContainer
    public int getDefaultMaxBinaryMessageBufferSize() {
        return this.maxBinaryMessageBufferSize;
    }

    @Override // javax.websocket.WebSocketContainer
    public void setDefaultMaxBinaryMessageBufferSize(int max) {
        this.maxBinaryMessageBufferSize = max;
    }

    @Override // javax.websocket.WebSocketContainer
    public int getDefaultMaxTextMessageBufferSize() {
        return this.maxTextMessageBufferSize;
    }

    @Override // javax.websocket.WebSocketContainer
    public void setDefaultMaxTextMessageBufferSize(int max) {
        this.maxTextMessageBufferSize = max;
    }

    @Override // javax.websocket.WebSocketContainer
    public Set<Extension> getInstalledExtensions() {
        return Collections.emptySet();
    }

    @Override // javax.websocket.WebSocketContainer
    public long getDefaultAsyncSendTimeout() {
        return this.defaultAsyncTimeout;
    }

    @Override // javax.websocket.WebSocketContainer
    public void setAsyncSendTimeout(long timeout) {
        this.defaultAsyncTimeout = timeout;
    }

    public void destroy() {
        CloseReason cr = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, sm.getString("wsWebSocketContainer.shutdown"));
        for (WsSession session : this.sessions.keySet()) {
            try {
                session.close(cr);
            } catch (IOException ioe) {
                this.log.debug(sm.getString("wsWebSocketContainer.sessionCloseFail", session.getId()), ioe);
            }
        }
        if (this.asynchronousChannelGroup != null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup != null) {
                    AsyncChannelGroupUtil.unregister();
                    this.asynchronousChannelGroup = null;
                }
            }
        }
    }

    private AsynchronousChannelGroup getAsynchronousChannelGroup() {
        AsynchronousChannelGroup result = this.asynchronousChannelGroup;
        if (result == null) {
            synchronized (this.asynchronousChannelGroupLock) {
                if (this.asynchronousChannelGroup == null) {
                    this.asynchronousChannelGroup = AsyncChannelGroupUtil.register();
                }
                result = this.asynchronousChannelGroup;
            }
        }
        return result;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void backgroundProcess() {
        this.backgroundProcessCount++;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            for (WsSession wsSession : this.sessions.keySet()) {
                wsSession.checkExpiration();
            }
        }
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/WsWebSocketContainer$HttpResponse.class */
    public static class HttpResponse {
        private final int status;
        private final HandshakeResponse handshakeResponse;

        public HttpResponse(int status, HandshakeResponse handshakeResponse) {
            this.status = status;
            this.handshakeResponse = handshakeResponse;
        }

        public int getStatus() {
            return this.status;
        }

        public HandshakeResponse getHandshakeResponse() {
            return this.handshakeResponse;
        }
    }
}