package org.springframework.boot.web.embedded.jetty;

import java.net.BindException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyWebServer.class */
public class JettyWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog(JettyWebServer.class);
    private final Object monitor;
    private final Server server;
    private final boolean autoStart;
    private Connector[] connectors;
    private volatile boolean started;

    public JettyWebServer(Server server) {
        this(server, true);
    }

    public JettyWebServer(Server server, boolean autoStart) {
        this.monitor = new Object();
        this.autoStart = autoStart;
        Assert.notNull(server, "Jetty Server must not be null");
        this.server = server;
        initialize();
    }

    private void initialize() {
        synchronized (this.monitor) {
            this.connectors = this.server.getConnectors();
            this.server.addBean(new AbstractLifeCycle() { // from class: org.springframework.boot.web.embedded.jetty.JettyWebServer.1
                protected void doStart() throws Exception {
                    Connector[] connectorArr;
                    for (Connector connector : JettyWebServer.this.connectors) {
                        Assert.state(connector.isStopped(), () -> {
                            return "Connector " + connector + " has been started prematurely";
                        });
                    }
                    JettyWebServer.this.server.setConnectors((Connector[]) null);
                }
            });
            this.server.start();
            this.server.setStopAtShutdown(false);
        }
    }

    private void stopSilently() {
        try {
            this.server.stop();
        } catch (Exception e) {
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        Handler[] handlers;
        NetworkConnector[] connectors;
        synchronized (this.monitor) {
            if (this.started) {
                return;
            }
            this.server.setConnectors(this.connectors);
            if (this.autoStart) {
                try {
                    this.server.start();
                    for (Handler handler : this.server.getHandlers()) {
                        handleDeferredInitialize(handler);
                    }
                    for (NetworkConnector networkConnector : this.server.getConnectors()) {
                        try {
                            networkConnector.start();
                        } catch (BindException ex) {
                            if (networkConnector instanceof NetworkConnector) {
                                throw new PortInUseException(networkConnector.getPort());
                            }
                            throw ex;
                        }
                    }
                    this.started = true;
                    logger.info("Jetty started on port(s) " + getActualPortsDescription() + " with context path '" + getContextPath() + "'");
                } catch (WebServerException ex2) {
                    stopSilently();
                    throw ex2;
                } catch (Exception ex3) {
                    stopSilently();
                    throw new WebServerException("Unable to start embedded Jetty server", ex3);
                }
            }
        }
    }

    private String getActualPortsDescription() {
        Connector[] connectors;
        StringBuilder ports = new StringBuilder();
        for (Connector connector : this.server.getConnectors()) {
            if (ports.length() != 0) {
                ports.append(", ");
            }
            ports.append(getLocalPort(connector)).append(getProtocols(connector));
        }
        return ports.toString();
    }

    private Integer getLocalPort(Connector connector) {
        try {
            return (Integer) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(connector.getClass(), "getLocalPort"), connector);
        } catch (Exception ex) {
            logger.info("could not determine port ( " + ex.getMessage() + ")");
            return 0;
        }
    }

    private String getProtocols(Connector connector) {
        List<String> protocols = connector.getProtocols();
        return " (" + StringUtils.collectionToDelimitedString(protocols, ", ") + ")";
    }

    private String getContextPath() {
        Stream stream = Arrays.stream(this.server.getHandlers());
        ContextHandler.class.getClass();
        Stream filter = stream.filter((v1) -> {
            return r1.isInstance(v1);
        });
        ContextHandler.class.getClass();
        return (String) filter.map((v1) -> {
            return r1.cast(v1);
        }).map((v0) -> {
            return v0.getContextPath();
        }).collect(Collectors.joining(" "));
    }

    private void handleDeferredInitialize(Handler... handlers) throws Exception {
        for (Handler handler : handlers) {
            if (handler instanceof JettyEmbeddedWebAppContext) {
                ((JettyEmbeddedWebAppContext) handler).deferredInitialize();
            } else if (handler instanceof HandlerWrapper) {
                handleDeferredInitialize(((HandlerWrapper) handler).getHandler());
            } else if (handler instanceof HandlerCollection) {
                handleDeferredInitialize(((HandlerCollection) handler).getHandlers());
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() {
        synchronized (this.monitor) {
            this.started = false;
            try {
                this.server.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                throw new WebServerException("Unable to stop embedded Jetty server", ex);
            }
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        Connector[] connectors = this.server.getConnectors();
        if (0 < connectors.length) {
            Connector connector = connectors[0];
            return getLocalPort(connector).intValue();
        }
        return 0;
    }

    public Server getServer() {
        return this.server;
    }
}