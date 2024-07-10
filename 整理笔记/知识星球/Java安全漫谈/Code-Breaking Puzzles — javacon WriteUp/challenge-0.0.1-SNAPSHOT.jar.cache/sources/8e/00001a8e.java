package org.springframework.boot.web.embedded.tomcat;

import java.io.FileNotFoundException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/SslConnectorCustomizer.class */
public class SslConnectorCustomizer implements TomcatConnectorCustomizer {
    private final Ssl ssl;
    private final SslStoreProvider sslStoreProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SslConnectorCustomizer(Ssl ssl, SslStoreProvider sslStoreProvider) {
        Assert.notNull(ssl, "Ssl configuration should not be null");
        this.ssl = ssl;
        this.sslStoreProvider = sslStoreProvider;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
    public void customize(Connector connector) {
        ProtocolHandler handler = connector.getProtocolHandler();
        Assert.state(handler instanceof AbstractHttp11JsseProtocol, "To use SSL, the connector's protocol handler must be an AbstractHttp11JsseProtocol subclass");
        configureSsl((AbstractHttp11JsseProtocol) handler, this.ssl, this.sslStoreProvider);
        connector.setScheme("https");
        connector.setSecure(true);
    }

    protected void configureSsl(AbstractHttp11JsseProtocol<?> protocol, Ssl ssl, SslStoreProvider sslStoreProvider) {
        SSLHostConfig[] findSslHostConfigs;
        protocol.setSSLEnabled(true);
        protocol.setSslProtocol(ssl.getProtocol());
        configureSslClientAuth(protocol, ssl);
        protocol.setKeystorePass(ssl.getKeyStorePassword());
        protocol.setKeyPass(ssl.getKeyPassword());
        protocol.setKeyAlias(ssl.getKeyAlias());
        String ciphers = StringUtils.arrayToCommaDelimitedString(ssl.getCiphers());
        if (StringUtils.hasText(ciphers)) {
            protocol.setCiphers(ciphers);
        }
        if (ssl.getEnabledProtocols() != null) {
            for (SSLHostConfig sslHostConfig : protocol.findSslHostConfigs()) {
                sslHostConfig.setProtocols(StringUtils.arrayToCommaDelimitedString(ssl.getEnabledProtocols()));
            }
        }
        if (sslStoreProvider != null) {
            configureSslStoreProvider(protocol, sslStoreProvider);
            return;
        }
        configureSslKeyStore(protocol, ssl);
        configureSslTrustStore(protocol, ssl);
    }

    private void configureSslClientAuth(AbstractHttp11JsseProtocol<?> protocol, Ssl ssl) {
        if (ssl.getClientAuth() == Ssl.ClientAuth.NEED) {
            protocol.setClientAuth(Boolean.TRUE.toString());
        } else if (ssl.getClientAuth() == Ssl.ClientAuth.WANT) {
            protocol.setClientAuth("want");
        }
    }

    protected void configureSslStoreProvider(AbstractHttp11JsseProtocol<?> protocol, SslStoreProvider sslStoreProvider) {
        Assert.isInstanceOf(Http11NioProtocol.class, protocol, "SslStoreProvider can only be used with Http11NioProtocol");
        TomcatURLStreamHandlerFactory instance = TomcatURLStreamHandlerFactory.getInstance();
        instance.addUserFactory(new SslStoreProviderUrlStreamHandlerFactory(sslStoreProvider));
        try {
            if (sslStoreProvider.getKeyStore() != null) {
                protocol.setKeystorePass("");
                protocol.setKeystoreFile("springbootssl:keyStore");
            }
            if (sslStoreProvider.getTrustStore() != null) {
                protocol.setTruststorePass("");
                protocol.setTruststoreFile("springbootssl:trustStore");
            }
        } catch (Exception ex) {
            throw new WebServerException("Could not load store: " + ex.getMessage(), ex);
        }
    }

    private void configureSslKeyStore(AbstractHttp11JsseProtocol<?> protocol, Ssl ssl) {
        try {
            protocol.setKeystoreFile(ResourceUtils.getURL(ssl.getKeyStore()).toString());
            if (ssl.getKeyStoreType() != null) {
                protocol.setKeystoreType(ssl.getKeyStoreType());
            }
            if (ssl.getKeyStoreProvider() != null) {
                protocol.setKeystoreProvider(ssl.getKeyStoreProvider());
            }
        } catch (FileNotFoundException ex) {
            throw new WebServerException("Could not load key store: " + ex.getMessage(), ex);
        }
    }

    private void configureSslTrustStore(AbstractHttp11JsseProtocol<?> protocol, Ssl ssl) {
        if (ssl.getTrustStore() != null) {
            try {
                protocol.setTruststoreFile(ResourceUtils.getURL(ssl.getTrustStore()).toString());
            } catch (FileNotFoundException ex) {
                throw new WebServerException("Could not load trust store: " + ex.getMessage(), ex);
            }
        }
        protocol.setTruststorePass(ssl.getTrustStorePassword());
        if (ssl.getTrustStoreType() != null) {
            protocol.setTruststoreType(ssl.getTrustStoreType());
        }
        if (ssl.getTrustStoreProvider() != null) {
            protocol.setTruststoreProvider(ssl.getTrustStoreProvider());
        }
    }
}