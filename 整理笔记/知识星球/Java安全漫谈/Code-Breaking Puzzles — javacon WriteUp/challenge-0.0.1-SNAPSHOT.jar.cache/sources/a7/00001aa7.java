package org.springframework.boot.web.embedded.undertow;

import ch.qos.logback.core.net.ssl.SSL;
import io.undertow.Undertow;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import org.apache.tomcat.jni.Address;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.util.ResourceUtils;
import org.xnio.Options;
import org.xnio.Sequence;
import org.xnio.SslClientAuthMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/SslBuilderCustomizer.class */
class SslBuilderCustomizer implements UndertowBuilderCustomizer {
    private final int port;
    private final InetAddress address;
    private final Ssl ssl;
    private final SslStoreProvider sslStoreProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslBuilderCustomizer(int port, InetAddress address, Ssl ssl, SslStoreProvider sslStoreProvider) {
        this.port = port;
        this.address = address;
        this.ssl = ssl;
        this.sslStoreProvider = sslStoreProvider;
    }

    @Override // org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
    public void customize(Undertow.Builder builder) {
        try {
            SSLContext sslContext = SSLContext.getInstance(this.ssl.getProtocol());
            sslContext.init(getKeyManagers(this.ssl, this.sslStoreProvider), getTrustManagers(this.ssl, this.sslStoreProvider), null);
            builder.addHttpsListener(this.port, getListenAddress(), sslContext);
            builder.setSocketOption(Options.SSL_CLIENT_AUTH_MODE, getSslClientAuthMode(this.ssl));
            if (this.ssl.getEnabledProtocols() != null) {
                builder.setSocketOption(Options.SSL_ENABLED_PROTOCOLS, Sequence.of(this.ssl.getEnabledProtocols()));
            }
            if (this.ssl.getCiphers() != null) {
                builder.setSocketOption(Options.SSL_ENABLED_CIPHER_SUITES, Sequence.of(this.ssl.getCiphers()));
            }
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String getListenAddress() {
        if (this.address == null) {
            return Address.APR_ANYADDR;
        }
        return this.address.getHostAddress();
    }

    private SslClientAuthMode getSslClientAuthMode(Ssl ssl) {
        if (ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            return SslClientAuthMode.REQUIRED;
        }
        if (ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            return SslClientAuthMode.REQUESTED;
        }
        return SslClientAuthMode.NOT_REQUESTED;
    }

    private KeyManager[] getKeyManagers(Ssl ssl, SslStoreProvider sslStoreProvider) {
        try {
            KeyStore keyStore = getKeyStore(ssl, sslStoreProvider);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPassword = ssl.getKeyPassword() != null ? ssl.getKeyPassword().toCharArray() : null;
            if (keyPassword == null && ssl.getKeyStorePassword() != null) {
                keyPassword = ssl.getKeyStorePassword().toCharArray();
            }
            keyManagerFactory.init(keyStore, keyPassword);
            if (ssl.getKeyAlias() != null) {
                return getConfigurableAliasKeyManagers(ssl, keyManagerFactory.getKeyManagers());
            }
            return keyManagerFactory.getKeyManagers();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private KeyManager[] getConfigurableAliasKeyManagers(Ssl ssl, KeyManager[] keyManagers) {
        for (int i = 0; i < keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509ExtendedKeyManager) {
                keyManagers[i] = new ConfigurableAliasKeyManager((X509ExtendedKeyManager) keyManagers[i], ssl.getKeyAlias());
            }
        }
        return keyManagers;
    }

    private KeyStore getKeyStore(Ssl ssl, SslStoreProvider sslStoreProvider) throws Exception {
        if (sslStoreProvider != null) {
            return sslStoreProvider.getKeyStore();
        }
        return loadKeyStore(ssl.getKeyStoreType(), ssl.getKeyStoreProvider(), ssl.getKeyStore(), ssl.getKeyStorePassword());
    }

    private TrustManager[] getTrustManagers(Ssl ssl, SslStoreProvider sslStoreProvider) {
        try {
            KeyStore store = getTrustStore(ssl, sslStoreProvider);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory.getTrustManagers();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private KeyStore getTrustStore(Ssl ssl, SslStoreProvider sslStoreProvider) throws Exception {
        if (sslStoreProvider != null) {
            return sslStoreProvider.getTrustStore();
        }
        return loadKeyStore(ssl.getTrustStoreType(), ssl.getTrustStoreProvider(), ssl.getTrustStore(), ssl.getTrustStorePassword());
    }

    private KeyStore loadKeyStore(String type, String provider, String resource, String password) throws Exception {
        String type2 = type != null ? type : SSL.DEFAULT_KEYSTORE_TYPE;
        if (resource == null) {
            return null;
        }
        KeyStore store = provider != null ? KeyStore.getInstance(type2, provider) : KeyStore.getInstance(type2);
        URL url = ResourceUtils.getURL(resource);
        store.load(url.openStream(), password != null ? password.toCharArray() : null);
        return store;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/SslBuilderCustomizer$ConfigurableAliasKeyManager.class */
    public static class ConfigurableAliasKeyManager extends X509ExtendedKeyManager {
        private final X509ExtendedKeyManager keyManager;
        private final String alias;

        ConfigurableAliasKeyManager(X509ExtendedKeyManager keyManager, String alias) {
            this.keyManager = keyManager;
            this.alias = alias;
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineClientAlias(String[] strings, Principal[] principals, SSLEngine sslEngine) {
            return this.keyManager.chooseEngineClientAlias(strings, principals, sslEngine);
        }

        @Override // javax.net.ssl.X509ExtendedKeyManager
        public String chooseEngineServerAlias(String s, Principal[] principals, SSLEngine sslEngine) {
            if (this.alias == null) {
                return this.keyManager.chooseEngineServerAlias(s, principals, sslEngine);
            }
            return this.alias;
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return this.keyManager.chooseClientAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return this.keyManager.chooseServerAlias(keyType, issuers, socket);
        }

        @Override // javax.net.ssl.X509KeyManager
        public X509Certificate[] getCertificateChain(String alias) {
            return this.keyManager.getCertificateChain(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.keyManager.getClientAliases(keyType, issuers);
        }

        @Override // javax.net.ssl.X509KeyManager
        public PrivateKey getPrivateKey(String alias) {
            return this.keyManager.getPrivateKey(alias);
        }

        @Override // javax.net.ssl.X509KeyManager
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.keyManager.getServerAliases(keyType, issuers);
        }
    }
}