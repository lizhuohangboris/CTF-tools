package org.springframework.boot.web.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/AbstractConfigurableWebServerFactory.class */
public abstract class AbstractConfigurableWebServerFactory implements ConfigurableWebServerFactory {
    private int port;
    private InetAddress address;
    private Set<ErrorPage> errorPages;
    private Ssl ssl;
    private SslStoreProvider sslStoreProvider;
    private Http2 http2;
    private Compression compression;
    private String serverHeader;

    public AbstractConfigurableWebServerFactory() {
        this.port = 8080;
        this.errorPages = new LinkedHashSet();
    }

    public AbstractConfigurableWebServerFactory(int port) {
        this.port = 8080;
        this.errorPages = new LinkedHashSet();
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public Set<ErrorPage> getErrorPages() {
        return this.errorPages;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setErrorPages(Set<? extends ErrorPage> errorPages) {
        Assert.notNull(errorPages, "ErrorPages must not be null");
        this.errorPages = new LinkedHashSet(errorPages);
    }

    @Override // org.springframework.boot.web.server.ErrorPageRegistry
    public void addErrorPages(ErrorPage... errorPages) {
        Assert.notNull(errorPages, "ErrorPages must not be null");
        this.errorPages.addAll(Arrays.asList(errorPages));
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public SslStoreProvider getSslStoreProvider() {
        return this.sslStoreProvider;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setSslStoreProvider(SslStoreProvider sslStoreProvider) {
        this.sslStoreProvider = sslStoreProvider;
    }

    public Http2 getHttp2() {
        return this.http2;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setHttp2(Http2 http2) {
        this.http2 = http2;
    }

    public Compression getCompression() {
        return this.compression;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    public String getServerHeader() {
        return this.serverHeader;
    }

    @Override // org.springframework.boot.web.server.ConfigurableWebServerFactory
    public void setServerHeader(String serverHeader) {
        this.serverHeader = serverHeader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + getPort());
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException ex) {
            throw new WebServerException("Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"), ex);
        }
    }
}