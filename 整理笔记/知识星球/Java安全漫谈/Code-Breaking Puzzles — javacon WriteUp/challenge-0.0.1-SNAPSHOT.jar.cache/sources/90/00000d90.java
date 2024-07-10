package org.apache.tomcat.util.net.openssl;

import java.io.File;
import javax.net.ssl.KeyManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/OpenSSLKeyManager.class */
public class OpenSSLKeyManager implements KeyManager {
    private File certificateChain;
    private File privateKey;

    public File getCertificateChain() {
        return this.certificateChain;
    }

    public void setCertificateChain(File certificateChain) {
        this.certificateChain = certificateChain;
    }

    public File getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(File privateKey) {
        this.privateKey = privateKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSSLKeyManager(String certChainFile, String keyFile) {
        if (certChainFile == null || keyFile == null) {
            return;
        }
        this.certificateChain = new File(certChainFile);
        this.privateKey = new File(keyFile);
    }
}