package org.springframework.boot.web.embedded.tomcat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.TomcatHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatReactiveWebServerFactory.class */
public class TomcatReactiveWebServerFactory extends AbstractReactiveWebServerFactory implements ConfigurableTomcatWebServerFactory {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
    private File baseDirectory;
    private List<Valve> engineValves;
    private List<LifecycleListener> contextLifecycleListeners;
    private List<TomcatContextCustomizer> tomcatContextCustomizers;
    private List<TomcatConnectorCustomizer> tomcatConnectorCustomizers;
    private String protocol;
    private Charset uriEncoding;
    private int backgroundProcessorDelay;

    public TomcatReactiveWebServerFactory() {
        this.engineValves = new ArrayList();
        this.contextLifecycleListeners = new ArrayList(Collections.singleton(new AprLifecycleListener()));
        this.tomcatContextCustomizers = new ArrayList();
        this.tomcatConnectorCustomizers = new ArrayList();
        this.protocol = "org.apache.coyote.http11.Http11NioProtocol";
        this.uriEncoding = DEFAULT_CHARSET;
    }

    public TomcatReactiveWebServerFactory(int port) {
        super(port);
        this.engineValves = new ArrayList();
        this.contextLifecycleListeners = new ArrayList(Collections.singleton(new AprLifecycleListener()));
        this.tomcatContextCustomizers = new ArrayList();
        this.tomcatConnectorCustomizers = new ArrayList();
        this.protocol = "org.apache.coyote.http11.Http11NioProtocol";
        this.uriEncoding = DEFAULT_CHARSET;
    }

    @Override // org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
    public WebServer getWebServer(HttpHandler httpHandler) {
        Tomcat tomcat = new Tomcat();
        File baseDir = this.baseDirectory != null ? this.baseDirectory : createTempDir("tomcat");
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        Connector connector = new Connector(this.protocol);
        tomcat.getService().addConnector(connector);
        customizeConnector(connector);
        tomcat.setConnector(connector);
        tomcat.getHost().setAutoDeploy(false);
        configureEngine(tomcat.getEngine());
        TomcatHttpHandlerAdapter servlet = new TomcatHttpHandlerAdapter(httpHandler);
        prepareContext(tomcat.getHost(), servlet);
        return new TomcatWebServer(tomcat, getPort() >= 0);
    }

    private void configureEngine(Engine engine) {
        engine.setBackgroundProcessorDelay(this.backgroundProcessorDelay);
        for (Valve valve : this.engineValves) {
            engine.getPipeline().addValve(valve);
        }
    }

    protected void prepareContext(Host host, TomcatHttpHandlerAdapter servlet) {
        File docBase = createTempDir("tomcat-docbase");
        TomcatEmbeddedContext context = new TomcatEmbeddedContext();
        context.setPath("");
        context.setDocBase(docBase.getAbsolutePath());
        context.addLifecycleListener(new Tomcat.FixContextListener());
        context.setParentClassLoader(ClassUtils.getDefaultClassLoader());
        skipAllTldScanning(context);
        WebappLoader loader = new WebappLoader(context.getParentClassLoader());
        loader.setLoaderClass(TomcatEmbeddedWebappClassLoader.class.getName());
        loader.setDelegate(true);
        context.setLoader(loader);
        Tomcat.addServlet(context, "httpHandlerServlet", servlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "httpHandlerServlet");
        host.addChild(context);
        configureContext(context);
    }

    private void skipAllTldScanning(TomcatEmbeddedContext context) {
        StandardJarScanFilter filter = new StandardJarScanFilter();
        filter.setTldSkip("*.jar");
        context.getJarScanner().setJarScanFilter(filter);
    }

    protected void configureContext(Context context) {
        List<LifecycleListener> list = this.contextLifecycleListeners;
        context.getClass();
        list.forEach(this::addLifecycleListener);
        this.tomcatContextCustomizers.forEach(customizer -> {
            customizer.customize(context);
        });
    }

    protected void customizeConnector(Connector connector) {
        int port = getPort() >= 0 ? getPort() : 0;
        connector.setPort(port);
        if (StringUtils.hasText(getServerHeader())) {
            connector.setAttribute("server", getServerHeader());
        }
        if (connector.getProtocolHandler() instanceof AbstractProtocol) {
            customizeProtocol((AbstractProtocol) connector.getProtocolHandler());
        }
        if (getUriEncoding() != null) {
            connector.setURIEncoding(getUriEncoding().name());
        }
        connector.setProperty("bindOnInit", "false");
        if (getSsl() != null && getSsl().isEnabled()) {
            customizeSsl(connector);
        }
        TomcatConnectorCustomizer compression = new CompressionConnectorCustomizer(getCompression());
        compression.customize(connector);
        for (TomcatConnectorCustomizer customizer : this.tomcatConnectorCustomizers) {
            customizer.customize(connector);
        }
    }

    private void customizeProtocol(AbstractProtocol<?> protocol) {
        if (getAddress() != null) {
            protocol.setAddress(getAddress());
        }
    }

    private void customizeSsl(Connector connector) {
        new SslConnectorCustomizer(getSsl(), getSslStoreProvider()).customize(connector);
        if (getHttp2() != null && getHttp2().isEnabled()) {
            connector.addUpgradeProtocol(new Http2Protocol());
        }
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setBackgroundProcessorDelay(int delay) {
        this.backgroundProcessorDelay = delay;
    }

    public void setTomcatContextCustomizers(Collection<? extends TomcatContextCustomizer> tomcatContextCustomizers) {
        Assert.notNull(tomcatContextCustomizers, "TomcatContextCustomizers must not be null");
        this.tomcatContextCustomizers = new ArrayList(tomcatContextCustomizers);
    }

    public Collection<TomcatContextCustomizer> getTomcatContextCustomizers() {
        return this.tomcatContextCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addContextCustomizers(TomcatContextCustomizer... tomcatContextCustomizers) {
        Assert.notNull(tomcatContextCustomizers, "TomcatContextCustomizers must not be null");
        this.tomcatContextCustomizers.addAll(Arrays.asList(tomcatContextCustomizers));
    }

    public void setTomcatConnectorCustomizers(Collection<? extends TomcatConnectorCustomizer> tomcatConnectorCustomizers) {
        Assert.notNull(tomcatConnectorCustomizers, "TomcatConnectorCustomizers must not be null");
        this.tomcatConnectorCustomizers = new ArrayList(tomcatConnectorCustomizers);
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addConnectorCustomizers(TomcatConnectorCustomizer... tomcatConnectorCustomizers) {
        Assert.notNull(tomcatConnectorCustomizers, "TomcatConnectorCustomizers must not be null");
        this.tomcatConnectorCustomizers.addAll(Arrays.asList(tomcatConnectorCustomizers));
    }

    public Collection<TomcatConnectorCustomizer> getTomcatConnectorCustomizers() {
        return this.tomcatConnectorCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void addEngineValves(Valve... engineValves) {
        Assert.notNull(engineValves, "Valves must not be null");
        this.engineValves.addAll(Arrays.asList(engineValves));
    }

    public List<Valve> getEngineValves() {
        return this.engineValves;
    }

    @Override // org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory
    public void setUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    public Charset getUriEncoding() {
        return this.uriEncoding;
    }

    public void setContextLifecycleListeners(Collection<? extends LifecycleListener> contextLifecycleListeners) {
        Assert.notNull(contextLifecycleListeners, "ContextLifecycleListeners must not be null");
        this.contextLifecycleListeners = new ArrayList(contextLifecycleListeners);
    }

    public Collection<LifecycleListener> getContextLifecycleListeners() {
        return this.contextLifecycleListeners;
    }

    public void addContextLifecycleListeners(LifecycleListener... contextLifecycleListeners) {
        Assert.notNull(contextLifecycleListeners, "ContextLifecycleListeners must not be null");
        this.contextLifecycleListeners.addAll(Arrays.asList(contextLifecycleListeners));
    }

    protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
        return new TomcatWebServer(tomcat, getPort() >= 0);
    }

    public void setProtocol(String protocol) {
        Assert.hasLength(protocol, "Protocol must not be empty");
        this.protocol = protocol;
    }
}