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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/upgrade/UpgradeProcessorInternal.class */
public class UpgradeProcessorInternal extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog(UpgradeProcessorInternal.class);
    private final InternalHttpUpgradeHandler internalHttpUpgradeHandler;

    public UpgradeProcessorInternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken) {
        super(upgradeToken);
        this.internalHttpUpgradeHandler = (InternalHttpUpgradeHandler) upgradeToken.getHttpUpgradeHandler();
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
        this.internalHttpUpgradeHandler.setSocketWrapper(wrapper);
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        return this.internalHttpUpgradeHandler.upgradeDispatch(status);
    }

    @Override // org.apache.coyote.Processor
    public final void setSslSupport(SSLSupport sslSupport) {
        this.internalHttpUpgradeHandler.setSslSupport(sslSupport);
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
        this.internalHttpUpgradeHandler.pause();
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        this.internalHttpUpgradeHandler.destroy();
    }

    @Override // javax.servlet.http.WebConnection
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override // javax.servlet.http.WebConnection
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
}