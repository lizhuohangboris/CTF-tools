package org.springframework.boot.web.server;

import org.apache.tomcat.util.net.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/Ssl.class */
public class Ssl {
    private ClientAuth clientAuth;
    private String[] ciphers;
    private String[] enabledProtocols;
    private String keyAlias;
    private String keyPassword;
    private String keyStore;
    private String keyStorePassword;
    private String keyStoreType;
    private String keyStoreProvider;
    private String trustStore;
    private String trustStorePassword;
    private String trustStoreType;
    private String trustStoreProvider;
    private boolean enabled = true;
    private String protocol = Constants.SSL_PROTO_TLS;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/Ssl$ClientAuth.class */
    public enum ClientAuth {
        WANT,
        NEED
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ClientAuth getClientAuth() {
        return this.clientAuth;
    }

    public void setClientAuth(ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    public String[] getCiphers() {
        return this.ciphers;
    }

    public void setCiphers(String[] ciphers) {
        this.ciphers = ciphers;
    }

    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public void setEnabledProtocols(String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String getKeyAlias() {
        return this.keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return this.keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyStore() {
        return this.keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStoreProvider() {
        return this.keyStoreProvider;
    }

    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    public String getTrustStore() {
        return this.trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStoreType() {
        return this.trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTrustStoreProvider() {
        return this.trustStoreProvider;
    }

    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}