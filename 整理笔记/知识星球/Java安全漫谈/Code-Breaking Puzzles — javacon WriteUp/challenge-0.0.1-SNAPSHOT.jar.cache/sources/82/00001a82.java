package org.springframework.boot.web.embedded.jetty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/SslServerCustomizer.class */
public class SslServerCustomizer implements JettyServerCustomizer {
    private final InetSocketAddress address;
    private final Ssl ssl;
    private final SslStoreProvider sslStoreProvider;
    private final Http2 http2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslServerCustomizer(InetSocketAddress address, Ssl ssl, SslStoreProvider sslStoreProvider, Http2 http2) {
        this.address = address;
        this.ssl = ssl;
        this.sslStoreProvider = sslStoreProvider;
        this.http2 = http2;
    }

    @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
    public void customize(Server server) {
        SslContextFactory sslContextFactory = new SslContextFactory();
        configureSsl(sslContextFactory, this.ssl, this.sslStoreProvider);
        server.setConnectors(new Connector[]{createConnector(server, sslContextFactory, this.address)});
    }

    private ServerConnector createConnector(Server server, SslContextFactory sslContextFactory, InetSocketAddress address) {
        HttpConfiguration config = new HttpConfiguration();
        config.setSendServerVersion(false);
        config.setSecureScheme("https");
        config.setSecurePort(address.getPort());
        config.addCustomizer(new SecureRequestCustomizer());
        ServerConnector connector = createServerConnector(server, sslContextFactory, config);
        connector.setPort(address.getPort());
        connector.setHost(address.getHostString());
        return connector;
    }

    private ServerConnector createServerConnector(Server server, SslContextFactory sslContextFactory, HttpConfiguration config) {
        if (this.http2 == null || !this.http2.isEnabled()) {
            return createHttp11ServerConnector(server, config, sslContextFactory);
        }
        Assert.state(isAlpnPresent(), () -> {
            return "The 'org.eclipse.jetty:jetty-alpn-server' dependency is required for HTTP/2 support.";
        });
        Assert.state(isConscryptPresent(), () -> {
            return "The 'org.eclipse.jetty.http2:http2-server' and Conscrypt dependencies are required for HTTP/2 support.";
        });
        return createHttp2ServerConnector(server, config, sslContextFactory);
    }

    private ServerConnector createHttp11ServerConnector(Server server, HttpConfiguration config, SslContextFactory sslContextFactory) {
        return new ServerConnector(server, new ConnectionFactory[]{new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(config)});
    }

    private boolean isAlpnPresent() {
        return ClassUtils.isPresent("org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory", null);
    }

    private boolean isConscryptPresent() {
        return ClassUtils.isPresent("org.conscrypt.Conscrypt", null);
    }

    private ServerConnector createHttp2ServerConnector(Server server, HttpConfiguration config, SslContextFactory sslContextFactory) {
        ConnectionFactory hTTP2ServerConnectionFactory = new HTTP2ServerConnectionFactory(config);
        ConnectionFactory aLPNServerConnectionFactory = new ALPNServerConnectionFactory(new String[0]);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setProvider("Conscrypt");
        return new ServerConnector(server, new ConnectionFactory[]{new SslConnectionFactory(sslContextFactory, aLPNServerConnectionFactory.getProtocol()), aLPNServerConnectionFactory, hTTP2ServerConnectionFactory, new HttpConnectionFactory(config)});
    }

    protected void configureSsl(SslContextFactory factory, Ssl ssl, SslStoreProvider sslStoreProvider) {
        factory.setProtocol(ssl.getProtocol());
        configureSslClientAuth(factory, ssl);
        configureSslPasswords(factory, ssl);
        factory.setCertAlias(ssl.getKeyAlias());
        if (!ObjectUtils.isEmpty((Object[]) ssl.getCiphers())) {
            factory.setIncludeCipherSuites(ssl.getCiphers());
            factory.setExcludeCipherSuites(new String[0]);
        }
        if (ssl.getEnabledProtocols() != null) {
            factory.setIncludeProtocols(ssl.getEnabledProtocols());
        }
        if (sslStoreProvider != null) {
            try {
                factory.setKeyStore(sslStoreProvider.getKeyStore());
                factory.setTrustStore(sslStoreProvider.getTrustStore());
                return;
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set SSL store", ex);
            }
        }
        configureSslKeyStore(factory, ssl);
        configureSslTrustStore(factory, ssl);
    }

    private void configureSslClientAuth(SslContextFactory factory, Ssl ssl) {
        if (ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            factory.setNeedClientAuth(true);
            factory.setWantClientAuth(true);
        } else if (ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            factory.setWantClientAuth(true);
        }
    }

    private void configureSslPasswords(SslContextFactory factory, Ssl ssl) {
        if (ssl.getKeyStorePassword() != null) {
            factory.setKeyStorePassword(ssl.getKeyStorePassword());
        }
        if (ssl.getKeyPassword() != null) {
            factory.setKeyManagerPassword(ssl.getKeyPassword());
        }
    }

    private void configureSslKeyStore(SslContextFactory factory, Ssl ssl) {
        try {
            URL url = ResourceUtils.getURL(ssl.getKeyStore());
            factory.setKeyStoreResource(Resource.newResource(url));
            if (ssl.getKeyStoreType() != null) {
                factory.setKeyStoreType(ssl.getKeyStoreType());
            }
            if (ssl.getKeyStoreProvider() != null) {
                factory.setKeyStoreProvider(ssl.getKeyStoreProvider());
            }
        } catch (IOException ex) {
            throw new WebServerException("Could not find key store '" + ssl.getKeyStore() + "'", ex);
        }
    }

    private void configureSslTrustStore(SslContextFactory factory, Ssl ssl) {
        if (ssl.getTrustStorePassword() != null) {
            factory.setTrustStorePassword(ssl.getTrustStorePassword());
        }
        if (ssl.getTrustStore() != null) {
            try {
                URL url = ResourceUtils.getURL(ssl.getTrustStore());
                factory.setTrustStoreResource(Resource.newResource(url));
            } catch (IOException ex) {
                throw new WebServerException("Could not find trust store '" + ssl.getTrustStore() + "'", ex);
            }
        }
        if (ssl.getTrustStoreType() != null) {
            factory.setTrustStoreType(ssl.getTrustStoreType());
        }
        if (ssl.getTrustStoreProvider() != null) {
            factory.setTrustStoreProvider(ssl.getTrustStoreProvider());
        }
    }
}