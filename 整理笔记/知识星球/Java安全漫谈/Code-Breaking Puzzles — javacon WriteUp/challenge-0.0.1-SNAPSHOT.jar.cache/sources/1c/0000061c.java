package javax.security.auth.message.callback;

import java.security.KeyStore;
import javax.security.auth.callback.Callback;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/callback/TrustStoreCallback.class */
public class TrustStoreCallback implements Callback {
    private KeyStore trustStore;

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public KeyStore getTrustStore() {
        return this.trustStore;
    }
}