package org.apache.tomcat.util.net.jsse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.tomcat.util.net.SSLContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/JSSESSLContext.class */
class JSSESSLContext implements SSLContext {
    private javax.net.ssl.SSLContext context;
    private KeyManager[] kms;
    private TrustManager[] tms;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSSESSLContext(String protocol) throws NoSuchAlgorithmException {
        this.context = javax.net.ssl.SSLContext.getInstance(protocol);
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) throws KeyManagementException {
        this.kms = kms;
        this.tms = tms;
        this.context.init(kms, tms, sr);
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public void destroy() {
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLSessionContext getServerSessionContext() {
        return this.context.getServerSessionContext();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLEngine createSSLEngine() {
        return this.context.createSSLEngine();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLServerSocketFactory getServerSocketFactory() {
        return this.context.getServerSocketFactory();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLParameters getSupportedSSLParameters() {
        return this.context.getSupportedSSLParameters();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] result = null;
        if (this.kms != null) {
            for (int i = 0; i < this.kms.length && result == null; i++) {
                if (this.kms[i] instanceof X509KeyManager) {
                    result = ((X509KeyManager) this.kms[i]).getCertificateChain(alias);
                }
            }
        }
        return result;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getAcceptedIssuers() {
        TrustManager[] trustManagerArr;
        X509Certificate[] accepted;
        Set<X509Certificate> certs = new HashSet<>();
        if (this.tms != null) {
            for (TrustManager tm : this.tms) {
                if ((tm instanceof X509TrustManager) && (accepted = ((X509TrustManager) tm).getAcceptedIssuers()) != null) {
                    for (X509Certificate c : accepted) {
                        certs.add(c);
                    }
                }
            }
        }
        return (X509Certificate[]) certs.toArray(new X509Certificate[certs.size()]);
    }
}