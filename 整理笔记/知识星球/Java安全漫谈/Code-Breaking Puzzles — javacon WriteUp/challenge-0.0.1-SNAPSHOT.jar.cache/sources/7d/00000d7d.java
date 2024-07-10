package org.apache.tomcat.util.net.jsse;

import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/JSSEImplementation.class */
public class JSSEImplementation extends SSLImplementation {
    public JSSEImplementation() {
        JSSESupport.init();
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLSupport getSSLSupport(SSLSession session) {
        return new JSSESupport(session);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new JSSEUtil(certificate);
    }

    @Override // org.apache.tomcat.util.net.SSLImplementation
    public boolean isAlpnSupported() {
        return JreCompat.isJre9Available();
    }
}