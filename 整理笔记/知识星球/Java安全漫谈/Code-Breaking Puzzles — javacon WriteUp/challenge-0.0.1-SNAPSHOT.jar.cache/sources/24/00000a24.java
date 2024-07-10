package org.apache.coyote.http11;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11Nio2Protocol.class */
public class Http11Nio2Protocol extends AbstractHttp11JsseProtocol<Nio2Channel> {
    private static final Log log = LogFactory.getLog(Http11Nio2Protocol.class);

    public Http11Nio2Protocol() {
        super(new Nio2Endpoint());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        if (isSSLEnabled()) {
            return "https-" + getSslImplementationShortName() + "-nio2";
        }
        return "http-nio2";
    }
}