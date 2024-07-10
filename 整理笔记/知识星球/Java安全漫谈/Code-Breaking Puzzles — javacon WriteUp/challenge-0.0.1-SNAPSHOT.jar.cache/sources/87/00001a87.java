package org.springframework.boot.web.embedded.netty;

import java.time.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.util.Assert;
import reactor.netty.ChannelBindException;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/netty/NettyWebServer.class */
public class NettyWebServer implements WebServer {
    private static final Log logger = LogFactory.getLog(NettyWebServer.class);
    private final HttpServer httpServer;
    private final ReactorHttpHandlerAdapter handlerAdapter;
    private final Duration lifecycleTimeout;
    private DisposableServer disposableServer;

    public NettyWebServer(HttpServer httpServer, ReactorHttpHandlerAdapter handlerAdapter, Duration lifecycleTimeout) {
        Assert.notNull(httpServer, "HttpServer must not be null");
        Assert.notNull(handlerAdapter, "HandlerAdapter must not be null");
        this.httpServer = httpServer;
        this.handlerAdapter = handlerAdapter;
        this.lifecycleTimeout = lifecycleTimeout;
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void start() throws WebServerException {
        if (this.disposableServer == null) {
            try {
                this.disposableServer = startHttpServer();
                logger.info("Netty started on port(s): " + getPort());
                startDaemonAwaitThread(this.disposableServer);
            } catch (Exception ex) {
                ChannelBindException bindException = findBindException(ex);
                if (bindException != null) {
                    throw new PortInUseException(bindException.localPort());
                }
                throw new WebServerException("Unable to start Netty", ex);
            }
        }
    }

    private DisposableServer startHttpServer() {
        if (this.lifecycleTimeout != null) {
            return this.httpServer.handle(this.handlerAdapter).bindNow(this.lifecycleTimeout);
        }
        return this.httpServer.handle(this.handlerAdapter).bindNow();
    }

    private ChannelBindException findBindException(Exception ex) {
        Throwable th = ex;
        while (true) {
            Throwable candidate = th;
            if (candidate != null) {
                if (candidate instanceof ChannelBindException) {
                    return (ChannelBindException) candidate;
                }
                th = candidate.getCause();
            } else {
                return null;
            }
        }
    }

    private void startDaemonAwaitThread(final DisposableServer disposableServer) {
        Thread awaitThread = new Thread("server") { // from class: org.springframework.boot.web.embedded.netty.NettyWebServer.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                disposableServer.onDispose().block();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override // org.springframework.boot.web.server.WebServer
    public void stop() throws WebServerException {
        if (this.disposableServer != null) {
            if (this.lifecycleTimeout != null) {
                this.disposableServer.disposeNow(this.lifecycleTimeout);
            } else {
                this.disposableServer.disposeNow();
            }
            this.disposableServer = null;
        }
    }

    @Override // org.springframework.boot.web.server.WebServer
    public int getPort() {
        if (this.disposableServer != null) {
            return this.disposableServer.port();
        }
        return 0;
    }
}