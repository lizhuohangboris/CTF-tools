package org.springframework.boot.web.embedded.jetty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory.class */
public class JettyServletWebServerFactory extends AbstractServletWebServerFactory implements ConfigurableJettyWebServerFactory, ResourceLoaderAware {
    private List<Configuration> configurations;
    private boolean useForwardHeaders;
    private int acceptors;
    private int selectors;
    private List<JettyServerCustomizer> jettyServerCustomizers;
    private ResourceLoader resourceLoader;
    private ThreadPool threadPool;

    public JettyServletWebServerFactory() {
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new ArrayList();
    }

    public JettyServletWebServerFactory(int port) {
        super(port);
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new ArrayList();
    }

    public JettyServletWebServerFactory(String contextPath, int port) {
        super(contextPath, port);
        this.configurations = new ArrayList();
        this.acceptors = -1;
        this.selectors = -1;
        this.jettyServerCustomizers = new ArrayList();
    }

    @Override // org.springframework.boot.web.servlet.server.ServletWebServerFactory
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        JettyEmbeddedWebAppContext context = new JettyEmbeddedWebAppContext();
        int port = getPort() >= 0 ? getPort() : 0;
        InetSocketAddress address = new InetSocketAddress(getAddress(), port);
        Server server = createServer(address);
        configureWebAppContext(context, initializers);
        server.setHandler(addHandlerWrappers(context));
        this.logger.info("Server initialized with port: " + port);
        if (getSsl() != null && getSsl().isEnabled()) {
            customizeSsl(server, address);
        }
        for (JettyServerCustomizer customizer : getServerCustomizers()) {
            customizer.customize(server);
        }
        if (this.useForwardHeaders) {
            new ForwardHeadersCustomizer().customize(server);
        }
        return getJettyWebServer(server);
    }

    private Server createServer(InetSocketAddress address) {
        Server server = new Server(getThreadPool());
        server.setConnectors(new Connector[]{createConnector(address, server)});
        return server;
    }

    private AbstractConnector createConnector(InetSocketAddress address, Server server) {
        ServerConnector connector = new ServerConnector(server, this.acceptors, this.selectors);
        connector.setHost(address.getHostString());
        connector.setPort(address.getPort());
        for (HttpConfiguration.ConnectionFactory connectionFactory : connector.getConnectionFactories()) {
            if (connectionFactory instanceof HttpConfiguration.ConnectionFactory) {
                connectionFactory.getHttpConfiguration().setSendServerVersion(false);
            }
        }
        return connector;
    }

    private Handler addHandlerWrappers(Handler handler) {
        if (getCompression() != null && getCompression().getEnabled()) {
            handler = applyWrapper(handler, JettyHandlerWrappers.createGzipHandlerWrapper(getCompression()));
        }
        if (StringUtils.hasText(getServerHeader())) {
            handler = applyWrapper(handler, JettyHandlerWrappers.createServerHeaderHandlerWrapper(getServerHeader()));
        }
        return handler;
    }

    private Handler applyWrapper(Handler handler, HandlerWrapper wrapper) {
        wrapper.setHandler(handler);
        return wrapper;
    }

    private void customizeSsl(Server server, InetSocketAddress address) {
        new SslServerCustomizer(address, getSsl(), getSslStoreProvider(), getHttp2()).customize(server);
    }

    protected final void configureWebAppContext(WebAppContext context, ServletContextInitializer... initializers) {
        Assert.notNull(context, "Context must not be null");
        context.setTempDirectory(getTempDirectory());
        if (this.resourceLoader != null) {
            context.setClassLoader(this.resourceLoader.getClassLoader());
        }
        String contextPath = getContextPath();
        context.setContextPath(StringUtils.hasLength(contextPath) ? contextPath : "/");
        context.setDisplayName(getDisplayName());
        configureDocumentRoot(context);
        if (isRegisterDefaultServlet()) {
            addDefaultServlet(context);
        }
        if (shouldRegisterJspServlet()) {
            addJspServlet(context);
            context.addBean(new JasperInitializer(context), true);
        }
        addLocaleMappings(context);
        ServletContextInitializer[] initializersToUse = mergeInitializers(initializers);
        Configuration[] configurations = getWebAppContextConfigurations(context, initializersToUse);
        context.setConfigurations(configurations);
        context.setThrowUnavailableOnStartupException(true);
        configureSession(context);
        postProcessWebAppContext(context);
    }

    private void configureSession(WebAppContext context) {
        SessionHandler handler = context.getSessionHandler();
        Duration sessionTimeout = getSession().getTimeout();
        handler.setMaxInactiveInterval(isNegative(sessionTimeout) ? -1 : (int) sessionTimeout.getSeconds());
        if (getSession().isPersistent()) {
            DefaultSessionCache cache = new DefaultSessionCache(handler);
            FileSessionDataStore store = new FileSessionDataStore();
            store.setStoreDir(getValidSessionStoreDir());
            cache.setSessionDataStore(store);
            handler.setSessionCache(cache);
        }
    }

    private boolean isNegative(Duration sessionTimeout) {
        return sessionTimeout == null || sessionTimeout.isNegative();
    }

    private void addLocaleMappings(WebAppContext context) {
        getLocaleCharsetMappings().forEach(locale, charset -> {
            context.addLocaleEncoding(locale.toString(), charset.toString());
        });
    }

    private File getTempDirectory() {
        String temp = System.getProperty("java.io.tmpdir");
        if (temp != null) {
            return new File(temp);
        }
        return null;
    }

    private void configureDocumentRoot(WebAppContext handler) {
        Resource newJarResource;
        File root = getValidDocumentRoot();
        File docBase = root != null ? root : createTempDir("jetty-docbase");
        try {
            List<Resource> resources = new ArrayList<>();
            if (docBase.isDirectory()) {
                newJarResource = Resource.newResource(docBase.getCanonicalFile());
            } else {
                newJarResource = JarResource.newJarResource(Resource.newResource(docBase));
            }
            Resource rootResource = newJarResource;
            resources.add(root != null ? new LoaderHidingResource(rootResource) : rootResource);
            for (URL resourceJarUrl : getUrlsOfJarsWithMetaInfResources()) {
                Resource resource = createResource(resourceJarUrl);
                if (resource.exists() && resource.isDirectory()) {
                    resources.add(resource);
                }
            }
            handler.setBaseResource(new ResourceCollection((Resource[]) resources.toArray(new Resource[0])));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Resource createResource(URL url) throws Exception {
        if ("file".equals(url.getProtocol())) {
            File file = new File(url.toURI());
            if (file.isFile()) {
                return Resource.newResource(ResourceUtils.JAR_URL_PREFIX + url + "!/META-INF/resources");
            }
        }
        return Resource.newResource(url + "META-INF/resources");
    }

    protected final void addDefaultServlet(WebAppContext context) {
        Assert.notNull(context, "Context must not be null");
        ServletHolder holder = new ServletHolder();
        holder.setName("default");
        holder.setClassName("org.eclipse.jetty.servlet.DefaultServlet");
        holder.setInitParameter("dirAllowed", "false");
        holder.setInitOrder(1);
        context.getServletHandler().addServletWithMapping(holder, "/");
        context.getServletHandler().getServletMapping("/").setDefault(true);
    }

    protected final void addJspServlet(WebAppContext context) {
        Assert.notNull(context, "Context must not be null");
        ServletHolder holder = new ServletHolder();
        holder.setName("jsp");
        holder.setClassName(getJsp().getClassName());
        holder.setInitParameter("fork", "false");
        holder.setInitParameters(getJsp().getInitParameters());
        holder.setInitOrder(3);
        context.getServletHandler().addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setServletName("jsp");
        mapping.setPathSpecs(new String[]{"*.jsp", "*.jspx"});
        context.getServletHandler().addServletMapping(mapping);
    }

    protected Configuration[] getWebAppContextConfigurations(WebAppContext webAppContext, ServletContextInitializer... initializers) {
        List<Configuration> configurations = new ArrayList<>();
        configurations.add(getServletContextInitializerConfiguration(webAppContext, initializers));
        configurations.addAll(getConfigurations());
        configurations.add(getErrorPageConfiguration());
        configurations.add(getMimeTypeConfiguration());
        return (Configuration[]) configurations.toArray(new Configuration[0]);
    }

    private Configuration getErrorPageConfiguration() {
        return new AbstractConfiguration() { // from class: org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory.1
            {
                JettyServletWebServerFactory.this = this;
            }

            public void configure(WebAppContext context) throws Exception {
                ErrorHandler errorHandler = context.getErrorHandler();
                context.setErrorHandler(new JettyEmbeddedErrorHandler(errorHandler));
                JettyServletWebServerFactory.this.addJettyErrorPages(errorHandler, JettyServletWebServerFactory.this.getErrorPages());
            }
        };
    }

    private Configuration getMimeTypeConfiguration() {
        return new AbstractConfiguration() { // from class: org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory.2
            {
                JettyServletWebServerFactory.this = this;
            }

            public void configure(WebAppContext context) throws Exception {
                MimeTypes mimeTypes = context.getMimeTypes();
                Iterator<MimeMappings.Mapping> it = JettyServletWebServerFactory.this.getMimeMappings().iterator();
                while (it.hasNext()) {
                    MimeMappings.Mapping mapping = it.next();
                    mimeTypes.addMimeMapping(mapping.getExtension(), mapping.getMimeType());
                }
            }
        };
    }

    protected Configuration getServletContextInitializerConfiguration(WebAppContext webAppContext, ServletContextInitializer... initializers) {
        return new ServletContextInitializerConfiguration(initializers);
    }

    protected void postProcessWebAppContext(WebAppContext webAppContext) {
    }

    protected JettyWebServer getJettyWebServer(Server server) {
        return new JettyWebServer(server, getPort() >= 0);
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void setSelectors(int selectors) {
        this.selectors = selectors;
    }

    public void setServerCustomizers(Collection<? extends JettyServerCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.jettyServerCustomizers = new ArrayList(customizers);
    }

    public Collection<JettyServerCustomizer> getServerCustomizers() {
        return this.jettyServerCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory
    public void addServerCustomizers(JettyServerCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.jettyServerCustomizers.addAll(Arrays.asList(customizers));
    }

    public void setConfigurations(Collection<? extends Configuration> configurations) {
        Assert.notNull(configurations, "Configurations must not be null");
        this.configurations = new ArrayList(configurations);
    }

    public Collection<Configuration> getConfigurations() {
        return this.configurations;
    }

    public void addConfigurations(Configuration... configurations) {
        Assert.notNull(configurations, "Configurations must not be null");
        this.configurations.addAll(Arrays.asList(configurations));
    }

    public ThreadPool getThreadPool() {
        return this.threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public void addJettyErrorPages(ErrorHandler errorHandler, Collection<ErrorPage> errorPages) {
        if (errorHandler instanceof ErrorPageErrorHandler) {
            ErrorPageErrorHandler handler = (ErrorPageErrorHandler) errorHandler;
            for (ErrorPage errorPage : errorPages) {
                if (errorPage.isGlobal()) {
                    handler.addErrorPage("org.eclipse.jetty.server.error_page.global", errorPage.getPath());
                } else if (errorPage.getExceptionName() != null) {
                    handler.addErrorPage(errorPage.getExceptionName(), errorPage.getPath());
                } else {
                    handler.addErrorPage(errorPage.getStatusCode(), errorPage.getPath());
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyServletWebServerFactory$LoaderHidingResource.class */
    public static final class LoaderHidingResource extends Resource {
        private final Resource delegate;

        private LoaderHidingResource(Resource delegate) {
            this.delegate = delegate;
        }

        public Resource addPath(String path) throws IOException, MalformedURLException {
            if (path.startsWith("/org/springframework/boot")) {
                return null;
            }
            return this.delegate.addPath(path);
        }

        public boolean isContainedIn(Resource resource) throws MalformedURLException {
            return this.delegate.isContainedIn(resource);
        }

        public void close() {
            this.delegate.close();
        }

        public boolean exists() {
            return this.delegate.exists();
        }

        public boolean isDirectory() {
            return this.delegate.isDirectory();
        }

        public long lastModified() {
            return this.delegate.lastModified();
        }

        public long length() {
            return this.delegate.length();
        }

        @Deprecated
        public URL getURL() {
            return this.delegate.getURL();
        }

        public File getFile() throws IOException {
            return this.delegate.getFile();
        }

        public String getName() {
            return this.delegate.getName();
        }

        public InputStream getInputStream() throws IOException {
            return this.delegate.getInputStream();
        }

        public ReadableByteChannel getReadableByteChannel() throws IOException {
            return this.delegate.getReadableByteChannel();
        }

        public boolean delete() throws SecurityException {
            return this.delegate.delete();
        }

        public boolean renameTo(Resource dest) throws SecurityException {
            return this.delegate.renameTo(dest);
        }

        public String[] list() {
            return this.delegate.list();
        }
    }
}