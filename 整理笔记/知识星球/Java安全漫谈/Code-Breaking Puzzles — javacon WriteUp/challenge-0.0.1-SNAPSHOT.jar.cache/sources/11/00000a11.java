package org.apache.coyote.ajp;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AprEndpoint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/ajp/AjpAprProtocol.class */
public class AjpAprProtocol extends AbstractAjpProtocol<Long> {
    private static final Log log = LogFactory.getLog(AjpAprProtocol.class);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public boolean isAprRequired() {
        return true;
    }

    public AjpAprProtocol() {
        super(new AprEndpoint());
    }

    public int getPollTime() {
        return ((AprEndpoint) getEndpoint()).getPollTime();
    }

    public void setPollTime(int pollTime) {
        ((AprEndpoint) getEndpoint()).setPollTime(pollTime);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        return "ajp-apr";
    }
}