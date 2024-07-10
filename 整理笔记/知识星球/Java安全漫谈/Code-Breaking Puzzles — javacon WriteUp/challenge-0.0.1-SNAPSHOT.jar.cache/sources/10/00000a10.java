package org.apache.coyote.ajp;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Processor;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/ajp/AbstractAjpProtocol.class */
public abstract class AbstractAjpProtocol<S> extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager(AbstractAjpProtocol.class);
    private boolean ajpFlush;
    private boolean tomcatAuthentication;
    private boolean tomcatAuthorization;
    private String requiredSecret;
    private int packetSize;

    public AbstractAjpProtocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.ajpFlush = true;
        this.tomcatAuthentication = true;
        this.tomcatAuthorization = false;
        this.requiredSecret = null;
        this.packetSize = 8192;
        setConnectionTimeout(-1);
        getEndpoint().setUseSendfile(false);
        AbstractProtocol.ConnectionHandler<S> cHandler = new AbstractProtocol.ConnectionHandler<>(this);
        setHandler(cHandler);
        getEndpoint().setHandler(cHandler);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getProtocolName() {
        return "Ajp";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected UpgradeProtocol getNegotiatedProtocol(String name) {
        return null;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected UpgradeProtocol getUpgradeProtocol(String name) {
        return null;
    }

    public boolean getAjpFlush() {
        return this.ajpFlush;
    }

    public void setAjpFlush(boolean ajpFlush) {
        this.ajpFlush = ajpFlush;
    }

    public boolean getTomcatAuthentication() {
        return this.tomcatAuthentication;
    }

    public void setTomcatAuthentication(boolean tomcatAuthentication) {
        this.tomcatAuthentication = tomcatAuthentication;
    }

    public boolean getTomcatAuthorization() {
        return this.tomcatAuthorization;
    }

    public void setTomcatAuthorization(boolean tomcatAuthorization) {
        this.tomcatAuthorization = tomcatAuthorization;
    }

    public void setRequiredSecret(String requiredSecret) {
        this.requiredSecret = requiredSecret;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getRequiredSecret() {
        return this.requiredSecret;
    }

    public int getPacketSize() {
        return this.packetSize;
    }

    public void setPacketSize(int packetSize) {
        if (packetSize < 8192) {
            this.packetSize = 8192;
        } else {
            this.packetSize = packetSize;
        }
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        getLog().warn(sm.getString("ajpprotocol.noSSL", sslHostConfig.getHostName()));
    }

    @Override // org.apache.coyote.ProtocolHandler
    public SSLHostConfig[] findSslHostConfigs() {
        return new SSLHostConfig[0];
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        getLog().warn(sm.getString("ajpprotocol.noUpgrade", upgradeProtocol.getClass().getName()));
    }

    @Override // org.apache.coyote.ProtocolHandler
    public UpgradeProtocol[] findUpgradeProtocols() {
        return new UpgradeProtocol[0];
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createProcessor() {
        AjpProcessor processor = new AjpProcessor(this, getAdapter());
        return processor;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        throw new IllegalStateException(sm.getString("ajpprotocol.noUpgradeHandler", upgradeToken.getHttpUpgradeHandler().getClass().getName()));
    }
}