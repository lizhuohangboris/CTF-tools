package org.springframework.boot.web.embedded.netty;

import ch.qos.logback.core.net.ssl.SSL;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.util.ResourceUtils;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.SslProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/netty/SslServerCustomizer.class */
public class SslServerCustomizer implements NettyServerCustomizer {
    private final Ssl ssl;
    private final Http2 http2;
    private final SslStoreProvider sslStoreProvider;

    public SslServerCustomizer(Ssl ssl, Http2 http2, SslStoreProvider sslStoreProvider) {
        this.ssl = ssl;
        this.http2 = http2;
        this.sslStoreProvider = sslStoreProvider;
    }

    @Override // java.util.function.Function
    public HttpServer apply(HttpServer server) {
        try {
            return server.secure(contextSpec -> {
                SslProvider.DefaultConfigurationSpec spec = contextSpec.sslContext(getContextBuilder());
                if (this.http2 != null && this.http2.isEnabled()) {
                    spec.defaultConfiguration(SslProvider.DefaultConfigurationType.H2);
                }
            });
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected SslContextBuilder getContextBuilder() {
        SslContextBuilder builder = SslContextBuilder.forServer(getKeyManagerFactory(this.ssl, this.sslStoreProvider)).trustManager(getTrustManagerFactory(this.ssl, this.sslStoreProvider));
        if (this.ssl.getEnabledProtocols() != null) {
            builder.protocols(this.ssl.getEnabledProtocols());
        }
        if (this.ssl.getCiphers() != null) {
            builder.ciphers(Arrays.asList(this.ssl.getCiphers()));
        }
        if (this.ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            builder.clientAuth(ClientAuth.REQUIRE);
        } else if (this.ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            builder.clientAuth(ClientAuth.OPTIONAL);
        }
        return builder;
    }

    protected KeyManagerFactory getKeyManagerFactory(Ssl ssl, SslStoreProvider sslStoreProvider) {
        try {
            KeyStore keyStore = getKeyStore(ssl, sslStoreProvider);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPassword = ssl.getKeyPassword() != null ? ssl.getKeyPassword().toCharArray() : null;
            if (keyPassword == null && ssl.getKeyStorePassword() != null) {
                keyPassword = ssl.getKeyStorePassword().toCharArray();
            }
            keyManagerFactory.init(keyStore, keyPassword);
            return keyManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private KeyStore getKeyStore(Ssl ssl, SslStoreProvider sslStoreProvider) throws Exception {
        if (sslStoreProvider != null) {
            return sslStoreProvider.getKeyStore();
        }
        return loadKeyStore(ssl.getKeyStoreType(), ssl.getKeyStoreProvider(), ssl.getKeyStore(), ssl.getKeyStorePassword());
    }

    protected TrustManagerFactory getTrustManagerFactory(Ssl ssl, SslStoreProvider sslStoreProvider) {
        try {
            KeyStore store = getTrustStore(ssl, sslStoreProvider);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory;
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
}