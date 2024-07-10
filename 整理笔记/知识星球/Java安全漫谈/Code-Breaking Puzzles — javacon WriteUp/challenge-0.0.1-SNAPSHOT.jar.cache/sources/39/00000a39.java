package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import org.apache.coyote.UpgradeToken;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/upgrade/UpgradeProcessorExternal.class */
public class UpgradeProcessorExternal extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog(UpgradeProcessorExternal.class);
    private static final StringManager sm = StringManager.getManager(UpgradeProcessorExternal.class);
    private final UpgradeServletInputStream upgradeServletInputStream;
    private final UpgradeServletOutputStream upgradeServletOutputStream;

    public UpgradeProcessorExternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken) {
        super(upgradeToken);
        this.upgradeServletInputStream = new UpgradeServletInputStream(this, wrapper);
        this.upgradeServletOutputStream = new UpgradeServletOutputStream(this, wrapper);
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        this.upgradeServletInputStream.close();
        this.upgradeServletOutputStream.close();
    }

    @Override // javax.servlet.http.WebConnection
    public ServletInputStream getInputStream() throws IOException {
        return this.upgradeServletInputStream;
    }

    @Override // javax.servlet.http.WebConnection
    public ServletOutputStream getOutputStream() throws IOException {
        return this.upgradeServletOutputStream;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public final AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        if (status == SocketEvent.OPEN_READ) {
            this.upgradeServletInputStream.onDataAvailable();
        } else if (status == SocketEvent.OPEN_WRITE) {
            this.upgradeServletOutputStream.onWritePossible();
        } else if (status == SocketEvent.STOP) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeProcessor.stop"));
            }
            try {
                this.upgradeServletInputStream.close();
            } catch (IOException ioe) {
                log.debug(sm.getString("upgradeProcessor.isCloseFail", ioe));
            }
            try {
                this.upgradeServletOutputStream.close();
            } catch (IOException ioe2) {
                log.debug(sm.getString("upgradeProcessor.osCloseFail", ioe2));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        } else {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeProcessor.unexpectedState"));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.upgradeServletInputStream.isClosed() && this.upgradeServletOutputStream.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("upgradeProcessor.requiredClose", Boolean.valueOf(this.upgradeServletInputStream.isClosed()), Boolean.valueOf(this.upgradeServletOutputStream.isClosed())));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.UPGRADED;
    }

    @Override // org.apache.coyote.Processor
    public final void setSslSupport(SSLSupport sslSupport) {
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
    }
}