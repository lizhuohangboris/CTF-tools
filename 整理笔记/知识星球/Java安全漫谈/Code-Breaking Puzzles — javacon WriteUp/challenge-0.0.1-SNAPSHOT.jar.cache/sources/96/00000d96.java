package org.apache.tomcat.util.net.openssl;

import java.util.List;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/OpenSSLUtil.class */
public class OpenSSLUtil extends SSLUtilBase {
    private static final Log log = LogFactory.getLog(OpenSSLUtil.class);
    private final JSSEUtil jsseUtil;

    public OpenSSLUtil(SSLHostConfigCertificate certificate) {
        super(certificate);
        if (certificate.getCertificateFile() == null) {
            this.jsseUtil = new JSSEUtil(certificate);
        } else {
            this.jsseUtil = null;
        }
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedProtocols() {
        return OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedCiphers() {
        return OpenSSLEngine.AVAILABLE_CIPHER_SUITES;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public SSLContext createSSLContext(List<String> negotiableProtocols) throws Exception {
        return new OpenSSLContext(this.certificate, negotiableProtocols);
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public KeyManager[] getKeyManagers() throws Exception {
        if (this.jsseUtil != null) {
            return this.jsseUtil.getKeyManagers();
        }
        KeyManager[] managers = {new OpenSSLKeyManager(SSLHostConfig.adjustRelativePath(this.certificate.getCertificateFile()), SSLHostConfig.adjustRelativePath(this.certificate.getCertificateKeyFile()))};
        return managers;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public TrustManager[] getTrustManagers() throws Exception {
        if (this.jsseUtil != null) {
            return this.jsseUtil.getTrustManagers();
        }
        return null;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public void configureSessionContext(SSLSessionContext sslSessionContext) {
        if (this.jsseUtil != null) {
            this.jsseUtil.configureSessionContext(sslSessionContext);
        }
    }
}