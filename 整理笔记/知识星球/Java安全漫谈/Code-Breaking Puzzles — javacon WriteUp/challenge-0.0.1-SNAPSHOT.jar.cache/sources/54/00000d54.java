package org.apache.tomcat.util.net;

import ch.qos.logback.core.net.ssl.SSL;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLHostConfigCertificate.class */
public class SSLHostConfigCertificate implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Log log = LogFactory.getLog(SSLHostConfigCertificate.class);
    private static final StringManager sm = StringManager.getManager(SSLHostConfigCertificate.class);
    public static final Type DEFAULT_TYPE = Type.UNDEFINED;
    static final String DEFAULT_KEYSTORE_PROVIDER = System.getProperty("javax.net.ssl.keyStoreProvider");
    static final String DEFAULT_KEYSTORE_TYPE = System.getProperty("javax.net.ssl.keyStoreType", SSL.DEFAULT_KEYSTORE_TYPE);
    private ObjectName oname;
    private transient SSLContext sslContext;
    private final SSLHostConfig sslHostConfig;
    private final Type type;
    private String certificateKeyPassword;
    private String certificateKeyAlias;
    private String certificateKeystorePassword;
    private String certificateKeystoreFile;
    private String certificateKeystoreProvider;
    private String certificateKeystoreType;
    private transient KeyStore certificateKeystore;
    private String certificateChainFile;
    private String certificateFile;
    private String certificateKeyFile;
    private StoreType storeType;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLHostConfigCertificate$StoreType.class */
    public enum StoreType {
        KEYSTORE,
        PEM
    }

    public SSLHostConfigCertificate() {
        this(null, Type.UNDEFINED);
    }

    public SSLHostConfigCertificate(SSLHostConfig sslHostConfig, Type type) {
        this.certificateKeyPassword = null;
        this.certificateKeystorePassword = "changeit";
        this.certificateKeystoreFile = System.getProperty("user.home") + "/.keystore";
        this.certificateKeystoreProvider = DEFAULT_KEYSTORE_PROVIDER;
        this.certificateKeystoreType = DEFAULT_KEYSTORE_TYPE;
        this.certificateKeystore = null;
        this.storeType = null;
        this.sslHostConfig = sslHostConfig;
        this.type = type;
    }

    public SSLContext getSslContext() {
        return this.sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public SSLHostConfig getSSLHostConfig() {
        return this.sslHostConfig;
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    public void setObjectName(ObjectName oname) {
        this.oname = oname;
    }

    public Type getType() {
        return this.type;
    }

    public String getCertificateKeyPassword() {
        return this.certificateKeyPassword;
    }

    public void setCertificateKeyPassword(String certificateKeyPassword) {
        this.certificateKeyPassword = certificateKeyPassword;
    }

    public void setCertificateKeyAlias(String certificateKeyAlias) {
        this.sslHostConfig.setProperty("Certificate.certificateKeyAlias", SSLHostConfig.Type.JSSE);
        this.certificateKeyAlias = certificateKeyAlias;
    }

    public String getCertificateKeyAlias() {
        return this.certificateKeyAlias;
    }

    public void setCertificateKeystoreFile(String certificateKeystoreFile) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreFile", SSLHostConfig.Type.JSSE);
        setStoreType("Certificate.certificateKeystoreFile", StoreType.KEYSTORE);
        this.certificateKeystoreFile = certificateKeystoreFile;
    }

    public String getCertificateKeystoreFile() {
        return this.certificateKeystoreFile;
    }

    public void setCertificateKeystorePassword(String certificateKeystorePassword) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystorePassword", SSLHostConfig.Type.JSSE);
        setStoreType("Certificate.certificateKeystorePassword", StoreType.KEYSTORE);
        this.certificateKeystorePassword = certificateKeystorePassword;
    }

    public String getCertificateKeystorePassword() {
        return this.certificateKeystorePassword;
    }

    public void setCertificateKeystoreProvider(String certificateKeystoreProvider) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreProvider", SSLHostConfig.Type.JSSE);
        setStoreType("Certificate.certificateKeystoreProvider", StoreType.KEYSTORE);
        this.certificateKeystoreProvider = certificateKeystoreProvider;
    }

    public String getCertificateKeystoreProvider() {
        return this.certificateKeystoreProvider;
    }

    public void setCertificateKeystoreType(String certificateKeystoreType) {
        this.sslHostConfig.setProperty("Certificate.certificateKeystoreType", SSLHostConfig.Type.JSSE);
        setStoreType("Certificate.certificateKeystoreType", StoreType.KEYSTORE);
        this.certificateKeystoreType = certificateKeystoreType;
    }

    public String getCertificateKeystoreType() {
        return this.certificateKeystoreType;
    }

    public void setCertificateKeystore(KeyStore certificateKeystore) {
        this.certificateKeystore = certificateKeystore;
    }

    public KeyStore getCertificateKeystore() throws IOException {
        KeyStore result = this.certificateKeystore;
        if (result == null && this.storeType == StoreType.KEYSTORE) {
            result = SSLUtilBase.getStore(getCertificateKeystoreType(), getCertificateKeystoreProvider(), getCertificateKeystoreFile(), getCertificateKeystorePassword());
        }
        return result;
    }

    public void setCertificateChainFile(String certificateChainFile) {
        setStoreType("Certificate.certificateChainFile", StoreType.PEM);
        this.certificateChainFile = certificateChainFile;
    }

    public String getCertificateChainFile() {
        return this.certificateChainFile;
    }

    public void setCertificateFile(String certificateFile) {
        setStoreType("Certificate.certificateFile", StoreType.PEM);
        this.certificateFile = certificateFile;
    }

    public String getCertificateFile() {
        return this.certificateFile;
    }

    public void setCertificateKeyFile(String certificateKeyFile) {
        setStoreType("Certificate.certificateKeyFile", StoreType.PEM);
        this.certificateKeyFile = certificateKeyFile;
    }

    public String getCertificateKeyFile() {
        return this.certificateKeyFile;
    }

    private void setStoreType(String name, StoreType type) {
        if (this.storeType == null) {
            this.storeType = type;
        } else if (this.storeType != type) {
            log.warn(sm.getString("sslHostConfigCertificate.mismatch", name, this.sslHostConfig.getHostName(), type, this.storeType));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLHostConfigCertificate$Type.class */
    public enum Type {
        UNDEFINED(new Authentication[0]),
        RSA(Authentication.RSA),
        DSA(Authentication.DSS),
        EC(Authentication.ECDH, Authentication.ECDSA);
        
        private final Set<Authentication> compatibleAuthentications = new HashSet();

        Type(Authentication... authentications) {
            if (authentications != null) {
                for (Authentication authentication : authentications) {
                    this.compatibleAuthentications.add(authentication);
                }
            }
        }

        public boolean isCompatibleWith(Authentication au) {
            return this.compatibleAuthentications.contains(au);
        }
    }
}