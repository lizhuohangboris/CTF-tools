package org.apache.tomcat.util.net;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.DomainLoadStoreParameter;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLUtilBase.class */
public abstract class SSLUtilBase implements SSLUtil {
    private static final Log log = LogFactory.getLog(SSLUtilBase.class);
    private static final StringManager sm = StringManager.getManager(SSLUtilBase.class);
    protected final SSLHostConfigCertificate certificate;
    private final String[] enabledProtocols;
    private final String[] enabledCiphers;

    protected abstract Set<String> getImplementedProtocols();

    protected abstract Set<String> getImplementedCiphers();

    protected abstract Log getLog();

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLUtilBase(SSLHostConfigCertificate certificate) {
        this.certificate = certificate;
        SSLHostConfig sslHostConfig = certificate.getSSLHostConfig();
        Set<String> configuredProtocols = sslHostConfig.getProtocols();
        Set<String> implementedProtocols = getImplementedProtocols();
        List<String> enabledProtocols = getEnabled("protocols", getLog(), true, configuredProtocols, implementedProtocols);
        if (enabledProtocols.contains(Constants.SSL_PROTO_SSLv3)) {
            log.warn(sm.getString("jsse.ssl3"));
        }
        this.enabledProtocols = (String[]) enabledProtocols.toArray(new String[enabledProtocols.size()]);
        List<String> configuredCiphers = sslHostConfig.getJsseCipherNames();
        Set<String> implementedCiphers = getImplementedCiphers();
        List<String> enabledCiphers = getEnabled("ciphers", getLog(), false, configuredCiphers, implementedCiphers);
        this.enabledCiphers = (String[]) enabledCiphers.toArray(new String[enabledCiphers.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> List<T> getEnabled(String name, Log log2, boolean warnOnSkip, Collection<T> configured, Collection<T> implemented) {
        List<T> enabled = new ArrayList<>();
        if (implemented.size() == 0) {
            enabled.addAll(configured);
        } else {
            enabled.addAll(configured);
            enabled.retainAll(implemented);
            if (enabled.isEmpty()) {
                throw new IllegalArgumentException(sm.getString("sslUtilBase.noneSupported", name, configured));
            }
            if (log2.isDebugEnabled()) {
                log2.debug(sm.getString("sslUtilBase.active", name, enabled));
            }
            if ((log2.isDebugEnabled() || warnOnSkip) && enabled.size() != configured.size()) {
                List<T> skipped = new ArrayList<>();
                skipped.addAll(configured);
                skipped.removeAll(enabled);
                String msg = sm.getString("sslUtilBase.skipped", name, skipped);
                if (warnOnSkip) {
                    log2.warn(msg);
                } else {
                    log2.debug(msg);
                }
            }
        }
        return enabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KeyStore getStore(String type, String provider, String path, String pass) throws IOException {
        KeyStore ks;
        InputStream istream = null;
        try {
            try {
                try {
                    try {
                        if (provider == null) {
                            ks = KeyStore.getInstance(type);
                        } else {
                            ks = KeyStore.getInstance(type, provider);
                        }
                        if ("DKS".equalsIgnoreCase(type)) {
                            URI uri = ConfigFileLoader.getURI(path);
                            ks.load(new DomainLoadStoreParameter(uri, Collections.emptyMap()));
                        } else {
                            if ((!"PKCS11".equalsIgnoreCase(type) && !"".equalsIgnoreCase(path)) || "NONE".equalsIgnoreCase(path)) {
                                istream = ConfigFileLoader.getInputStream(path);
                            }
                            char[] storePass = null;
                            if (pass != null && (!"".equals(pass) || SSL.DEFAULT_KEYSTORE_TYPE.equalsIgnoreCase(type) || "PKCS12".equalsIgnoreCase(type))) {
                                storePass = pass.toCharArray();
                            }
                            ks.load(istream, storePass);
                        }
                        if (istream != null) {
                            try {
                                istream.close();
                            } catch (IOException e) {
                            }
                        }
                        return ks;
                    } catch (IOException ioe) {
                        throw ioe;
                    }
                } catch (Exception ex) {
                    String msg = sm.getString("jsse.keystore_load_failed", type, path, ex.getMessage());
                    log.error(msg, ex);
                    throw new IOException(msg);
                }
            } catch (FileNotFoundException fnfe) {
                throw fnfe;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    istream.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    @Override // org.apache.tomcat.util.net.SSLUtil
    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }
}