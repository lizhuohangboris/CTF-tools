package org.springframework.boot.web.embedded.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.MimeMapping;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import org.apache.catalina.valves.Constants;
import org.apache.tomcat.jni.Address;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.AbstractServletWebServerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServerFactory.class */
public class UndertowServletWebServerFactory extends AbstractServletWebServerFactory implements ConfigurableUndertowWebServerFactory, ResourceLoaderAware {
    private static final Set<Class<?>> NO_CLASSES = Collections.emptySet();
    private List<UndertowBuilderCustomizer> builderCustomizers;
    private List<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers;
    private ResourceLoader resourceLoader;
    private Integer bufferSize;
    private Integer ioThreads;
    private Integer workerThreads;
    private Boolean directBuffers;
    private File accessLogDirectory;
    private String accessLogPattern;
    private String accessLogPrefix;
    private String accessLogSuffix;
    private boolean accessLogEnabled;
    private boolean accessLogRotate;
    private boolean useForwardHeaders;
    private boolean eagerInitFilters;

    public UndertowServletWebServerFactory() {
        this.builderCustomizers = new ArrayList();
        this.deploymentInfoCustomizers = new ArrayList();
        this.accessLogEnabled = false;
        this.accessLogRotate = true;
        this.eagerInitFilters = true;
        getJsp().setRegistered(false);
    }

    public UndertowServletWebServerFactory(int port) {
        super(port);
        this.builderCustomizers = new ArrayList();
        this.deploymentInfoCustomizers = new ArrayList();
        this.accessLogEnabled = false;
        this.accessLogRotate = true;
        this.eagerInitFilters = true;
        getJsp().setRegistered(false);
    }

    public UndertowServletWebServerFactory(String contextPath, int port) {
        super(contextPath, port);
        this.builderCustomizers = new ArrayList();
        this.deploymentInfoCustomizers = new ArrayList();
        this.accessLogEnabled = false;
        this.accessLogRotate = true;
        this.eagerInitFilters = true;
        getJsp().setRegistered(false);
    }

    public void setBuilderCustomizers(Collection<? extends UndertowBuilderCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.builderCustomizers = new ArrayList(customizers);
    }

    public Collection<UndertowBuilderCustomizer> getBuilderCustomizers() {
        return this.builderCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void addBuilderCustomizers(UndertowBuilderCustomizer... customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.builderCustomizers.addAll(Arrays.asList(customizers));
    }

    public void setDeploymentInfoCustomizers(Collection<? extends UndertowDeploymentInfoCustomizer> customizers) {
        Assert.notNull(customizers, "Customizers must not be null");
        this.deploymentInfoCustomizers = new ArrayList(customizers);
    }

    public Collection<UndertowDeploymentInfoCustomizer> getDeploymentInfoCustomizers() {
        return this.deploymentInfoCustomizers;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer... customizers) {
        Assert.notNull(customizers, "UndertowDeploymentInfoCustomizers must not be null");
        this.deploymentInfoCustomizers.addAll(Arrays.asList(customizers));
    }

    @Override // org.springframework.boot.web.servlet.server.ServletWebServerFactory
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        DeploymentManager manager = createDeploymentManager(initializers);
        int port = getPort();
        Undertow.Builder builder = createBuilder(port);
        return getUndertowWebServer(builder, manager, port);
    }

    private Undertow.Builder createBuilder(int port) {
        Undertow.Builder builder = Undertow.builder();
        if (this.bufferSize != null) {
            builder.setBufferSize(this.bufferSize.intValue());
        }
        if (this.ioThreads != null) {
            builder.setIoThreads(this.ioThreads.intValue());
        }
        if (this.workerThreads != null) {
            builder.setWorkerThreads(this.workerThreads.intValue());
        }
        if (this.directBuffers != null) {
            builder.setDirectBuffers(this.directBuffers.booleanValue());
        }
        if (getSsl() != null && getSsl().isEnabled()) {
            customizeSsl(builder);
        } else {
            builder.addHttpListener(port, getListenAddress());
        }
        for (UndertowBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
        return builder;
    }

    private void customizeSsl(Undertow.Builder builder) {
        new SslBuilderCustomizer(getPort(), getAddress(), getSsl(), getSslStoreProvider()).customize(builder);
        if (getHttp2() != null) {
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, Boolean.valueOf(getHttp2().isEnabled()));
        }
    }

    private String getListenAddress() {
        if (getAddress() == null) {
            return Address.APR_ANYADDR;
        }
        return getAddress().getHostAddress();
    }

    private DeploymentManager createDeploymentManager(ServletContextInitializer... initializers) {
        DeploymentInfo deployment = Servlets.deployment();
        registerServletContainerInitializerToDriveServletContextInitializers(deployment, initializers);
        deployment.setClassLoader(getServletClassLoader());
        deployment.setContextPath(getContextPath());
        deployment.setDisplayName(getDisplayName());
        deployment.setDeploymentName("spring-boot");
        if (isRegisterDefaultServlet()) {
            deployment.addServlet(Servlets.servlet("default", DefaultServlet.class));
        }
        configureErrorPages(deployment);
        deployment.setServletStackTraces(ServletStackTraces.NONE);
        deployment.setResourceManager(getDocumentRootResourceManager());
        deployment.setEagerFilterInit(this.eagerInitFilters);
        configureMimeMappings(deployment);
        for (UndertowDeploymentInfoCustomizer customizer : this.deploymentInfoCustomizers) {
            customizer.customize(deployment);
        }
        if (isAccessLogEnabled()) {
            configureAccessLog(deployment);
        }
        if (getSession().isPersistent()) {
            File dir = getValidSessionStoreDir();
            deployment.setSessionPersistenceManager(new FileSessionPersistence(dir));
        }
        addLocaleMappings(deployment);
        DeploymentManager manager = Servlets.newContainer().addDeployment(deployment);
        manager.deploy();
        SessionManager sessionManager = manager.getDeployment().getSessionManager();
        Duration timeoutDuration = getSession().getTimeout();
        int sessionTimeout = isZeroOrLess(timeoutDuration) ? -1 : (int) timeoutDuration.getSeconds();
        sessionManager.setDefaultSessionTimeout(sessionTimeout);
        return manager;
    }

    private boolean isZeroOrLess(Duration timeoutDuration) {
        return timeoutDuration == null || timeoutDuration.isZero() || timeoutDuration.isNegative();
    }

    private void configureAccessLog(DeploymentInfo deploymentInfo) {
        try {
            createAccessLogDirectoryIfNecessary();
            XnioWorker worker = createWorker();
            String prefix = this.accessLogPrefix != null ? this.accessLogPrefix : "access_log.";
            DefaultAccessLogReceiver accessLogReceiver = new DefaultAccessLogReceiver(worker, this.accessLogDirectory, prefix, this.accessLogSuffix, this.accessLogRotate);
            EventListener listener = new AccessLogShutdownListener(worker, accessLogReceiver);
            deploymentInfo.addListener(new ListenerInfo(AccessLogShutdownListener.class, new ImmediateInstanceFactory(listener)));
            deploymentInfo.addInitialHandlerChainWrapper(handler -> {
                return createAccessLogHandler(handler, accessLogReceiver);
            });
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to create AccessLogHandler", ex);
        }
    }

    private AccessLogHandler createAccessLogHandler(HttpHandler handler, AccessLogReceiver accessLogReceiver) {
        createAccessLogDirectoryIfNecessary();
        String formatString = this.accessLogPattern != null ? this.accessLogPattern : Constants.AccessLog.COMMON_ALIAS;
        return new AccessLogHandler(handler, accessLogReceiver, formatString, Undertow.class.getClassLoader());
    }

    private void createAccessLogDirectoryIfNecessary() {
        Assert.state(this.accessLogDirectory != null, "Access log directory is not set");
        if (!this.accessLogDirectory.isDirectory() && !this.accessLogDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create access log directory '" + this.accessLogDirectory + "'");
        }
    }

    private XnioWorker createWorker() throws IOException {
        Xnio xnio = Xnio.getInstance(Undertow.class.getClassLoader());
        return xnio.createWorker(OptionMap.builder().set(Options.THREAD_DAEMON, true).getMap());
    }

    private void addLocaleMappings(DeploymentInfo deployment) {
        getLocaleCharsetMappings().forEach(locale, charset -> {
            deployment.addLocaleCharsetMapping(locale.toString(), charset.toString());
        });
    }

    private void registerServletContainerInitializerToDriveServletContextInitializers(DeploymentInfo deployment, ServletContextInitializer... initializers) {
        ServletContextInitializer[] mergedInitializers = mergeInitializers(initializers);
        Initializer initializer = new Initializer(mergedInitializers);
        deployment.addServletContainerInitializer(new ServletContainerInitializerInfo(Initializer.class, new ImmediateInstanceFactory(initializer), NO_CLASSES));
    }

    private ClassLoader getServletClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return getClass().getClassLoader();
    }

    private ResourceManager getDocumentRootResourceManager() {
        File root = getValidDocumentRoot();
        File docBase = getCanonicalDocumentRoot(root);
        List<URL> metaInfResourceUrls = getUrlsOfJarsWithMetaInfResources();
        List<URL> resourceJarUrls = new ArrayList<>();
        List<ResourceManager> managers = new ArrayList<>();
        FileResourceManager fileResourceManager = docBase.isDirectory() ? new FileResourceManager(docBase, 0L) : new JarResourceManager(docBase);
        if (root != null) {
            fileResourceManager = new LoaderHidingResourceManager(fileResourceManager);
        }
        managers.add(fileResourceManager);
        for (URL url : metaInfResourceUrls) {
            if ("file".equals(url.getProtocol())) {
                try {
                    File file = new File(url.toURI());
                    if (file.isFile()) {
                        resourceJarUrls.add(new URL(ResourceUtils.JAR_URL_PREFIX + url + ResourceUtils.JAR_URL_SEPARATOR));
                    } else {
                        managers.add(new FileResourceManager(new File(file, "META-INF/resources"), 0L));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                resourceJarUrls.add(url);
            }
        }
        managers.add(new MetaInfResourcesResourceManager(resourceJarUrls));
        return new CompositeResourceManager((ResourceManager[]) managers.toArray(new ResourceManager[0]));
    }

    private File getCanonicalDocumentRoot(File docBase) {
        File createTempDir;
        if (docBase != null) {
            createTempDir = docBase;
        } else {
            try {
                createTempDir = createTempDir("undertow-docbase");
            } catch (IOException ex) {
                throw new IllegalStateException("Cannot get canonical document root", ex);
            }
        }
        File root = createTempDir;
        return root.getCanonicalFile();
    }

    private void configureErrorPages(DeploymentInfo servletBuilder) {
        for (ErrorPage errorPage : getErrorPages()) {
            servletBuilder.addErrorPage(getUndertowErrorPage(errorPage));
        }
    }

    private io.undertow.servlet.api.ErrorPage getUndertowErrorPage(ErrorPage errorPage) {
        if (errorPage.getStatus() != null) {
            return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(), errorPage.getStatusCode());
        }
        if (errorPage.getException() != null) {
            return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(), errorPage.getException());
        }
        return new io.undertow.servlet.api.ErrorPage(errorPage.getPath());
    }

    private void configureMimeMappings(DeploymentInfo servletBuilder) {
        Iterator<MimeMappings.Mapping> it = getMimeMappings().iterator();
        while (it.hasNext()) {
            MimeMappings.Mapping mimeMapping = it.next();
            servletBuilder.addMimeMapping(new MimeMapping(mimeMapping.getExtension(), mimeMapping.getMimeType()));
        }
    }

    protected UndertowServletWebServer getUndertowWebServer(Undertow.Builder builder, DeploymentManager manager, int port) {
        return new UndertowServletWebServer(builder, manager, getContextPath(), isUseForwardHeaders(), port >= 0, getCompression(), getServerHeader());
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setIoThreads(Integer ioThreads) {
        this.ioThreads = ioThreads;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setWorkerThreads(Integer workerThreads) {
        this.workerThreads = workerThreads;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setUseDirectBuffers(Boolean directBuffers) {
        this.directBuffers = directBuffers;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogDirectory(File accessLogDirectory) {
        this.accessLogDirectory = accessLogDirectory;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogPattern(String accessLogPattern) {
        this.accessLogPattern = accessLogPattern;
    }

    public String getAccessLogPrefix() {
        return this.accessLogPrefix;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogPrefix(String accessLogPrefix) {
        this.accessLogPrefix = accessLogPrefix;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogSuffix(String accessLogSuffix) {
        this.accessLogSuffix = accessLogSuffix;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogEnabled(boolean accessLogEnabled) {
        this.accessLogEnabled = accessLogEnabled;
    }

    public boolean isAccessLogEnabled() {
        return this.accessLogEnabled;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setAccessLogRotate(boolean accessLogRotate) {
        this.accessLogRotate = accessLogRotate;
    }

    protected final boolean isUseForwardHeaders() {
        return this.useForwardHeaders;
    }

    @Override // org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory
    public void setUseForwardHeaders(boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    public boolean isEagerInitFilters() {
        return this.eagerInitFilters;
    }

    public void setEagerInitFilters(boolean eagerInitFilters) {
        this.eagerInitFilters = eagerInitFilters;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServerFactory$MetaInfResourcesResourceManager.class */
    public static final class MetaInfResourcesResourceManager implements ResourceManager {
        private final List<URL> metaInfResourceJarUrls;

        private MetaInfResourcesResourceManager(List<URL> metaInfResourceJarUrls) {
            this.metaInfResourceJarUrls = metaInfResourceJarUrls;
        }

        public void close() throws IOException {
        }

        public Resource getResource(String path) {
            for (URL url : this.metaInfResourceJarUrls) {
                URLResource resource = getMetaInfResource(url, path);
                if (resource != null) {
                    return resource;
                }
            }
            return null;
        }

        public boolean isResourceChangeListenerSupported() {
            return false;
        }

        public void registerResourceChangeListener(ResourceChangeListener listener) {
        }

        public void removeResourceChangeListener(ResourceChangeListener listener) {
        }

        private URLResource getMetaInfResource(URL resourceJar, String path) {
            try {
                URL resourceUrl = new URL(resourceJar + "META-INF/resources" + path);
                URLResource resource = new URLResource(resourceUrl, path);
                if (resource.getContentLength().longValue() < 0) {
                    return null;
                }
                return resource;
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServerFactory$Initializer.class */
    public static class Initializer implements ServletContainerInitializer {
        private final ServletContextInitializer[] initializers;

        Initializer(ServletContextInitializer[] initializers) {
            this.initializers = initializers;
        }

        @Override // javax.servlet.ServletContainerInitializer
        public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
            ServletContextInitializer[] servletContextInitializerArr;
            for (ServletContextInitializer initializer : this.initializers) {
                initializer.onStartup(servletContext);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServerFactory$LoaderHidingResourceManager.class */
    public static final class LoaderHidingResourceManager implements ResourceManager {
        private final ResourceManager delegate;

        private LoaderHidingResourceManager(ResourceManager delegate) {
            this.delegate = delegate;
        }

        public Resource getResource(String path) throws IOException {
            if (path.startsWith("/org/springframework/boot")) {
                return null;
            }
            return this.delegate.getResource(path);
        }

        public boolean isResourceChangeListenerSupported() {
            return this.delegate.isResourceChangeListenerSupported();
        }

        public void registerResourceChangeListener(ResourceChangeListener listener) {
            this.delegate.registerResourceChangeListener(listener);
        }

        public void removeResourceChangeListener(ResourceChangeListener listener) {
            this.delegate.removeResourceChangeListener(listener);
        }

        public void close() throws IOException {
            this.delegate.close();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/undertow/UndertowServletWebServerFactory$AccessLogShutdownListener.class */
    public static class AccessLogShutdownListener implements ServletContextListener {
        private final XnioWorker worker;
        private final DefaultAccessLogReceiver accessLogReceiver;

        AccessLogShutdownListener(XnioWorker worker, DefaultAccessLogReceiver accessLogReceiver) {
            this.worker = worker;
            this.accessLogReceiver = accessLogReceiver;
        }

        @Override // javax.servlet.ServletContextListener
        public void contextInitialized(ServletContextEvent sce) {
        }

        @Override // javax.servlet.ServletContextListener
        public void contextDestroyed(ServletContextEvent sce) {
            try {
                this.accessLogReceiver.close();
                this.worker.shutdown();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}