package org.apache.tomcat.util.net.openssl;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.CertificateVerifier;
import org.apache.tomcat.jni.Pool;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.jni.SSLConf;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/OpenSSLContext.class */
public class OpenSSLContext implements SSLContext {
    private static final String defaultProtocol = "TLS";
    private final SSLHostConfig sslHostConfig;
    private final SSLHostConfigCertificate certificate;
    private OpenSSLSessionContext sessionContext;
    private X509KeyManager x509KeyManager;
    private X509TrustManager x509TrustManager;
    private final List<String> negotiableProtocols;
    private String enabledProtocol;
    protected final long cctx;
    protected final long ctx;
    static final CertificateFactory X509_CERT_FACTORY;
    private static final String BEGIN_KEY = "-----BEGIN RSA PRIVATE KEY-----\n";
    private static final Log log = LogFactory.getLog(OpenSSLContext.class);
    private static final StringManager netSm = StringManager.getManager(AbstractEndpoint.class);
    private static final StringManager sm = StringManager.getManager(OpenSSLContext.class);
    private static final Object END_KEY = "\n-----END RSA PRIVATE KEY-----";
    private List<String> jsseCipherNames = new ArrayList();
    private final AtomicInteger aprPoolDestroyed = new AtomicInteger(0);
    private boolean initialized = false;
    private final long aprPool = Pool.create(0);

    static {
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new IllegalStateException(sm.getString("openssl.X509FactoryError"), e);
        }
    }

    public List<String> getJsseCipherNames() {
        return this.jsseCipherNames;
    }

    public String getEnabledProtocol() {
        return this.enabledProtocol;
    }

    public void setEnabledProtocol(String protocol) {
        this.enabledProtocol = protocol == null ? "TLS" : protocol;
    }

    public OpenSSLContext(SSLHostConfigCertificate certificate, List<String> negotiableProtocols) throws SSLException {
        String[] enabledProtocols;
        this.sslHostConfig = certificate.getSSLHostConfig();
        this.certificate = certificate;
        try {
            try {
                OpenSSLConf openSslConf = this.sslHostConfig.getOpenSslConf();
                if (openSslConf != null) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("openssl.makeConf"));
                        }
                        this.cctx = SSLConf.make(this.aprPool, 58);
                    } catch (Exception e) {
                        throw new SSLException(sm.getString("openssl.errMakeConf"), e);
                    }
                } else {
                    this.cctx = 0L;
                }
                this.sslHostConfig.setOpenSslConfContext(Long.valueOf(this.cctx));
                int value = 0;
                for (String protocol : this.sslHostConfig.getEnabledProtocols()) {
                    if (!Constants.SSL_PROTO_SSLv2Hello.equalsIgnoreCase(protocol)) {
                        if (Constants.SSL_PROTO_SSLv2.equalsIgnoreCase(protocol)) {
                            value |= 1;
                        } else if (Constants.SSL_PROTO_SSLv3.equalsIgnoreCase(protocol)) {
                            value |= 2;
                        } else if (Constants.SSL_PROTO_TLSv1.equalsIgnoreCase(protocol)) {
                            value |= 4;
                        } else if (Constants.SSL_PROTO_TLSv1_1.equalsIgnoreCase(protocol)) {
                            value |= 8;
                        } else if (Constants.SSL_PROTO_TLSv1_2.equalsIgnoreCase(protocol)) {
                            value |= 16;
                        } else if ("all".equalsIgnoreCase(protocol)) {
                            value |= 28;
                        } else {
                            throw new Exception(netSm.getString("endpoint.apr.invalidSslProtocol", protocol));
                        }
                    }
                }
                try {
                    this.ctx = org.apache.tomcat.jni.SSLContext.make(this.aprPool, value, 1);
                    this.negotiableProtocols = negotiableProtocols;
                    if (1 == 0) {
                        destroy();
                    }
                } catch (Exception e2) {
                    throw new Exception(netSm.getString("endpoint.apr.failSslContextMake"), e2);
                }
            } catch (Exception e3) {
                throw new SSLException(sm.getString("openssl.errorSSLCtxInit"), e3);
            }
        } catch (Throwable th) {
            if (0 == 0) {
                destroy();
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public synchronized void destroy() {
        if (this.aprPoolDestroyed.compareAndSet(0, 1)) {
            if (this.ctx != 0) {
                org.apache.tomcat.jni.SSLContext.free(this.ctx);
            }
            if (this.cctx != 0) {
                SSLConf.free(this.cctx);
            }
            if (this.aprPool != 0) {
                Pool.destroy(this.aprPool);
            }
        }
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public synchronized void init(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) {
        OpenSSLConf openSslConf;
        X509Certificate[] acceptedIssuers;
        if (this.initialized) {
            log.warn(sm.getString("openssl.doubleInit"));
            return;
        }
        try {
            if (this.sslHostConfig.getInsecureRenegotiation()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 262144);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 262144);
            }
            if (this.sslHostConfig.getHonorCipherOrder()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 4194304);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 4194304);
            }
            if (this.sslHostConfig.getDisableCompression()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 131072);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 131072);
            }
            if (this.sslHostConfig.getDisableSessionTickets()) {
                org.apache.tomcat.jni.SSLContext.setOptions(this.ctx, 16384);
            } else {
                org.apache.tomcat.jni.SSLContext.clearOptions(this.ctx, 16384);
            }
            if (this.sslHostConfig.getSessionCacheSize() > 0) {
                org.apache.tomcat.jni.SSLContext.setSessionCacheSize(this.ctx, this.sslHostConfig.getSessionCacheSize());
            } else {
                long sessionCacheSize = org.apache.tomcat.jni.SSLContext.setSessionCacheSize(this.ctx, 20480L);
                org.apache.tomcat.jni.SSLContext.setSessionCacheSize(this.ctx, sessionCacheSize);
            }
            if (this.sslHostConfig.getSessionTimeout() > 0) {
                org.apache.tomcat.jni.SSLContext.setSessionCacheTimeout(this.ctx, this.sslHostConfig.getSessionTimeout());
            } else {
                long sessionTimeout = org.apache.tomcat.jni.SSLContext.setSessionCacheTimeout(this.ctx, 300L);
                org.apache.tomcat.jni.SSLContext.setSessionCacheTimeout(this.ctx, sessionTimeout);
            }
            String opensslCipherConfig = this.sslHostConfig.getCiphers();
            this.jsseCipherNames = OpenSSLCipherConfigurationParser.parseExpression(opensslCipherConfig);
            org.apache.tomcat.jni.SSLContext.setCipherSuite(this.ctx, opensslCipherConfig);
            if (this.certificate.getCertificateFile() != null) {
                org.apache.tomcat.jni.SSLContext.setCertificate(this.ctx, SSLHostConfig.adjustRelativePath(this.certificate.getCertificateFile()), SSLHostConfig.adjustRelativePath(this.certificate.getCertificateKeyFile()), this.certificate.getCertificateKeyPassword(), 0);
                org.apache.tomcat.jni.SSLContext.setCertificateChainFile(this.ctx, SSLHostConfig.adjustRelativePath(this.certificate.getCertificateChainFile()), false);
                org.apache.tomcat.jni.SSLContext.setCARevocation(this.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCertificateRevocationListPath()));
            } else {
                this.x509KeyManager = chooseKeyManager(kms);
                String alias = this.certificate.getCertificateKeyAlias();
                if (alias == null) {
                    alias = "tomcat";
                }
                X509Certificate[] chain = this.x509KeyManager.getCertificateChain(alias);
                if (chain == null) {
                    alias = findAlias(this.x509KeyManager, this.certificate);
                    chain = this.x509KeyManager.getCertificateChain(alias);
                }
                PrivateKey key = this.x509KeyManager.getPrivateKey(alias);
                org.apache.tomcat.jni.SSLContext.setCertificateRaw(this.ctx, chain[0].getEncoded(), (BEGIN_KEY + Base64.getMimeEncoder(64, new byte[]{10}).encodeToString(key.getEncoded()) + END_KEY).getBytes(StandardCharsets.US_ASCII), 0);
                for (int i = 1; i < chain.length; i++) {
                    org.apache.tomcat.jni.SSLContext.addChainCertificateRaw(this.ctx, chain[i].getEncoded());
                }
            }
            int value = 0;
            switch (this.sslHostConfig.getCertificateVerification()) {
                case NONE:
                    value = 0;
                    break;
                case OPTIONAL:
                    value = 1;
                    break;
                case OPTIONAL_NO_CA:
                    value = 3;
                    break;
                case REQUIRED:
                    value = 2;
                    break;
            }
            org.apache.tomcat.jni.SSLContext.setVerify(this.ctx, value, this.sslHostConfig.getCertificateVerificationDepth());
            if (tms != null) {
                this.x509TrustManager = chooseTrustManager(tms);
                org.apache.tomcat.jni.SSLContext.setCertVerifyCallback(this.ctx, new CertificateVerifier() { // from class: org.apache.tomcat.util.net.openssl.OpenSSLContext.1
                    @Override // org.apache.tomcat.jni.CertificateVerifier
                    public boolean verify(long ssl, byte[][] chain2, String auth) {
                        X509Certificate[] peerCerts = OpenSSLContext.certificates(chain2);
                        try {
                            OpenSSLContext.this.x509TrustManager.checkClientTrusted(peerCerts, auth);
                            return true;
                        } catch (Exception e) {
                            OpenSSLContext.log.debug(OpenSSLContext.sm.getString("openssl.certificateVerificationFailed"), e);
                            return false;
                        }
                    }
                });
                for (X509Certificate caCert : this.x509TrustManager.getAcceptedIssuers()) {
                    org.apache.tomcat.jni.SSLContext.addClientCACertificateRaw(this.ctx, caCert.getEncoded());
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("openssl.addedClientCaCert", caCert.toString()));
                    }
                }
            } else {
                org.apache.tomcat.jni.SSLContext.setCACertificate(this.ctx, SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificateFile()), SSLHostConfig.adjustRelativePath(this.sslHostConfig.getCaCertificatePath()));
            }
            if (this.negotiableProtocols != null && this.negotiableProtocols.size() > 0) {
                List<String> protocols = new ArrayList<>();
                protocols.addAll(this.negotiableProtocols);
                protocols.add("http/1.1");
                String[] protocolsArray = (String[]) protocols.toArray(new String[0]);
                org.apache.tomcat.jni.SSLContext.setAlpnProtos(this.ctx, protocolsArray, 0);
                org.apache.tomcat.jni.SSLContext.setNpnProtos(this.ctx, protocolsArray, 0);
            }
            openSslConf = this.sslHostConfig.getOpenSslConf();
        } catch (Exception e) {
            log.warn(sm.getString("openssl.errorSSLCtxInit"), e);
            destroy();
            return;
        }
        if (openSslConf != null && this.cctx != 0) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("openssl.checkConf"));
            }
            try {
                if (!openSslConf.check(this.cctx)) {
                    log.error(sm.getString("openssl.errCheckConf"));
                    throw new Exception(sm.getString("openssl.errCheckConf"));
                }
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("openssl.applyConf"));
                }
                try {
                    if (!openSslConf.apply(this.cctx, this.ctx)) {
                        log.error(sm.getString("openssl.errApplyConf"));
                        throw new SSLException(sm.getString("openssl.errApplyConf"));
                    }
                    int opts = org.apache.tomcat.jni.SSLContext.getOptions(this.ctx);
                    List<String> enabled = new ArrayList<>();
                    enabled.add(Constants.SSL_PROTO_SSLv2Hello);
                    if ((opts & SSL.SSL_OP_NO_TLSv1) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1);
                    }
                    if ((opts & 268435456) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1_1);
                    }
                    if ((opts & 134217728) == 0) {
                        enabled.add(Constants.SSL_PROTO_TLSv1_2);
                    }
                    if ((opts & 16777216) == 0) {
                        enabled.add(Constants.SSL_PROTO_SSLv2);
                    }
                    if ((opts & 33554432) == 0) {
                        enabled.add(Constants.SSL_PROTO_SSLv3);
                    }
                    this.sslHostConfig.setEnabledProtocols((String[]) enabled.toArray(new String[enabled.size()]));
                    this.sslHostConfig.setEnabledCiphers(org.apache.tomcat.jni.SSLContext.getCiphers(this.ctx));
                } catch (Exception e2) {
                    throw new SSLException(sm.getString("openssl.errApplyConf"), e2);
                }
            } catch (Exception e3) {
                throw new Exception(sm.getString("openssl.errCheckConf"), e3);
            }
            log.warn(sm.getString("openssl.errorSSLCtxInit"), e);
            destroy();
            return;
        }
        this.sessionContext = new OpenSSLSessionContext(this);
        this.sessionContext.setSessionIdContext(org.apache.tomcat.jni.SSLContext.DEFAULT_SESSION_ID_CONTEXT);
        this.sslHostConfig.setOpenSslContext(Long.valueOf(this.ctx));
        this.initialized = true;
    }

    private static String findAlias(X509KeyManager keyManager, SSLHostConfigCertificate certificate) {
        SSLHostConfigCertificate.Type type = certificate.getType();
        String result = null;
        List<SSLHostConfigCertificate.Type> candidateTypes = new ArrayList<>();
        if (SSLHostConfigCertificate.Type.UNDEFINED.equals(type)) {
            candidateTypes.addAll(Arrays.asList(SSLHostConfigCertificate.Type.values()));
            candidateTypes.remove(SSLHostConfigCertificate.Type.UNDEFINED);
        } else {
            candidateTypes.add(type);
        }
        Iterator<SSLHostConfigCertificate.Type> iter = candidateTypes.iterator();
        while (result == null && iter.hasNext()) {
            result = keyManager.chooseServerAlias(iter.next().toString(), null, null);
        }
        return result;
    }

    private static X509KeyManager chooseKeyManager(KeyManager[] managers) throws Exception {
        for (KeyManager manager : managers) {
            if (manager instanceof JSSEKeyManager) {
                return (JSSEKeyManager) manager;
            }
        }
        for (KeyManager manager2 : managers) {
            if (manager2 instanceof X509KeyManager) {
                return (X509KeyManager) manager2;
            }
        }
        throw new IllegalStateException(sm.getString("openssl.keyManagerMissing"));
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] managers) {
        for (TrustManager m : managers) {
            if (m instanceof X509TrustManager) {
                return (X509TrustManager) m;
            }
        }
        throw new IllegalStateException(sm.getString("openssl.trustManagerMissing"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static X509Certificate[] certificates(byte[][] chain) {
        X509Certificate[] peerCerts = new X509Certificate[chain.length];
        for (int i = 0; i < peerCerts.length; i++) {
            peerCerts[i] = new OpenSSLX509Certificate(chain[i]);
        }
        return peerCerts;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getSSLContextID() {
        return this.ctx;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLSessionContext getServerSessionContext() {
        return this.sessionContext;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLEngine createSSLEngine() {
        return new OpenSSLEngine(this.ctx, "TLS", false, this.sessionContext, this.negotiableProtocols != null && this.negotiableProtocols.size() > 0, this.initialized);
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLServerSocketFactory getServerSocketFactory() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public SSLParameters getSupportedSSLParameters() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] chain = null;
        if (this.x509KeyManager != null) {
            if (alias == null) {
                alias = "tomcat";
            }
            chain = this.x509KeyManager.getCertificateChain(alias);
            if (chain == null) {
                chain = this.x509KeyManager.getCertificateChain(findAlias(this.x509KeyManager, this.certificate));
            }
        }
        return chain;
    }

    @Override // org.apache.tomcat.util.net.SSLContext
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] acceptedCerts = null;
        if (this.x509TrustManager != null) {
            acceptedCerts = this.x509TrustManager.getAcceptedIssuers();
        }
        return acceptedCerts;
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }
}