package org.springframework.boot.web.embedded.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentManager;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.xnio.channels.BoundChannel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServer.class */
public class UndertowServletWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog(UndertowServletWebServer.class);
    private final Object monitor;
    private final Undertow.Builder builder;
    private final DeploymentManager manager;
    private final String contextPath;
    private final boolean useForwardHeaders;
    private final boolean autoStart;
    private final Compression compression;
    private final String serverHeader;
    private Undertow undertow;
    private volatile boolean started;

    public UndertowServletWebServer(Undertow.Builder builder, DeploymentManager manager, String contextPath, boolean autoStart, Compression compression) {
        this(builder, manager, contextPath, false, autoStart, compression);
    }

    public UndertowServletWebServer(Undertow.Builder builder, DeploymentManager manager, String contextPath, boolean useForwardHeaders, boolean autoStart, Compression compression) {
        this(builder, manager, contextPath, useForwardHeaders, autoStart, compression, null);
    }

    public UndertowServletWebServer(Undertow.Builder builder, DeploymentManager manager, String contextPath, boolean useForwardHeaders, boolean autoStart, Compression compression, String serverHeader) {
        this.monitor = new Object();
        this.started = false;
        this.builder = builder;
        this.manager = manager;
        this.contextPath = contextPath;
        this.useForwardHeaders = useForwardHeaders;
        this.autoStart = autoStart;
        this.compression = compression;
        this.serverHeader = serverHeader;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            try {
                if (this.autoStart) {
                    if (this.undertow == null) {
                        this.undertow = createUndertowServer();
                    }
                    this.undertow.start();
                    this.started = true;
                    logger.info("Undertow started on port(s) " + getPortsDescription() + " with context path '" + this.contextPath + "'");
                }
            } catch (Exception ex) {
                if (findBindException(ex) != null) {
                    List<Port> failedPorts = getConfiguredPorts();
                    List<Port> actualPorts = getActualPorts();
                    failedPorts.removeAll(actualPorts);
                    if (failedPorts.size() == 1) {
                        throw new PortInUseException(failedPorts.iterator().next().getNumber());
                    }
                }
                throw new WebServerException("Unable to start embedded Undertow", ex);
            }
        }
    }

    public DeploymentManager getDeploymentManager() {
        DeploymentManager deploymentManager;
        synchronized (this.monitor) {
            deploymentManager = this.manager;
        }
        return deploymentManager;
    }

    private void stopSilently() {
        try {
            if (this.undertow != null) {
                this.undertow.stop();
            }
        } catch (Exception e) {
        }
    }

    private BindException findBindException(Exception ex) {
        Throwable th = ex;
        while (true) {
            Throwable candidate = th;
            if (candidate != null) {
                if (candidate instanceof BindException) {
                    return (BindException) candidate;
                }
                th = candidate.getCause();
            } else {
                return null;
            }
        }
    }

    private Undertow createUndertowServer() throws ServletException {
        HttpHandler httpHandler = getContextHandler(this.manager.start());
        if (this.useForwardHeaders) {
            httpHandler = Handlers.proxyPeerAddress(httpHandler);
        }
        if (StringUtils.hasText(this.serverHeader)) {
            httpHandler = Handlers.header(httpHandler, HttpHeaders.SERVER, this.serverHeader);
        }
        this.builder.setHandler(httpHandler);
        return this.builder.build();
    }

    private HttpHandler getContextHandler(HttpHandler httpHandler) {
        HttpHandler contextHandler = UndertowCompressionConfigurer.configureCompression(this.compression, httpHandler);
        if (StringUtils.isEmpty(this.contextPath)) {
            return contextHandler;
        }
        return Handlers.path().addPrefixPath(this.contextPath, contextHandler);
    }

    private String getPortsDescription() {
        List<Port> ports = getActualPorts();
        if (!ports.isEmpty()) {
            return StringUtils.collectionToDelimitedString(ports, " ");
        }
        return "unknown";
    }

    private List<Port> getActualPorts() {
        List<Port> ports = new ArrayList<>();
        try {
            if (!this.autoStart) {
                ports.add(new Port(-1, "unknown"));
            } else {
                for (BoundChannel channel : extractChannels()) {
                    ports.add(getPortFromChannel(channel));
                }
            }
        } catch (Exception e) {
        }
        return ports;
    }

    private List<BoundChannel> extractChannels() {
        Field channelsField = ReflectionUtils.findField(Undertow.class, "channels");
        ReflectionUtils.makeAccessible(channelsField);
        return (List) ReflectionUtils.getField(channelsField, this.undertow);
    }

    private Port getPortFromChannel(BoundChannel channel) {
        SocketAddress socketAddress = channel.getLocalAddress();
        if (socketAddress instanceof InetSocketAddress) {
            String protocol = ReflectionUtils.findField(channel.getClass(), "ssl") != null ? "https" : "http";
            return new Port(((InetSocketAddress) socketAddress).getPort(), protocol);
        }
        return null;
    }

    private List<Port> getConfiguredPorts() {
        List<Port> ports = new ArrayList<>();
        for (Object listener : extractListeners()) {
            try {
                Port port = getPortFromListener(listener);
                if (port.getNumber() != 0) {
                    ports.add(port);
                }
            } catch (Exception e) {
            }
        }
        return ports;
    }

    private List<Object> extractListeners() {
        Field listenersField = ReflectionUtils.findField(Undertow.class, "listeners");
        ReflectionUtils.makeAccessible(listenersField);
        return (List) ReflectionUtils.getField(listenersField, this.undertow);
    }

    private Port getPortFromListener(Object listener) {
        Field typeField = ReflectionUtils.findField(listener.getClass(), "type");
        ReflectionUtils.makeAccessible(typeField);
        String protocol = ReflectionUtils.getField(typeField, listener).toString();
        Field portField = ReflectionUtils.findField(listener.getClass(), "port");
        ReflectionUtils.makeAccessible(portField);
        int port = ((Integer) ReflectionUtils.getField(portField, listener)).intValue();
        return new Port(port, protocol);
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        synchronized (this.monitor) {
            if (this.started) {
                this.started = false;
                try {
                    this.manager.stop();
                    this.manager.undeploy();
                    this.undertow.stop();
                } catch (Exception ex) {
                    throw new WebServerException("Unable to stop undertow", ex);
                }
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        List<Port> ports = getActualPorts();
        if (ports.isEmpty()) {
            return 0;
        }
        return ports.get(0).getNumber();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServer$Port.class */
    public static final class Port {
        private final int number;
        private final String protocol;

        private Port(int number, String protocol) {
            this.number = number;
            this.protocol = protocol;
        }

        public int getNumber() {
            return this.number;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Port other = (Port) obj;
            if (this.number != other.number) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.number;
        }

        public String toString() {
            return this.number + " (" + this.protocol + ")";
        }
    }
}