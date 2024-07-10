package org.apache.tomcat.util.net.jsse;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathParameters;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreVendor;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.net.Constants;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/JSSEUtil.class */
public class JSSEUtil extends SSLUtilBase {
    private static final Log log = LogFactory.getLog(JSSEUtil.class);
    private static final StringManager sm = StringManager.getManager(JSSEUtil.class);
    private static final Set<String> implementedProtocols;
    private static final Set<String> implementedCiphers;
    private final SSLHostConfig sslHostConfig;

    static {
        try {
            SSLContext context = new JSSESSLContext(Constants.SSL_PROTO_TLS);
            context.init(null, null, null);
            String[] implementedProtocolsArray = context.getSupportedSSLParameters().getProtocols();
            implementedProtocols = new HashSet(implementedProtocolsArray.length);
            for (String protocol : implementedProtocolsArray) {
                String protocolUpper = protocol.toUpperCase(Locale.ENGLISH);
                if (!"SSLV2HELLO".equals(protocolUpper) && !"SSLV3".equals(protocolUpper) && protocolUpper.contains(SSL.DEFAULT_PROTOCOL)) {
                    log.debug(sm.getString("jsse.excludeProtocol", protocol));
                } else {
                    implementedProtocols.add(protocol);
                }
            }
            if (implementedProtocols.size() == 0) {
                log.warn(sm.getString("jsse.noDefaultProtocols"));
            }
            String[] implementedCipherSuiteArray = context.getSupportedSSLParameters().getCipherSuites();
            if (JreVendor.IS_IBM_JVM) {
                implementedCiphers = new HashSet(implementedCipherSuiteArray.length * 2);
                for (String name : implementedCipherSuiteArray) {
                    implementedCiphers.add(name);
                    if (name.startsWith(SSL.DEFAULT_PROTOCOL)) {
                        implementedCiphers.add(Constants.SSL_PROTO_TLS + name.substring(3));
                    }
                }
                return;
            }
            implementedCiphers = new HashSet(implementedCipherSuiteArray.length);
            implementedCiphers.addAll(Arrays.asList(implementedCipherSuiteArray));
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JSSEUtil(SSLHostConfigCertificate certificate) {
        super(certificate);
        this.sslHostConfig = certificate.getSSLHostConfig();
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedProtocols() {
        return implementedProtocols;
    }

    @Override // org.apache.tomcat.util.net.SSLUtilBase
    protected Set<String> getImplementedCiphers() {
        return implementedCiphers;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public SSLContext createSSLContext(List<String> negotiableProtocols) throws NoSuchAlgorithmException {
        return new JSSESSLContext(this.sslHostConfig.getSslProtocol());
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public KeyManager[] getKeyManagers() throws Exception {
        String keyAlias = this.certificate.getCertificateKeyAlias();
        String algorithm = this.sslHostConfig.getKeyManagerAlgorithm();
        String keyPass = this.certificate.getCertificateKeyPassword();
        if (keyPass == null) {
            keyPass = this.certificate.getCertificateKeystorePassword();
        }
        KeyStore ks = this.certificate.getCertificateKeystore();
        KeyStore ksUsed = ks;
        char[] keyPassArray = keyPass.toCharArray();
        if (ks == null) {
            if (this.certificate.getCertificateFile() == null) {
                throw new IOException(sm.getString("jsse.noCertFile"));
            }
            PEMFile privateKeyFile = new PEMFile(SSLHostConfig.adjustRelativePath(this.certificate.getCertificateKeyFile() != null ? this.certificate.getCertificateKeyFile() : this.certificate.getCertificateFile()), keyPass);
            PEMFile certificateFile = new PEMFile(SSLHostConfig.adjustRelativePath(this.certificate.getCertificateFile()));
            Collection<Certificate> chain = new ArrayList<>();
            chain.addAll(certificateFile.getCertificates());
            if (this.certificate.getCertificateChainFile() != null) {
                PEMFile certificateChainFile = new PEMFile(SSLHostConfig.adjustRelativePath(this.certificate.getCertificateChainFile()));
                chain.addAll(certificateChainFile.getCertificates());
            }
            if (keyAlias == null) {
                keyAlias = "tomcat";
            }
            ksUsed = KeyStore.getInstance(SSL.DEFAULT_KEYSTORE_TYPE);
            ksUsed.load(null, null);
            ksUsed.setKeyEntry(keyAlias, privateKeyFile.getPrivateKey(), keyPass.toCharArray(), (Certificate[]) chain.toArray(new Certificate[chain.size()]));
        } else if (keyAlias != null && !ks.isKeyEntry(keyAlias)) {
            throw new IOException(sm.getString("jsse.alias_no_key_entry", keyAlias));
        } else {
            if (keyAlias == null) {
                Enumeration<String> aliases = ks.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new IOException(sm.getString("jsse.noKeys"));
                }
                while (aliases.hasMoreElements() && keyAlias == null) {
                    keyAlias = aliases.nextElement();
                    if (!ks.isKeyEntry(keyAlias)) {
                        keyAlias = null;
                    }
                }
                if (keyAlias == null) {
                    throw new IOException(sm.getString("jsse.alias_no_key_entry", null));
                }
            }
            Key k = ks.getKey(keyAlias, keyPassArray);
            if (k != null && !"DKS".equalsIgnoreCase(this.certificate.getCertificateKeystoreType()) && "PKCS#8".equalsIgnoreCase(k.getFormat())) {
                String provider = this.certificate.getCertificateKeystoreProvider();
                if (provider == null) {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType());
                } else {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType(), provider);
                }
                ksUsed.load(null, null);
                ksUsed.setKeyEntry(keyAlias, k, keyPassArray, ks.getCertificateChain(keyAlias));
            }
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(ksUsed, keyPassArray);
        KeyManager[] kms = kmf.getKeyManagers();
        if (kms != null && ksUsed == ks) {
            String alias = keyAlias;
            if (SSL.DEFAULT_KEYSTORE_TYPE.equals(this.certificate.getCertificateKeystoreType())) {
                alias = alias.toLowerCase(Locale.ENGLISH);
            }
            for (int i = 0; i < kms.length; i++) {
                kms[i] = new JSSEKeyManager((X509KeyManager) kms[i], alias);
            }
        }
        return kms;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public TrustManager[] getTrustManagers() throws Exception {
        String className = this.sslHostConfig.getTrustManagerClassName();
        if (className != null && className.length() > 0) {
            ClassLoader classLoader = getClass().getClassLoader();
            Class<?> clazz = classLoader.loadClass(className);
            if (!TrustManager.class.isAssignableFrom(clazz)) {
                throw new InstantiationException(sm.getString("jsse.invalidTrustManagerClassName", className));
            }
            Object trustManagerObject = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            TrustManager trustManager = (TrustManager) trustManagerObject;
            return new TrustManager[]{trustManager};
        }
        TrustManager[] tms = null;
        KeyStore trustStore = this.sslHostConfig.getTruststore();
        if (trustStore != null) {
            checkTrustStoreEntries(trustStore);
            String algorithm = this.sslHostConfig.getTruststoreAlgorithm();
            String crlf = this.sslHostConfig.getCertificateRevocationListFile();
            boolean revocationEnabled = this.sslHostConfig.getRevocationEnabled();
            if ("PKIX".equalsIgnoreCase(algorithm)) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                CertPathParameters params = getParameters(crlf, trustStore, revocationEnabled);
                ManagerFactoryParameters mfp = new CertPathTrustManagerParameters(params);
                tmf.init(mfp);
                tms = tmf.getTrustManagers();
            } else {
                TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(algorithm);
                tmf2.init(trustStore);
                tms = tmf2.getTrustManagers();
                if (crlf != null && crlf.length() > 0) {
                    throw new CRLException(sm.getString("jsseUtil.noCrlSupport", algorithm));
                }
                if (this.sslHostConfig.isCertificateVerificationDepthConfigured()) {
                    log.warn(sm.getString("jsseUtil.noVerificationDepth", algorithm));
                }
            }
        }
        return tms;
    }

    private void checkTrustStoreEntries(KeyStore trustStore) throws Exception {
        Enumeration<String> aliases = trustStore.aliases();
        if (aliases != null) {
            Date now = new Date();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (trustStore.isCertificateEntry(alias)) {
                    Certificate cert = trustStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        try {
                            ((X509Certificate) cert).checkValidity(now);
                        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                            String msg = sm.getString("jsseUtil.trustedCertNotValid", alias, ((X509Certificate) cert).getSubjectDN(), e.getMessage());
                            if (log.isDebugEnabled()) {
                                log.debug(msg, e);
                            } else {
                                log.warn(msg);
                            }
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug(sm.getString("jsseUtil.trustedCertNotChecked", alias));
                    }
                }
            }
        }
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public void configureSessionContext(SSLSessionContext sslSessionContext) {
        sslSessionContext.setSessionCacheSize(this.sslHostConfig.getSessionCacheSize());
        sslSessionContext.setSessionTimeout(this.sslHostConfig.getSessionTimeout());
    }

    protected CertPathParameters getParameters(String crlf, KeyStore trustStore, boolean revocationEnabled) throws Exception {
        PKIXBuilderParameters xparams = new PKIXBuilderParameters(trustStore, new X509CertSelector());
        if (crlf != null && crlf.length() > 0) {
            Collection<? extends CRL> crls = getCRLs(crlf);
            CertStoreParameters csp = new CollectionCertStoreParameters(crls);
            CertStore store = CertStore.getInstance("Collection", csp);
            xparams.addCertStore(store);
            xparams.setRevocationEnabled(true);
        } else {
            xparams.setRevocationEnabled(revocationEnabled);
        }
        xparams.setMaxPathLength(this.sslHostConfig.getCertificateVerificationDepth());
        return xparams;
    }

    protected Collection<? extends CRL> getCRLs(String crlf) throws IOException, CRLException, CertificateException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = ConfigFileLoader.getInputStream(crlf);
            Throwable th = null;
            try {
                Collection<? extends CRL> crls = cf.generateCRLs(is);
                if (is != null) {
                    if (0 != 0) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        is.close();
                    }
                }
                return crls;
            } finally {
            }
        } catch (IOException iex) {
            throw iex;
        } catch (CRLException crle) {
            throw crle;
        } catch (CertificateException ce) {
            throw ce;
        }
    }
}