package ch.qos.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/net/SocketNode.class */
public class SocketNode implements Runnable {
    Socket socket;
    LoggerContext context;
    HardenedLoggingEventInputStream hardenedLoggingEventInputStream;
    SocketAddress remoteSocketAddress;
    Logger logger;
    boolean closed = false;
    SimpleSocketServer socketServer;

    public SocketNode(SimpleSocketServer socketServer, Socket socket, LoggerContext context) {
        this.socketServer = socketServer;
        this.socket = socket;
        this.remoteSocketAddress = socket.getRemoteSocketAddress();
        this.context = context;
        this.logger = context.getLogger(SocketNode.class);
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.hardenedLoggingEventInputStream = new HardenedLoggingEventInputStream(new BufferedInputStream(this.socket.getInputStream()));
        } catch (Exception e) {
            this.logger.error("Could not open ObjectInputStream to " + this.socket, (Throwable) e);
            this.closed = true;
        }
        while (!this.closed) {
            try {
                ILoggingEvent event = (ILoggingEvent) this.hardenedLoggingEventInputStream.readObject();
                Logger remoteLogger = this.context.getLogger(event.getLoggerName());
                if (remoteLogger.isEnabledFor(event.getLevel())) {
                    remoteLogger.callAppenders(event);
                }
            } catch (EOFException e2) {
                this.logger.info("Caught java.io.EOFException closing connection.");
            } catch (SocketException e3) {
                this.logger.info("Caught java.net.SocketException closing connection.");
            } catch (IOException e4) {
                this.logger.info("Caught java.io.IOException: " + e4);
                this.logger.info("Closing connection.");
            } catch (Exception e5) {
                this.logger.error("Unexpected exception. Closing connection.", (Throwable) e5);
            }
        }
        this.socketServer.socketNodeClosing(this);
        close();
    }

    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            if (this.hardenedLoggingEventInputStream != null) {
                try {
                    this.hardenedLoggingEventInputStream.close();
                    this.hardenedLoggingEventInputStream = null;
                } catch (IOException e) {
                    this.logger.warn("Could not close connection.", (Throwable) e);
                    this.hardenedLoggingEventInputStream = null;
                }
            }
        } catch (Throwable th) {
            this.hardenedLoggingEventInputStream = null;
            throw th;
        }
    }

    public String toString() {
        return getClass().getName() + this.remoteSocketAddress.toString();
    }
}