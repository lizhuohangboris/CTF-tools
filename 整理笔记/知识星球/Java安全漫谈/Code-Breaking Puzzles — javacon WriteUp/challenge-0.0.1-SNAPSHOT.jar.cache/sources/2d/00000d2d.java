package org.apache.tomcat.util.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.net.AprEndpoint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AprSSLSupport.class */
public class AprSSLSupport implements SSLSupport {
    private final AprEndpoint.AprSocketWrapper socketWrapper;
    private final String clientCertProvider;

    public AprSSLSupport(AprEndpoint.AprSocketWrapper socketWrapper, String clientCertProvider) {
        this.socketWrapper = socketWrapper;
        this.clientCertProvider = clientCertProvider;
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getCipherSuite() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(2);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        CertificateFactory cf;
        try {
            int certLength = this.socketWrapper.getSSLInfoI(1024);
            byte[] clientCert = this.socketWrapper.getSSLInfoB(SSL.SSL_INFO_CLIENT_CERT);
            X509Certificate[] certs = null;
            if (clientCert != null) {
                if (certLength < 0) {
                    certLength = 0;
                }
                certs = new X509Certificate[certLength + 1];
                if (this.clientCertProvider == null) {
                    cf = CertificateFactory.getInstance("X.509");
                } else {
                    cf = CertificateFactory.getInstance("X.509", this.clientCertProvider);
                }
                certs[0] = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(clientCert));
                for (int i = 0; i < certLength; i++) {
                    byte[] data = this.socketWrapper.getSSLInfoB(1024 + i);
                    certs[i + 1] = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(data));
                }
            }
            return certs;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public Integer getKeySize() throws IOException {
        try {
            return Integer.valueOf(this.socketWrapper.getSSLInfoI(3));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getSessionId() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(1);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getProtocol() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(7);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}