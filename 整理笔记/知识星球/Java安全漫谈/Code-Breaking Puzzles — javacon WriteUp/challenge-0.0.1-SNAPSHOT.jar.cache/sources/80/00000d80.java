package org.apache.tomcat.util.net.jsse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLSessionManager;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/JSSESupport.class */
public class JSSESupport implements SSLSupport, SSLSessionManager {
    private static final Log log = LogFactory.getLog(JSSESupport.class);
    private static final StringManager sm = StringManager.getManager(JSSESupport.class);
    private static final Map<String, Integer> keySizeCache = new HashMap();
    private SSLSession session;

    static {
        Cipher[] values;
        for (Cipher cipher : Cipher.values()) {
            for (String jsseName : cipher.getJsseNames()) {
                keySizeCache.put(jsseName, Integer.valueOf(cipher.getStrength_bits()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void init() {
    }

    public JSSESupport(SSLSession session) {
        this.session = session;
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getCipherSuite() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getCipherSuite();
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        if (this.session == null) {
            return null;
        }
        try {
            Certificate[] certs = this.session.getPeerCertificates();
            if (certs == null) {
                return null;
            }
            X509Certificate[] x509Certs = new X509Certificate[certs.length];
            for (int i = 0; i < certs.length; i++) {
                if (certs[i] instanceof X509Certificate) {
                    x509Certs[i] = (X509Certificate) certs[i];
                } else {
                    try {
                        byte[] buffer = certs[i].getEncoded();
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
                        x509Certs[i] = (X509Certificate) cf.generateCertificate(stream);
                    } catch (Exception ex) {
                        log.info(sm.getString("jseeSupport.certTranslationError", certs[i]), ex);
                        return null;
                    }
                }
                if (log.isTraceEnabled()) {
                    log.trace("Cert #" + i + " = " + x509Certs[i]);
                }
            }
            if (x509Certs.length < 1) {
                return null;
            }
            return x509Certs;
        } catch (Throwable t) {
            log.debug(sm.getString("jsseSupport.clientCertError"), t);
            return null;
        }
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public Integer getKeySize() throws IOException {
        if (this.session == null) {
            return null;
        }
        return keySizeCache.get(this.session.getCipherSuite());
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getSessionId() throws IOException {
        byte[] ssl_session;
        if (this.session == null || (ssl_session = this.session.getId()) == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : ssl_session) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                buf.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            buf.append(digit);
        }
        return buf.toString();
    }

    public void setSession(SSLSession session) {
        this.session = session;
    }

    @Override // org.apache.tomcat.util.net.SSLSessionManager
    public void invalidateSession() {
        this.session.invalidate();
    }

    @Override // org.apache.tomcat.util.net.SSLSupport
    public String getProtocol() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getProtocol();
    }
}