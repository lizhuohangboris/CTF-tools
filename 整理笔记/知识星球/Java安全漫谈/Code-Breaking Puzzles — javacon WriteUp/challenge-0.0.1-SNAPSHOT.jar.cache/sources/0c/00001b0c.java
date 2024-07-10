package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/server/AbstractServletWebServerFactory.class */
public abstract class AbstractServletWebServerFactory extends AbstractConfigurableWebServerFactory implements ConfigurableServletWebServerFactory {
    protected final Log logger;
    private String contextPath;
    private String displayName;
    private Session session;
    private boolean registerDefaultServlet;
    private MimeMappings mimeMappings;
    private List<ServletContextInitializer> initializers;
    private Jsp jsp;
    private Map<Locale, Charset> localeCharsetMappings;
    private Map<String, String> initParameters;
    private final DocumentRoot documentRoot;
    private final StaticResourceJars staticResourceJars;

    public AbstractServletWebServerFactory() {
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = true;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
    }

    public AbstractServletWebServerFactory(int port) {
        super(port);
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = true;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
    }

    public AbstractServletWebServerFactory(String contextPath, int port) {
        super(port);
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = true;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
        checkContextPath(contextPath);
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setContextPath(String contextPath) {
        checkContextPath(contextPath);
        this.contextPath = contextPath;
    }

    private void checkContextPath(String contextPath) {
        Assert.notNull(contextPath, "ContextPath must not be null");
        if (!contextPath.isEmpty()) {
            if ("/".equals(contextPath)) {
                throw new IllegalArgumentException("Root ContextPath must be specified using an empty string");
            }
            if (!contextPath.startsWith("/") || contextPath.endsWith("/")) {
                throw new IllegalArgumentException("ContextPath must start with '/' and not end with '/'");
            }
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isRegisterDefaultServlet() {
        return this.registerDefaultServlet;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setRegisterDefaultServlet(boolean registerDefaultServlet) {
        this.registerDefaultServlet = registerDefaultServlet;
    }

    public MimeMappings getMimeMappings() {
        return this.mimeMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setMimeMappings(MimeMappings mimeMappings) {
        this.mimeMappings = new MimeMappings(mimeMappings);
    }

    public File getDocumentRoot() {
        return this.documentRoot.getDirectory();
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setDocumentRoot(File documentRoot) {
        this.documentRoot.setDirectory(documentRoot);
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setInitializers(List<? extends ServletContextInitializer> initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers = new ArrayList(initializers);
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void addInitializers(ServletContextInitializer... initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers.addAll(Arrays.asList(initializers));
    }

    public Jsp getJsp() {
        return this.jsp;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setJsp(Jsp jsp) {
        this.jsp = jsp;
    }

    public Session getSession() {
        return this.session;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setSession(Session session) {
        this.session = session;
    }

    public Map<Locale, Charset> getLocaleCharsetMappings() {
        return this.localeCharsetMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setLocaleCharsetMappings(Map<Locale, Charset> localeCharsetMappings) {
        Assert.notNull(localeCharsetMappings, "localeCharsetMappings must not be null");
        this.localeCharsetMappings = localeCharsetMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ServletContextInitializer[] mergeInitializers(ServletContextInitializer... initializers) {
        List<ServletContextInitializer> mergedInitializers = new ArrayList<>();
        mergedInitializers.add(servletContext -> {
            Map<String, String> map = this.initParameters;
            servletContext.getClass();
            map.forEach(this::setInitParameter);
        });
        mergedInitializers.add(new SessionConfiguringInitializer(this.session));
        mergedInitializers.addAll(Arrays.asList(initializers));
        mergedInitializers.addAll(this.initializers);
        return (ServletContextInitializer[]) mergedInitializers.toArray(new ServletContextInitializer[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldRegisterJspServlet() {
        return this.jsp != null && this.jsp.getRegistered() && ClassUtils.isPresent(this.jsp.getClassName(), getClass().getClassLoader());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File getValidDocumentRoot() {
        return this.documentRoot.getValidDirectory();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final List<URL> getUrlsOfJarsWithMetaInfResources() {
        return this.staticResourceJars.getUrls();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final File getValidSessionStoreDir() {
        return getValidSessionStoreDir(true);
    }

    protected final File getValidSessionStoreDir(boolean mkdirs) {
        return this.session.getSessionStoreDirectory().getValidDirectory(mkdirs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/server/AbstractServletWebServerFactory$SessionConfiguringInitializer.class */
    public static class SessionConfiguringInitializer implements ServletContextInitializer {
        private final Session session;

        SessionConfiguringInitializer(Session session) {
            this.session = session;
        }

        @Override // org.springframework.boot.web.servlet.ServletContextInitializer
        public void onStartup(ServletContext servletContext) throws ServletException {
            if (this.session.getTrackingModes() != null) {
                servletContext.setSessionTrackingModes(unwrap(this.session.getTrackingModes()));
            }
            configureSessionCookie(servletContext.getSessionCookieConfig());
        }

        private void configureSessionCookie(SessionCookieConfig config) {
            Session.Cookie cookie = this.session.getCookie();
            if (cookie.getName() != null) {
                config.setName(cookie.getName());
            }
            if (cookie.getDomain() != null) {
                config.setDomain(cookie.getDomain());
            }
            if (cookie.getPath() != null) {
                config.setPath(cookie.getPath());
            }
            if (cookie.getComment() != null) {
                config.setComment(cookie.getComment());
            }
            if (cookie.getHttpOnly() != null) {
                config.setHttpOnly(cookie.getHttpOnly().booleanValue());
            }
            if (cookie.getSecure() != null) {
                config.setSecure(cookie.getSecure().booleanValue());
            }
            if (cookie.getMaxAge() != null) {
                config.setMaxAge((int) cookie.getMaxAge().getSeconds());
            }
        }

        private Set<SessionTrackingMode> unwrap(Set<Session.SessionTrackingMode> modes) {
            if (modes == null) {
                return null;
            }
            Set<SessionTrackingMode> result = new LinkedHashSet<>();
            for (Session.SessionTrackingMode mode : modes) {
                result.add(SessionTrackingMode.valueOf(mode.name()));
            }
            return result;
        }
    }
}