package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.HandlesTypes;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.bcel.classfile.AnnotationElementValue;
import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.ArrayElementValue;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ClassParser;
import org.apache.tomcat.util.bcel.classfile.ElementValue;
import org.apache.tomcat.util.bcel.classfile.ElementValuePair;
import org.apache.tomcat.util.bcel.classfile.JavaClass;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.descriptor.InputSourceUtil;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.MultipartDef;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.descriptor.web.SecurityRoleRef;
import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.SessionConfig;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.JarFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ContextConfig.class */
public class ContextConfig implements LifecycleListener {
    private static final Log log = LogFactory.getLog(ContextConfig.class);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected static final LoginConfig DUMMY_LOGIN_CONFIG = new LoginConfig("NONE", null, null, null);
    protected static final Properties authenticators;
    protected static long deploymentCount;
    protected static final Map<Host, DefaultWebXmlCacheEntry> hostWebXmlCache;
    private static final Set<ServletContainerInitializer> EMPTY_SCI_SET;
    protected Map<String, Authenticator> customAuthenticators;
    protected volatile Context context = null;
    protected String defaultWebXml = null;
    protected boolean ok = false;
    protected String originalDocBase = null;
    private File antiLockingDocBase = null;
    protected final Map<ServletContainerInitializer, Set<Class<?>>> initializerClassMap = new LinkedHashMap();
    protected final Map<Class<?>, Set<ServletContainerInitializer>> typeInitializerMap = new HashMap();
    protected boolean handlesTypesAnnotations = false;
    protected boolean handlesTypesNonAnnotations = false;

    static {
        Properties props = new Properties();
        try {
            InputStream is = ContextConfig.class.getClassLoader().getResourceAsStream("org/apache/catalina/startup/Authenticators.properties");
            if (is != null) {
                props.load(is);
            }
            if (is != null) {
                if (0 != 0) {
                    is.close();
                } else {
                    is.close();
                }
            }
        } catch (IOException e) {
            props = null;
        }
        authenticators = props;
        deploymentCount = 0L;
        hostWebXmlCache = new ConcurrentHashMap();
        EMPTY_SCI_SET = Collections.emptySet();
    }

    public String getDefaultWebXml() {
        if (this.defaultWebXml == null) {
            this.defaultWebXml = Constants.DefaultWebXml;
        }
        return this.defaultWebXml;
    }

    public void setDefaultWebXml(String path) {
        this.defaultWebXml = path;
    }

    public void setCustomAuthenticators(Map<String, Authenticator> customAuthenticators) {
        this.customAuthenticators = customAuthenticators;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.context = (Context) event.getLifecycle();
            if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                configureStart();
            } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
                beforeStart();
            } else if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) {
                if (this.originalDocBase != null) {
                    this.context.setDocBase(this.originalDocBase);
                }
            } else if (event.getType().equals(Lifecycle.CONFIGURE_STOP_EVENT)) {
                configureStop();
            } else if (event.getType().equals(Lifecycle.AFTER_INIT_EVENT)) {
                init();
            } else if (event.getType().equals(Lifecycle.AFTER_DESTROY_EVENT)) {
                destroy();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("contextConfig.cce", event.getLifecycle()), e);
        }
    }

    protected void applicationAnnotationsConfig() {
        long t1 = System.currentTimeMillis();
        WebAnnotationSet.loadApplicationAnnotations(this.context);
        long t2 = System.currentTimeMillis();
        if (this.context instanceof StandardContext) {
            ((StandardContext) this.context).setStartupTime((t2 - t1) + ((StandardContext) this.context).getStartupTime());
        }
    }

    protected void authenticatorConfig() {
        Pipeline pipeline;
        LoginConfig loginConfig = this.context.getLoginConfig();
        if (loginConfig == null) {
            loginConfig = DUMMY_LOGIN_CONFIG;
            this.context.setLoginConfig(loginConfig);
        }
        if (this.context.getAuthenticator() != null) {
            return;
        }
        if (this.context.getRealm() == null) {
            log.error(sm.getString("contextConfig.missingRealm"));
            this.ok = false;
            return;
        }
        Valve authenticator = null;
        if (this.customAuthenticators != null) {
            authenticator = (Valve) this.customAuthenticators.get(loginConfig.getAuthMethod());
        }
        if (authenticator == null) {
            if (authenticators == null) {
                log.error(sm.getString("contextConfig.authenticatorResources"));
                this.ok = false;
                return;
            }
            String authenticatorName = authenticators.getProperty(loginConfig.getAuthMethod());
            if (authenticatorName == null) {
                log.error(sm.getString("contextConfig.authenticatorMissing", loginConfig.getAuthMethod()));
                this.ok = false;
                return;
            }
            try {
                Class<?> authenticatorClass = Class.forName(authenticatorName);
                authenticator = (Valve) authenticatorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("contextConfig.authenticatorInstantiate", authenticatorName), t);
                this.ok = false;
            }
        }
        if (authenticator != null && (pipeline = this.context.getPipeline()) != null) {
            pipeline.addValve(authenticator);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("contextConfig.authenticatorConfigured", loginConfig.getAuthMethod()));
            }
        }
    }

    protected Digester createContextDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        Map<Class<?>, List<String>> fakeAttributes = new HashMap<>();
        List<String> attrs = new ArrayList<>();
        attrs.add("className");
        fakeAttributes.put(Object.class, attrs);
        digester.setFakeAttributes(fakeAttributes);
        RuleSet contextRuleSet = new ContextRuleSet("", false);
        digester.addRuleSet(contextRuleSet);
        RuleSet namingRuleSet = new NamingRuleSet("Context/");
        digester.addRuleSet(namingRuleSet);
        return digester;
    }

    protected void contextConfig(Digester digester) {
        String defaultContextXml = null;
        if (this.context instanceof StandardContext) {
            defaultContextXml = ((StandardContext) this.context).getDefaultContextXml();
        }
        if (defaultContextXml == null) {
            defaultContextXml = Constants.DefaultContextXml;
        }
        if (!this.context.getOverride()) {
            File defaultContextFile = new File(defaultContextXml);
            if (!defaultContextFile.isAbsolute()) {
                defaultContextFile = new File(this.context.getCatalinaBase(), defaultContextXml);
            }
            if (defaultContextFile.exists()) {
                try {
                    URL defaultContextUrl = defaultContextFile.toURI().toURL();
                    processContextConfig(digester, defaultContextUrl);
                } catch (MalformedURLException e) {
                    log.error(sm.getString("contextConfig.badUrl", defaultContextFile), e);
                }
            }
            File hostContextFile = new File(getHostConfigBase(), Constants.HostContextXml);
            if (hostContextFile.exists()) {
                try {
                    URL hostContextUrl = hostContextFile.toURI().toURL();
                    processContextConfig(digester, hostContextUrl);
                } catch (MalformedURLException e2) {
                    log.error(sm.getString("contextConfig.badUrl", hostContextFile), e2);
                }
            }
        }
        if (this.context.getConfigFile() != null) {
            processContextConfig(digester, this.context.getConfigFile());
        }
    }

    protected void processContextConfig(Digester digester, URL contextXml) {
        if (log.isDebugEnabled()) {
            log.debug("Processing context [" + this.context.getName() + "] configuration file [" + contextXml + "]");
        }
        InputSource source = null;
        InputStream stream = null;
        try {
            source = new InputSource(contextXml.toString());
            URLConnection xmlConn = contextXml.openConnection();
            xmlConn.setUseCaches(false);
            stream = xmlConn.getInputStream();
        } catch (Exception e) {
            log.error(sm.getString("contextConfig.contextMissing", contextXml), e);
        }
        try {
            if (source == null) {
                return;
            }
            try {
                try {
                    source.setByteStream(stream);
                    digester.setClassLoader(getClass().getClassLoader());
                    digester.setUseContextClassLoader(false);
                    digester.push(this.context.getParent());
                    digester.push(this.context);
                    XmlErrorHandler errorHandler = new XmlErrorHandler();
                    digester.setErrorHandler(errorHandler);
                    digester.parse(source);
                    if (errorHandler.getWarnings().size() > 0 || errorHandler.getErrors().size() > 0) {
                        errorHandler.logFindings(log, contextXml.toString());
                        this.ok = false;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully processed context [" + this.context.getName() + "] configuration file [" + contextXml + "]");
                    }
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e2) {
                            log.error(sm.getString("contextConfig.contextClose"), e2);
                        }
                    }
                } catch (Exception e3) {
                    log.error(sm.getString("contextConfig.contextParse", this.context.getName()), e3);
                    this.ok = false;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e4) {
                            log.error(sm.getString("contextConfig.contextClose"), e4);
                        }
                    }
                }
            } catch (SAXParseException e5) {
                log.error(sm.getString("contextConfig.contextParse", this.context.getName()), e5);
                log.error(sm.getString("contextConfig.defaultPosition", "" + e5.getLineNumber(), "" + e5.getColumnNumber()));
                this.ok = false;
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e6) {
                        log.error(sm.getString("contextConfig.contextClose"), e6);
                    }
                }
            }
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e7) {
                    log.error(sm.getString("contextConfig.contextClose"), e7);
                    throw th;
                }
            }
            throw th;
        }
    }

    protected void fixDocBase() throws IOException {
        String docBase;
        String docBase2;
        Host host = (Host) this.context.getParent();
        File appBase = host.getAppBaseFile();
        String docBase3 = this.context.getDocBase();
        if (docBase3 == null) {
            String path = this.context.getPath();
            if (path == null) {
                return;
            }
            ContextName cn = new ContextName(path, this.context.getWebappVersion());
            docBase3 = cn.getBaseName();
        }
        File file = new File(docBase3);
        if (!file.isAbsolute()) {
            docBase = new File(appBase, docBase3).getPath();
        } else {
            docBase = file.getCanonicalPath();
        }
        File file2 = new File(docBase);
        String origDocBase = docBase;
        ContextName cn2 = new ContextName(this.context.getPath(), this.context.getWebappVersion());
        String pathName = cn2.getBaseName();
        boolean unpackWARs = true;
        if (host instanceof StandardHost) {
            unpackWARs = ((StandardHost) host).isUnpackWARs();
            if (unpackWARs && (this.context instanceof StandardContext)) {
                unpackWARs = ((StandardContext) this.context).getUnpackWAR();
            }
        }
        boolean docBaseInAppBase = docBase.startsWith(appBase.getPath() + File.separatorChar);
        if (docBase.toLowerCase(Locale.ENGLISH).endsWith(".war") && !file2.isDirectory()) {
            URL war = UriUtil.buildJarUrl(new File(docBase));
            if (unpackWARs) {
                docBase = new File(ExpandWar.expand(host, war, pathName)).getCanonicalPath();
                if (this.context instanceof StandardContext) {
                    ((StandardContext) this.context).setOriginalDocBase(origDocBase);
                }
            } else {
                ExpandWar.validate(host, war, pathName);
            }
        } else {
            File docDir = new File(docBase);
            File warFile = new File(docBase + ".war");
            URL war2 = null;
            if (warFile.exists() && docBaseInAppBase) {
                war2 = UriUtil.buildJarUrl(warFile);
            }
            if (docDir.exists()) {
                if (war2 != null && unpackWARs) {
                    ExpandWar.expand(host, war2, pathName);
                }
            } else {
                if (war2 != null) {
                    if (unpackWARs) {
                        docBase = new File(ExpandWar.expand(host, war2, pathName)).getCanonicalPath();
                    } else {
                        docBase = warFile.getCanonicalPath();
                        ExpandWar.validate(host, war2, pathName);
                    }
                }
                if (this.context instanceof StandardContext) {
                    ((StandardContext) this.context).setOriginalDocBase(origDocBase);
                }
            }
        }
        boolean docBaseInAppBase2 = docBase.startsWith(appBase.getPath() + File.separatorChar);
        if (docBaseInAppBase2) {
            docBase2 = docBase.substring(appBase.getPath().length()).replace(File.separatorChar, '/');
            if (docBase2.startsWith("/")) {
                docBase2 = docBase2.substring(1);
            }
        } else {
            docBase2 = docBase.replace(File.separatorChar, '/');
        }
        this.context.setDocBase(docBase2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void antiLocking() {
        if ((this.context instanceof StandardContext) && ((StandardContext) this.context).getAntiResourceLocking()) {
            Host host = (Host) this.context.getParent();
            String docBase = this.context.getDocBase();
            if (docBase == null) {
                return;
            }
            this.originalDocBase = docBase;
            File docBaseFile = new File(docBase);
            if (!docBaseFile.isAbsolute()) {
                docBaseFile = new File(host.getAppBaseFile(), docBase);
            }
            String path = this.context.getPath();
            if (path == null) {
                return;
            }
            ContextName cn = new ContextName(path, this.context.getWebappVersion());
            String docBase2 = cn.getBaseName();
            if (this.originalDocBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                System.getProperty("java.io.tmpdir");
                StringBuilder sb = new StringBuilder();
                long j = deploymentCount;
                deploymentCount = j + 1;
                this.antiLockingDocBase = new File((String) this, sb.append(j).append("-").append(docBase2).append(".war").toString());
            } else {
                String property = System.getProperty("java.io.tmpdir");
                new StringBuilder();
                long j2 = deploymentCount;
                deploymentCount = j2 + 1;
                this.antiLockingDocBase = new File(property, append(j2).append("-").append(docBase2).toString());
            }
            this.antiLockingDocBase = this.antiLockingDocBase.getAbsoluteFile();
            if (log.isDebugEnabled()) {
                log.debug("Anti locking context[" + this.context.getName() + "] setting docBase to " + this.antiLockingDocBase.getPath());
            }
            ExpandWar.delete(this.antiLockingDocBase);
            if (ExpandWar.copy(docBaseFile, this.antiLockingDocBase)) {
                this.context.setDocBase(this.antiLockingDocBase.getPath());
            }
        }
    }

    protected synchronized void init() {
        Digester contextDigester = createContextDigester();
        contextDigester.getParser();
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("contextConfig.init"));
        }
        this.context.setConfigured(false);
        this.ok = true;
        contextConfig(contextDigester);
    }

    protected synchronized void beforeStart() {
        try {
            fixDocBase();
        } catch (IOException e) {
            log.error(sm.getString("contextConfig.fixDocBase", this.context.getName()), e);
        }
        antiLocking();
    }

    protected synchronized void configureStart() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("contextConfig.start"));
        }
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("contextConfig.xmlSettings", this.context.getName(), Boolean.valueOf(this.context.getXmlValidation()), Boolean.valueOf(this.context.getXmlNamespaceAware())));
        }
        webConfig();
        if (!this.context.getIgnoreAnnotations()) {
            applicationAnnotationsConfig();
        }
        if (this.ok) {
            validateSecurityRoles();
        }
        if (this.ok) {
            authenticatorConfig();
        }
        if (log.isDebugEnabled()) {
            log.debug("Pipeline Configuration:");
            Pipeline pipeline = this.context.getPipeline();
            Valve[] valves = null;
            if (pipeline != null) {
                valves = pipeline.getValves();
            }
            if (valves != null) {
                for (int i = 0; i < valves.length; i++) {
                    log.debug("  " + valves[i].getClass().getName());
                }
            }
            log.debug("======================");
        }
        if (this.ok) {
            this.context.setConfigured(true);
            return;
        }
        log.error(sm.getString("contextConfig.unavailable"));
        this.context.setConfigured(false);
    }

    protected synchronized void configureStop() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("contextConfig.stop"));
        }
        Container[] children = this.context.findChildren();
        for (Container container : children) {
            this.context.removeChild(container);
        }
        SecurityConstraint[] securityConstraints = this.context.findConstraints();
        for (SecurityConstraint securityConstraint : securityConstraints) {
            this.context.removeConstraint(securityConstraint);
        }
        ErrorPage[] errorPages = this.context.findErrorPages();
        for (ErrorPage errorPage : errorPages) {
            this.context.removeErrorPage(errorPage);
        }
        FilterDef[] filterDefs = this.context.findFilterDefs();
        for (FilterDef filterDef : filterDefs) {
            this.context.removeFilterDef(filterDef);
        }
        FilterMap[] filterMaps = this.context.findFilterMaps();
        for (FilterMap filterMap : filterMaps) {
            this.context.removeFilterMap(filterMap);
        }
        String[] mimeMappings = this.context.findMimeMappings();
        for (String str : mimeMappings) {
            this.context.removeMimeMapping(str);
        }
        String[] parameters = this.context.findParameters();
        for (String str2 : parameters) {
            this.context.removeParameter(str2);
        }
        String[] securityRoles = this.context.findSecurityRoles();
        for (String str3 : securityRoles) {
            this.context.removeSecurityRole(str3);
        }
        String[] servletMappings = this.context.findServletMappings();
        for (String str4 : servletMappings) {
            this.context.removeServletMapping(str4);
        }
        String[] welcomeFiles = this.context.findWelcomeFiles();
        for (String str5 : welcomeFiles) {
            this.context.removeWelcomeFile(str5);
        }
        String[] wrapperLifecycles = this.context.findWrapperLifecycles();
        for (String str6 : wrapperLifecycles) {
            this.context.removeWrapperLifecycle(str6);
        }
        String[] wrapperListeners = this.context.findWrapperListeners();
        for (String str7 : wrapperListeners) {
            this.context.removeWrapperListener(str7);
        }
        if (this.antiLockingDocBase != null) {
            ExpandWar.delete(this.antiLockingDocBase, false);
        }
        this.initializerClassMap.clear();
        this.typeInitializerMap.clear();
        this.ok = true;
    }

    protected synchronized void destroy() {
        String workDir;
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("contextConfig.destroy"));
        }
        Server s = getServer();
        if ((s == null || s.getState().isAvailable()) && (this.context instanceof StandardContext) && (workDir = ((StandardContext) this.context).getWorkPath()) != null) {
            ExpandWar.delete(new File(workDir));
        }
    }

    private Server getServer() {
        Container c;
        Service s;
        Container container = this.context;
        while (true) {
            c = container;
            if (c == null || (c instanceof Engine)) {
                break;
            }
            container = c.getParent();
        }
        if (c == null || (s = ((Engine) c).getService()) == null) {
            return null;
        }
        return s.getServer();
    }

    protected void validateSecurityRoles() {
        SecurityConstraint[] constraints = this.context.findConstraints();
        for (SecurityConstraint securityConstraint : constraints) {
            String[] roles = securityConstraint.findAuthRoles();
            for (int j = 0; j < roles.length; j++) {
                if (!"*".equals(roles[j]) && !this.context.findSecurityRole(roles[j])) {
                    log.warn(sm.getString("contextConfig.role.auth", roles[j]));
                    this.context.addSecurityRole(roles[j]);
                }
            }
        }
        Container[] wrappers = this.context.findChildren();
        for (Container container : wrappers) {
            Wrapper wrapper = (Wrapper) container;
            String runAs = wrapper.getRunAs();
            if (runAs != null && !this.context.findSecurityRole(runAs)) {
                log.warn(sm.getString("contextConfig.role.runas", runAs));
                this.context.addSecurityRole(runAs);
            }
            String[] names = wrapper.findSecurityReferences();
            for (String str : names) {
                String link = wrapper.findSecurityReference(str);
                if (link != null && !this.context.findSecurityRole(link)) {
                    log.warn(sm.getString("contextConfig.role.link", link));
                    this.context.addSecurityRole(link);
                }
            }
        }
    }

    protected File getHostConfigBase() {
        File file = null;
        if (this.context.getParent() instanceof Host) {
            file = ((Host) this.context.getParent()).getConfigBaseFile();
        }
        return file;
    }

    protected void webConfig() {
        WebXmlParser webXmlParser = new WebXmlParser(this.context.getXmlNamespaceAware(), this.context.getXmlValidation(), this.context.getXmlBlockExternal());
        Set<WebXml> defaults = new HashSet<>();
        defaults.add(getDefaultWebXmlFragment(webXmlParser));
        Set<WebXml> tomcatWebXml = new HashSet<>();
        tomcatWebXml.add(getTomcatWebXmlFragment(webXmlParser));
        WebXml webXml = createWebXml();
        InputSource contextWebXml = getContextWebXmlSource();
        if (!webXmlParser.parseWebXml(contextWebXml, webXml, false)) {
            this.ok = false;
        }
        ServletContext sContext = this.context.getServletContext();
        Map<String, WebXml> fragments = processJarsForWebFragments(webXml, webXmlParser);
        Set<WebXml> orderedFragments = WebXml.orderWebFragments(webXml, fragments, sContext);
        if (this.ok) {
            processServletContainerInitializers();
        }
        if (!webXml.isMetadataComplete() || this.typeInitializerMap.size() > 0) {
            Map<String, JavaClassCacheEntry> javaClassCache = new HashMap<>();
            if (this.ok) {
                WebResource[] webResources = this.context.getResources().listResources(org.apache.tomcat.util.scan.Constants.WEB_INF_CLASSES);
                for (WebResource webResource : webResources) {
                    if (!"META-INF".equals(webResource.getName())) {
                        processAnnotationsWebResource(webResource, webXml, webXml.isMetadataComplete(), javaClassCache);
                    }
                }
            }
            if (this.ok) {
                processAnnotations(orderedFragments, webXml.isMetadataComplete(), javaClassCache);
            }
            javaClassCache.clear();
        }
        if (!webXml.isMetadataComplete()) {
            if (this.ok) {
                this.ok = webXml.merge(orderedFragments);
            }
            webXml.merge(tomcatWebXml);
            webXml.merge(defaults);
            if (this.ok) {
                convertJsps(webXml);
            }
            if (this.ok) {
                configureContext(webXml);
            }
        } else {
            webXml.merge(tomcatWebXml);
            webXml.merge(defaults);
            convertJsps(webXml);
            configureContext(webXml);
        }
        if (this.context.getLogEffectiveWebXml()) {
            log.info("web.xml:\n" + webXml.toXml());
        }
        if (this.ok) {
            Set<WebXml> resourceJars = new LinkedHashSet<>();
            for (WebXml fragment : orderedFragments) {
                resourceJars.add(fragment);
            }
            for (WebXml fragment2 : fragments.values()) {
                if (!resourceJars.contains(fragment2)) {
                    resourceJars.add(fragment2);
                }
            }
            processResourceJARs(resourceJars);
        }
        if (this.ok) {
            for (Map.Entry<ServletContainerInitializer, Set<Class<?>>> entry : this.initializerClassMap.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    this.context.addServletContainerInitializer(entry.getKey(), null);
                } else {
                    this.context.addServletContainerInitializer(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void configureContext(WebXml webxml) {
        this.context.setPublicId(webxml.getPublicId());
        this.context.setEffectiveMajorVersion(webxml.getMajorVersion());
        this.context.setEffectiveMinorVersion(webxml.getMinorVersion());
        for (Map.Entry<String, String> entry : webxml.getContextParams().entrySet()) {
            this.context.addParameter(entry.getKey(), entry.getValue());
        }
        this.context.setDenyUncoveredHttpMethods(webxml.getDenyUncoveredHttpMethods());
        this.context.setDisplayName(webxml.getDisplayName());
        this.context.setDistributable(webxml.isDistributable());
        for (ContextLocalEjb ejbLocalRef : webxml.getEjbLocalRefs().values()) {
            this.context.getNamingResources().addLocalEjb(ejbLocalRef);
        }
        for (ContextEjb ejbRef : webxml.getEjbRefs().values()) {
            this.context.getNamingResources().addEjb(ejbRef);
        }
        for (ContextEnvironment environment : webxml.getEnvEntries().values()) {
            this.context.getNamingResources().addEnvironment(environment);
        }
        for (ErrorPage errorPage : webxml.getErrorPages().values()) {
            this.context.addErrorPage(errorPage);
        }
        for (FilterDef filter : webxml.getFilters().values()) {
            if (filter.getAsyncSupported() == null) {
                filter.setAsyncSupported("false");
            }
            this.context.addFilterDef(filter);
        }
        for (FilterMap filterMap : webxml.getFilterMappings()) {
            this.context.addFilterMap(filterMap);
        }
        this.context.setJspConfigDescriptor(webxml.getJspConfigDescriptor());
        for (String listener : webxml.getListeners()) {
            this.context.addApplicationListener(listener);
        }
        for (Map.Entry<String, String> entry2 : webxml.getLocaleEncodingMappings().entrySet()) {
            this.context.addLocaleEncodingMappingParameter(entry2.getKey(), entry2.getValue());
        }
        if (webxml.getLoginConfig() != null) {
            this.context.setLoginConfig(webxml.getLoginConfig());
        }
        for (MessageDestinationRef mdr : webxml.getMessageDestinationRefs().values()) {
            this.context.getNamingResources().addMessageDestinationRef(mdr);
        }
        this.context.setIgnoreAnnotations(webxml.isMetadataComplete());
        for (Map.Entry<String, String> entry3 : webxml.getMimeMappings().entrySet()) {
            this.context.addMimeMapping(entry3.getKey(), entry3.getValue());
        }
        this.context.setRequestCharacterEncoding(webxml.getRequestCharacterEncoding());
        for (ContextResourceEnvRef resource : webxml.getResourceEnvRefs().values()) {
            this.context.getNamingResources().addResourceEnvRef(resource);
        }
        for (ContextResource resource2 : webxml.getResourceRefs().values()) {
            this.context.getNamingResources().addResource(resource2);
        }
        this.context.setResponseCharacterEncoding(webxml.getResponseCharacterEncoding());
        boolean allAuthenticatedUsersIsAppRole = webxml.getSecurityRoles().contains(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS);
        for (SecurityConstraint constraint : webxml.getSecurityConstraints()) {
            if (allAuthenticatedUsersIsAppRole) {
                constraint.treatAllAuthenticatedUsersAsApplicationRole();
            }
            this.context.addConstraint(constraint);
        }
        for (String role : webxml.getSecurityRoles()) {
            this.context.addSecurityRole(role);
        }
        for (ContextService service : webxml.getServiceRefs().values()) {
            this.context.getNamingResources().addService(service);
        }
        for (ServletDef servlet : webxml.getServlets().values()) {
            Wrapper wrapper = this.context.createWrapper();
            if (servlet.getLoadOnStartup() != null) {
                wrapper.setLoadOnStartup(servlet.getLoadOnStartup().intValue());
            }
            if (servlet.getEnabled() != null) {
                wrapper.setEnabled(servlet.getEnabled().booleanValue());
            }
            wrapper.setName(servlet.getServletName());
            Map<String, String> params = servlet.getParameterMap();
            for (Map.Entry<String, String> entry4 : params.entrySet()) {
                wrapper.addInitParameter(entry4.getKey(), entry4.getValue());
            }
            wrapper.setRunAs(servlet.getRunAs());
            Set<SecurityRoleRef> roleRefs = servlet.getSecurityRoleRefs();
            for (SecurityRoleRef roleRef : roleRefs) {
                wrapper.addSecurityReference(roleRef.getName(), roleRef.getLink());
            }
            wrapper.setServletClass(servlet.getServletClass());
            MultipartDef multipartdef = servlet.getMultipartDef();
            if (multipartdef != null) {
                if (multipartdef.getMaxFileSize() != null && multipartdef.getMaxRequestSize() != null && multipartdef.getFileSizeThreshold() != null) {
                    wrapper.setMultipartConfigElement(new MultipartConfigElement(multipartdef.getLocation(), Long.parseLong(multipartdef.getMaxFileSize()), Long.parseLong(multipartdef.getMaxRequestSize()), Integer.parseInt(multipartdef.getFileSizeThreshold())));
                } else {
                    wrapper.setMultipartConfigElement(new MultipartConfigElement(multipartdef.getLocation()));
                }
            }
            if (servlet.getAsyncSupported() != null) {
                wrapper.setAsyncSupported(servlet.getAsyncSupported().booleanValue());
            }
            wrapper.setOverridable(servlet.isOverridable());
            this.context.addChild(wrapper);
        }
        for (Map.Entry<String, String> entry5 : webxml.getServletMappings().entrySet()) {
            this.context.addServletMappingDecoded(entry5.getKey(), entry5.getValue());
        }
        SessionConfig sessionConfig = webxml.getSessionConfig();
        if (sessionConfig != null) {
            if (sessionConfig.getSessionTimeout() != null) {
                this.context.setSessionTimeout(sessionConfig.getSessionTimeout().intValue());
            }
            SessionCookieConfig scc = this.context.getServletContext().getSessionCookieConfig();
            scc.setName(sessionConfig.getCookieName());
            scc.setDomain(sessionConfig.getCookieDomain());
            scc.setPath(sessionConfig.getCookiePath());
            scc.setComment(sessionConfig.getCookieComment());
            if (sessionConfig.getCookieHttpOnly() != null) {
                scc.setHttpOnly(sessionConfig.getCookieHttpOnly().booleanValue());
            }
            if (sessionConfig.getCookieSecure() != null) {
                scc.setSecure(sessionConfig.getCookieSecure().booleanValue());
            }
            if (sessionConfig.getCookieMaxAge() != null) {
                scc.setMaxAge(sessionConfig.getCookieMaxAge().intValue());
            }
            if (sessionConfig.getSessionTrackingModes().size() > 0) {
                this.context.getServletContext().setSessionTrackingModes(sessionConfig.getSessionTrackingModes());
            }
        }
        for (String welcomeFile : webxml.getWelcomeFiles()) {
            if (welcomeFile != null && welcomeFile.length() > 0) {
                this.context.addWelcomeFile(welcomeFile);
            }
        }
        for (JspPropertyGroup jspPropertyGroup : webxml.getJspPropertyGroups()) {
            String jspServletName = this.context.findServletMapping("*.jsp");
            if (jspServletName == null) {
                jspServletName = "jsp";
            }
            if (this.context.findChild(jspServletName) != null) {
                for (String urlPattern : jspPropertyGroup.getUrlPatterns()) {
                    this.context.addServletMappingDecoded(urlPattern, jspServletName, true);
                }
            } else if (log.isDebugEnabled()) {
                for (String urlPattern2 : jspPropertyGroup.getUrlPatterns()) {
                    log.debug("Skipping " + urlPattern2 + " , no servlet " + jspServletName);
                }
            }
        }
        for (Map.Entry<String, String> entry6 : webxml.getPostConstructMethods().entrySet()) {
            this.context.addPostConstructMethod(entry6.getKey(), entry6.getValue());
        }
        for (Map.Entry<String, String> entry7 : webxml.getPreDestroyMethods().entrySet()) {
            this.context.addPreDestroyMethod(entry7.getKey(), entry7.getValue());
        }
    }

    private WebXml getTomcatWebXmlFragment(WebXmlParser webXmlParser) {
        WebXml webXmlTomcatFragment = createWebXml();
        webXmlTomcatFragment.setOverridable(true);
        webXmlTomcatFragment.setDistributable(true);
        webXmlTomcatFragment.setAlwaysAddWelcomeFiles(false);
        WebResource resource = this.context.getResources().getResource(Constants.TomcatWebXml);
        if (resource.isFile()) {
            try {
                InputSource source = new InputSource(resource.getURL().toURI().toString());
                source.setByteStream(resource.getInputStream());
                if (!webXmlParser.parseWebXml(source, webXmlTomcatFragment, false)) {
                    this.ok = false;
                }
            } catch (URISyntaxException e) {
                log.error(sm.getString("contextConfig.tomcatWebXmlError"), e);
            }
        }
        return webXmlTomcatFragment;
    }

    private WebXml getDefaultWebXmlFragment(WebXmlParser webXmlParser) {
        Host host = (Host) this.context.getParent();
        DefaultWebXmlCacheEntry entry = hostWebXmlCache.get(host);
        InputSource globalWebXml = getGlobalWebXmlSource();
        InputSource hostWebXml = getHostWebXmlSource();
        long globalTimeStamp = 0;
        long hostTimeStamp = 0;
        if (globalWebXml != null) {
            URLConnection uc = null;
            try {
                URL url = new URL(globalWebXml.getSystemId());
                uc = url.openConnection();
                globalTimeStamp = uc.getLastModified();
                if (uc != null) {
                    try {
                        uc.getInputStream().close();
                    } catch (IOException e) {
                        ExceptionUtils.handleThrowable(e);
                        globalTimeStamp = -1;
                    }
                }
            } catch (IOException e2) {
                globalTimeStamp = -1;
                if (uc != null) {
                    try {
                        uc.getInputStream().close();
                    } catch (IOException e3) {
                        ExceptionUtils.handleThrowable(e3);
                        globalTimeStamp = -1;
                    }
                }
            } catch (Throwable th) {
                if (uc != null) {
                    try {
                        uc.getInputStream().close();
                    } catch (IOException e4) {
                        ExceptionUtils.handleThrowable(e4);
                    }
                }
                throw th;
            }
        }
        if (hostWebXml != null) {
            URLConnection uc2 = null;
            try {
                URL url2 = new URL(hostWebXml.getSystemId());
                uc2 = url2.openConnection();
                hostTimeStamp = uc2.getLastModified();
                if (uc2 != null) {
                    try {
                        uc2.getInputStream().close();
                    } catch (IOException e5) {
                        ExceptionUtils.handleThrowable(e5);
                        hostTimeStamp = -1;
                    }
                }
            } catch (IOException e6) {
                hostTimeStamp = -1;
                if (uc2 != null) {
                    try {
                        uc2.getInputStream().close();
                    } catch (IOException e7) {
                        ExceptionUtils.handleThrowable(e7);
                        hostTimeStamp = -1;
                    }
                }
            } catch (Throwable th2) {
                if (uc2 != null) {
                    try {
                        uc2.getInputStream().close();
                    } catch (IOException e8) {
                        ExceptionUtils.handleThrowable(e8);
                    }
                }
                throw th2;
            }
        }
        if (entry != null && entry.getGlobalTimeStamp() == globalTimeStamp && entry.getHostTimeStamp() == hostTimeStamp) {
            InputSourceUtil.close(globalWebXml);
            InputSourceUtil.close(hostWebXml);
            return entry.getWebXml();
        }
        synchronized (host.getPipeline()) {
            DefaultWebXmlCacheEntry entry2 = hostWebXmlCache.get(host);
            if (entry2 != null && entry2.getGlobalTimeStamp() == globalTimeStamp && entry2.getHostTimeStamp() == hostTimeStamp) {
                return entry2.getWebXml();
            }
            WebXml webXmlDefaultFragment = createWebXml();
            webXmlDefaultFragment.setOverridable(true);
            webXmlDefaultFragment.setDistributable(true);
            webXmlDefaultFragment.setAlwaysAddWelcomeFiles(false);
            if (globalWebXml == null) {
                log.info(sm.getString("contextConfig.defaultMissing"));
            } else if (!webXmlParser.parseWebXml(globalWebXml, webXmlDefaultFragment, false)) {
                this.ok = false;
            }
            webXmlDefaultFragment.setReplaceWelcomeFiles(true);
            if (!webXmlParser.parseWebXml(hostWebXml, webXmlDefaultFragment, false)) {
                this.ok = false;
            }
            if (globalTimeStamp != -1 && hostTimeStamp != -1) {
                hostWebXmlCache.put(host, new DefaultWebXmlCacheEntry(webXmlDefaultFragment, globalTimeStamp, hostTimeStamp));
            }
            return webXmlDefaultFragment;
        }
    }

    private void convertJsps(WebXml webXml) {
        Map<String, String> jspInitParams;
        ServletDef jspServlet = webXml.getServlets().get("jsp");
        if (jspServlet == null) {
            jspInitParams = new HashMap<>();
            Wrapper w = (Wrapper) this.context.findChild("jsp");
            if (w != null) {
                String[] params = w.findInitParameters();
                for (String param : params) {
                    jspInitParams.put(param, w.findInitParameter(param));
                }
            }
        } else {
            jspInitParams = jspServlet.getParameterMap();
        }
        for (ServletDef servletDef : webXml.getServlets().values()) {
            if (servletDef.getJspFile() != null) {
                convertJsp(servletDef, jspInitParams);
            }
        }
    }

    private void convertJsp(ServletDef servletDef, Map<String, String> jspInitParams) {
        servletDef.setServletClass(org.apache.catalina.core.Constants.JSP_SERVLET_CLASS);
        String jspFile = servletDef.getJspFile();
        if (jspFile != null && !jspFile.startsWith("/")) {
            if (this.context.isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("contextConfig.jspFile.warning", jspFile));
                }
                jspFile = "/" + jspFile;
            } else {
                throw new IllegalArgumentException(sm.getString("contextConfig.jspFile.error", jspFile));
            }
        }
        servletDef.getParameterMap().put("jspFile", jspFile);
        servletDef.setJspFile(null);
        for (Map.Entry<String, String> initParam : jspInitParams.entrySet()) {
            servletDef.addInitParameter(initParam.getKey(), initParam.getValue());
        }
    }

    protected WebXml createWebXml() {
        return new WebXml();
    }

    protected void processServletContainerInitializers() {
        Class<?>[] types;
        try {
            WebappServiceLoader<ServletContainerInitializer> loader = new WebappServiceLoader<>(this.context);
            List<ServletContainerInitializer> detectedScis = loader.load(ServletContainerInitializer.class);
            for (ServletContainerInitializer sci : detectedScis) {
                this.initializerClassMap.put(sci, new HashSet());
                try {
                    HandlesTypes ht = (HandlesTypes) sci.getClass().getAnnotation(HandlesTypes.class);
                    if (ht != null && (types = ht.value()) != null) {
                        for (Class<?> type : types) {
                            if (type.isAnnotation()) {
                                this.handlesTypesAnnotations = true;
                            } else {
                                this.handlesTypesNonAnnotations = true;
                            }
                            Set<ServletContainerInitializer> scis = this.typeInitializerMap.get(type);
                            if (scis == null) {
                                scis = new HashSet<>();
                                this.typeInitializerMap.put(type, scis);
                            }
                            scis.add(sci);
                        }
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.info(sm.getString("contextConfig.sci.debug", sci.getClass().getName()), e);
                    } else {
                        log.info(sm.getString("contextConfig.sci.info", sci.getClass().getName()));
                    }
                }
            }
        } catch (IOException e2) {
            log.error(sm.getString("contextConfig.servletContainerInitializerFail", this.context.getName()), e2);
            this.ok = false;
        }
    }

    protected void processResourceJARs(Set<WebXml> fragments) {
        for (WebXml fragment : fragments) {
            URL url = fragment.getURL();
            try {
                if (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) || url.toString().endsWith(".jar")) {
                    Jar jar = JarFactory.newInstance(url);
                    Throwable th = null;
                    try {
                        jar.nextEntry();
                        String entryName = jar.getEntryName();
                        while (true) {
                            if (entryName == null) {
                                break;
                            } else if (entryName.startsWith("META-INF/resources/")) {
                                this.context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", url, "/META-INF/resources");
                                break;
                            } else {
                                jar.nextEntry();
                                entryName = jar.getEntryName();
                            }
                        }
                        if (jar != null) {
                            if (0 != 0) {
                                try {
                                    jar.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                jar.close();
                            }
                        }
                    } catch (Throwable th3) {
                        try {
                            throw th3;
                            break;
                        } catch (Throwable th4) {
                            if (jar != null) {
                                if (th3 != null) {
                                    try {
                                        jar.close();
                                    } catch (Throwable th5) {
                                        th3.addSuppressed(th5);
                                    }
                                } else {
                                    jar.close();
                                }
                            }
                            throw th4;
                            break;
                        }
                    }
                } else if ("file".equals(url.getProtocol())) {
                    File file = new File(url.toURI());
                    File resources = new File(file, "META-INF/resources/");
                    if (resources.isDirectory()) {
                        this.context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", resources.getAbsolutePath(), null, "/");
                    }
                }
            } catch (IOException e) {
                log.error(sm.getString("contextConfig.resourceJarFail", url, this.context.getName()));
            } catch (URISyntaxException e2) {
                log.error(sm.getString("contextConfig.resourceJarFail", url, this.context.getName()));
            }
        }
    }

    protected InputSource getGlobalWebXmlSource() {
        if (this.defaultWebXml == null && (this.context instanceof StandardContext)) {
            this.defaultWebXml = ((StandardContext) this.context).getDefaultWebXml();
        }
        if (this.defaultWebXml == null) {
            getDefaultWebXml();
        }
        if (Constants.NoDefaultWebXml.equals(this.defaultWebXml)) {
            return null;
        }
        return getWebXmlSource(this.defaultWebXml, this.context.getCatalinaBase().getPath());
    }

    protected InputSource getHostWebXmlSource() {
        File hostConfigBase = getHostConfigBase();
        if (hostConfigBase == null) {
            return null;
        }
        return getWebXmlSource(Constants.HostWebXml, hostConfigBase.getPath());
    }

    protected InputSource getContextWebXmlSource() {
        InputStream stream = null;
        InputSource source = null;
        URL url = null;
        ServletContext servletContext = this.context.getServletContext();
        if (servletContext != null) {
            try {
                String altDDName = (String) servletContext.getAttribute(Globals.ALT_DD_ATTR);
                if (altDDName != null) {
                    try {
                        stream = new FileInputStream(altDDName);
                        url = new File(altDDName).toURI().toURL();
                    } catch (FileNotFoundException e) {
                        log.error(sm.getString("contextConfig.altDDNotFound", altDDName));
                    } catch (MalformedURLException e2) {
                        log.error(sm.getString("contextConfig.applicationUrl"));
                    }
                } else {
                    stream = servletContext.getResourceAsStream("/WEB-INF/web.xml");
                    try {
                        url = servletContext.getResource("/WEB-INF/web.xml");
                    } catch (MalformedURLException e3) {
                        log.error(sm.getString("contextConfig.applicationUrl"));
                    }
                }
            } finally {
            }
            if (0 == 0 && stream != null) {
                try {
                    stream.close();
                } catch (IOException e4) {
                }
            }
        }
        if (stream == null || url == null) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("contextConfig.applicationMissing") + " " + this.context);
            }
        } else {
            source = new InputSource(url.toExternalForm());
            source.setByteStream(stream);
        }
        source = source;
        return source;
    }

    protected InputSource getWebXmlSource(String filename, String path) {
        File file = new File(filename);
        if (!file.isAbsolute()) {
            file = new File(path, filename);
        }
        InputStream stream = null;
        InputSource source = null;
        try {
            try {
                if (file.exists()) {
                    source = new InputSource(file.getAbsoluteFile().toURI().toString());
                    stream = new FileInputStream(file);
                } else {
                    stream = getClass().getClassLoader().getResourceAsStream(filename);
                    if (stream != null) {
                        source = new InputSource(getClass().getClassLoader().getResource(filename).toURI().toString());
                    }
                }
                if (stream != null && source != null) {
                    source.setByteStream(stream);
                }
                if (source == null && stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                log.error(sm.getString("contextConfig.defaultError", filename, file), e2);
                if (source == null && stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e3) {
                    }
                }
            }
            return source;
        } catch (Throwable th) {
            if (source == null && stream != null) {
                try {
                    stream.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    protected Map<String, WebXml> processJarsForWebFragments(WebXml application, WebXmlParser webXmlParser) {
        JarScanner jarScanner = this.context.getJarScanner();
        boolean delegate = false;
        if (this.context instanceof StandardContext) {
            delegate = ((StandardContext) this.context).getDelegate();
        }
        boolean parseRequired = true;
        Set<String> absoluteOrder = application.getAbsoluteOrdering();
        if (absoluteOrder != null && absoluteOrder.isEmpty() && !this.context.getXmlValidation()) {
            parseRequired = false;
        }
        FragmentJarScannerCallback callback = new FragmentJarScannerCallback(webXmlParser, delegate, parseRequired);
        jarScanner.scan(JarScanType.PLUGGABILITY, this.context.getServletContext(), callback);
        if (!callback.isOk()) {
            this.ok = false;
        }
        return callback.getFragments();
    }

    protected void processAnnotations(Set<WebXml> fragments, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        for (WebXml fragment : fragments) {
            boolean htOnly = handlesTypesOnly || !fragment.getWebappJar() || fragment.isMetadataComplete();
            WebXml annotations = new WebXml();
            annotations.setDistributable(true);
            URL url = fragment.getURL();
            processAnnotationsUrl(url, annotations, htOnly, javaClassCache);
            Set<WebXml> set = new HashSet<>();
            set.add(annotations);
            fragment.merge(set);
        }
    }

    protected void processAnnotationsWebResource(WebResource webResource, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (webResource.isDirectory()) {
            WebResource[] webResources = webResource.getWebResourceRoot().listResources(webResource.getWebappPath());
            if (webResources.length > 0) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("contextConfig.processAnnotationsWebDir.debug", webResource.getURL()));
                }
                for (WebResource r : webResources) {
                    processAnnotationsWebResource(r, fragment, handlesTypesOnly, javaClassCache);
                }
            }
        } else if (webResource.isFile() && webResource.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
            try {
                InputStream is = webResource.getInputStream();
                processAnnotationsStream(is, fragment, handlesTypesOnly, javaClassCache);
                if (is != null) {
                    if (0 != 0) {
                        is.close();
                    } else {
                        is.close();
                    }
                }
            } catch (IOException e) {
                log.error(sm.getString("contextConfig.inputStreamWebResource", webResource.getWebappPath()), e);
            } catch (ClassFormatException e2) {
                log.error(sm.getString("contextConfig.inputStreamWebResource", webResource.getWebappPath()), e2);
            }
        }
    }

    protected void processAnnotationsUrl(URL url, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (url == null) {
            return;
        }
        if (ResourceUtils.URL_PROTOCOL_JAR.equals(url.getProtocol()) || url.toString().endsWith(".jar")) {
            processAnnotationsJar(url, fragment, handlesTypesOnly, javaClassCache);
        } else if ("file".equals(url.getProtocol())) {
            try {
                processAnnotationsFile(new File(url.toURI()), fragment, handlesTypesOnly, javaClassCache);
            } catch (URISyntaxException e) {
                log.error(sm.getString("contextConfig.fileUrl", url), e);
            }
        } else {
            log.error(sm.getString("contextConfig.unknownUrlProtocol", url.getProtocol(), url));
        }
    }

    protected void processAnnotationsJar(URL url, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        try {
            Jar jar = JarFactory.newInstance(url);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("contextConfig.processAnnotationsJar.debug", url));
            }
            jar.nextEntry();
            for (String entryName = jar.getEntryName(); entryName != null; entryName = jar.getEntryName()) {
                if (entryName.endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
                    try {
                        InputStream is = jar.getEntryInputStream();
                        Throwable th = null;
                        try {
                            processAnnotationsStream(is, fragment, handlesTypesOnly, javaClassCache);
                            if (is != null) {
                                if (0 != 0) {
                                    try {
                                        is.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    is.close();
                                }
                            }
                        } catch (Throwable th3) {
                            try {
                                throw th3;
                            } catch (Throwable th4) {
                                if (is != null) {
                                    if (th3 != null) {
                                        try {
                                            is.close();
                                        } catch (Throwable th5) {
                                            th3.addSuppressed(th5);
                                        }
                                    } else {
                                        is.close();
                                    }
                                }
                                throw th4;
                            }
                        }
                    } catch (IOException e) {
                        log.error(sm.getString("contextConfig.inputStreamJar", entryName, url), e);
                    } catch (ClassFormatException e2) {
                        log.error(sm.getString("contextConfig.inputStreamJar", entryName, url), e2);
                    }
                }
                jar.nextEntry();
            }
            if (jar != null) {
                if (0 != 0) {
                    jar.close();
                } else {
                    jar.close();
                }
            }
        } catch (IOException e3) {
            log.error(sm.getString("contextConfig.jarFile", url), e3);
        }
    }

    protected void processAnnotationsFile(File file, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (file.isDirectory()) {
            String[] dirs = file.list();
            if (dirs != null) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("contextConfig.processAnnotationsDir.debug", file));
                }
                for (String dir : dirs) {
                    processAnnotationsFile(new File(file, dir), fragment, handlesTypesOnly, javaClassCache);
                }
            }
        } else if (file.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX) && file.canRead()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                processAnnotationsStream(fis, fragment, handlesTypesOnly, javaClassCache);
                if (fis != null) {
                    if (0 != 0) {
                        fis.close();
                    } else {
                        fis.close();
                    }
                }
            } catch (IOException e) {
                log.error(sm.getString("contextConfig.inputStreamFile", file.getAbsolutePath()), e);
            } catch (ClassFormatException e2) {
                log.error(sm.getString("contextConfig.inputStreamFile", file.getAbsolutePath()), e2);
            }
        }
    }

    protected void processAnnotationsStream(InputStream is, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) throws ClassFormatException, IOException {
        AnnotationEntry[] annotationsEntries;
        ClassParser parser = new ClassParser(is);
        JavaClass clazz = parser.parse();
        checkHandlesTypes(clazz, javaClassCache);
        if (!handlesTypesOnly && (annotationsEntries = clazz.getAnnotationEntries()) != null) {
            String className = clazz.getClassName();
            for (AnnotationEntry ae : annotationsEntries) {
                String type = ae.getAnnotationType();
                if ("Ljavax/servlet/annotation/WebServlet;".equals(type)) {
                    processAnnotationWebServlet(className, ae, fragment);
                } else if ("Ljavax/servlet/annotation/WebFilter;".equals(type)) {
                    processAnnotationWebFilter(className, ae, fragment);
                } else if ("Ljavax/servlet/annotation/WebListener;".equals(type)) {
                    fragment.addListener(className);
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:66:0x0115, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void checkHandlesTypes(org.apache.tomcat.util.bcel.classfile.JavaClass r13, java.util.Map<java.lang.String, org.apache.catalina.startup.ContextConfig.JavaClassCacheEntry> r14) {
        /*
            Method dump skipped, instructions count: 470
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.ContextConfig.checkHandlesTypes(org.apache.tomcat.util.bcel.classfile.JavaClass, java.util.Map):void");
    }

    private String classHierarchyToString(String className, JavaClassCacheEntry entry, Map<String, JavaClassCacheEntry> javaClassCache) {
        StringBuilder msg = new StringBuilder(className);
        msg.append("->");
        String parentName = entry.getSuperclassName();
        JavaClassCacheEntry parent = javaClassCache.get(parentName);
        int count = 0;
        while (count < 100 && parent != null && parent != entry) {
            msg.append(parentName);
            msg.append("->");
            count++;
            parentName = parent.getSuperclassName();
            parent = javaClassCache.get(parentName);
        }
        msg.append(parentName);
        return msg.toString();
    }

    private void populateJavaClassCache(String className, JavaClass javaClass, Map<String, JavaClassCacheEntry> javaClassCache) {
        String[] interfaceNames;
        if (javaClassCache.containsKey(className)) {
            return;
        }
        javaClassCache.put(className, new JavaClassCacheEntry(javaClass));
        populateJavaClassCache(javaClass.getSuperclassName(), javaClassCache);
        for (String interfaceName : javaClass.getInterfaceNames()) {
            populateJavaClassCache(interfaceName, javaClassCache);
        }
    }

    private void populateJavaClassCache(String className, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (javaClassCache.containsKey(className)) {
            return;
        }
        String name = className.replace('.', '/') + ClassUtils.CLASS_FILE_SUFFIX;
        try {
            InputStream is = this.context.getLoader().getClassLoader().getResourceAsStream(name);
            if (is == null) {
                if (is != null) {
                    if (0 != 0) {
                        is.close();
                        return;
                    } else {
                        is.close();
                        return;
                    }
                }
                return;
            }
            ClassParser parser = new ClassParser(is);
            JavaClass clazz = parser.parse();
            populateJavaClassCache(clazz.getClassName(), clazz, javaClassCache);
            if (is != null) {
                if (0 != 0) {
                    is.close();
                } else {
                    is.close();
                }
            }
        } catch (IOException e) {
            log.debug(sm.getString("contextConfig.invalidSciHandlesTypes", className), e);
        } catch (ClassFormatException e2) {
            log.debug(sm.getString("contextConfig.invalidSciHandlesTypes", className), e2);
        }
    }

    private void populateSCIsForCacheEntry(JavaClassCacheEntry cacheEntry, Map<String, JavaClassCacheEntry> javaClassCache) {
        String[] interfaceNames;
        Set<ServletContainerInitializer> result = new HashSet<>();
        String superClassName = cacheEntry.getSuperclassName();
        JavaClassCacheEntry superClassCacheEntry = javaClassCache.get(superClassName);
        if (cacheEntry.equals(superClassCacheEntry)) {
            cacheEntry.setSciSet(EMPTY_SCI_SET);
            return;
        }
        if (superClassCacheEntry != null) {
            if (superClassCacheEntry.getSciSet() == null) {
                populateSCIsForCacheEntry(superClassCacheEntry, javaClassCache);
            }
            result.addAll(superClassCacheEntry.getSciSet());
        }
        result.addAll(getSCIsForClass(superClassName));
        for (String interfaceName : cacheEntry.getInterfaceNames()) {
            JavaClassCacheEntry interfaceEntry = javaClassCache.get(interfaceName);
            if (interfaceEntry != null) {
                if (interfaceEntry.getSciSet() == null) {
                    populateSCIsForCacheEntry(interfaceEntry, javaClassCache);
                }
                result.addAll(interfaceEntry.getSciSet());
            }
            result.addAll(getSCIsForClass(interfaceName));
        }
        cacheEntry.setSciSet(result.isEmpty() ? EMPTY_SCI_SET : result);
    }

    private Set<ServletContainerInitializer> getSCIsForClass(String className) {
        for (Map.Entry<Class<?>, Set<ServletContainerInitializer>> entry : this.typeInitializerMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (!clazz.isAnnotation() && clazz.getName().equals(className)) {
                return entry.getValue();
            }
        }
        return EMPTY_SCI_SET;
    }

    private static final String getClassName(String internalForm) {
        if (!internalForm.startsWith("L")) {
            return internalForm;
        }
        return internalForm.substring(1, internalForm.length() - 1).replace('/', '.');
    }

    protected void processAnnotationWebServlet(String className, AnnotationEntry ae, WebXml fragment) {
        boolean isWebXMLservletDef;
        String[] strArr;
        String servletName = null;
        List<ElementValuePair> evps = ae.getElementValuePairs();
        Iterator<ElementValuePair> it = evps.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ElementValuePair evp = it.next();
            if ("name".equals(evp.getNameString())) {
                servletName = evp.getValue().stringifyValue();
                break;
            }
        }
        if (servletName == null) {
            servletName = className;
        }
        ServletDef servletDef = fragment.getServlets().get(servletName);
        if (servletDef == null) {
            servletDef = new ServletDef();
            servletDef.setServletName(servletName);
            servletDef.setServletClass(className);
            isWebXMLservletDef = false;
        } else {
            isWebXMLservletDef = true;
        }
        boolean urlPatternsSet = false;
        String[] urlPatterns = null;
        for (ElementValuePair evp2 : evps) {
            String name = evp2.getNameString();
            if ("value".equals(name) || "urlPatterns".equals(name)) {
                if (urlPatternsSet) {
                    throw new IllegalArgumentException(sm.getString("contextConfig.urlPatternValue", "WebServlet", className));
                }
                urlPatternsSet = true;
                urlPatterns = processAnnotationsStringArray(evp2.getValue());
            } else if ("description".equals(name)) {
                if (servletDef.getDescription() == null) {
                    servletDef.setDescription(evp2.getValue().stringifyValue());
                }
            } else if ("displayName".equals(name)) {
                if (servletDef.getDisplayName() == null) {
                    servletDef.setDisplayName(evp2.getValue().stringifyValue());
                }
            } else if ("largeIcon".equals(name)) {
                if (servletDef.getLargeIcon() == null) {
                    servletDef.setLargeIcon(evp2.getValue().stringifyValue());
                }
            } else if ("smallIcon".equals(name)) {
                if (servletDef.getSmallIcon() == null) {
                    servletDef.setSmallIcon(evp2.getValue().stringifyValue());
                }
            } else if ("asyncSupported".equals(name)) {
                if (servletDef.getAsyncSupported() == null) {
                    servletDef.setAsyncSupported(evp2.getValue().stringifyValue());
                }
            } else if ("loadOnStartup".equals(name)) {
                if (servletDef.getLoadOnStartup() == null) {
                    servletDef.setLoadOnStartup(evp2.getValue().stringifyValue());
                }
            } else if ("initParams".equals(name)) {
                Map<String, String> initParams = processAnnotationWebInitParams(evp2.getValue());
                if (isWebXMLservletDef) {
                    Map<String, String> webXMLInitParams = servletDef.getParameterMap();
                    for (Map.Entry<String, String> entry : initParams.entrySet()) {
                        if (webXMLInitParams.get(entry.getKey()) == null) {
                            servletDef.addInitParameter(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    for (Map.Entry<String, String> entry2 : initParams.entrySet()) {
                        servletDef.addInitParameter(entry2.getKey(), entry2.getValue());
                    }
                }
            }
        }
        if (!isWebXMLservletDef && urlPatterns != null) {
            fragment.addServlet(servletDef);
        }
        if (urlPatterns != null && !fragment.getServletMappings().containsValue(servletName)) {
            for (String urlPattern : urlPatterns) {
                fragment.addServletMapping(urlPattern, servletName);
            }
        }
    }

    protected void processAnnotationWebFilter(String className, AnnotationEntry ae, WebXml fragment) {
        boolean isWebXMLfilterDef;
        String[] dispatcherNames;
        String[] uRLPatterns;
        String filterName = null;
        List<ElementValuePair> evps = ae.getElementValuePairs();
        Iterator<ElementValuePair> it = evps.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ElementValuePair evp = it.next();
            if ("filterName".equals(evp.getNameString())) {
                filterName = evp.getValue().stringifyValue();
                break;
            }
        }
        if (filterName == null) {
            filterName = className;
        }
        FilterDef filterDef = fragment.getFilters().get(filterName);
        FilterMap filterMap = new FilterMap();
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            filterDef.setFilterClass(className);
            isWebXMLfilterDef = false;
        } else {
            isWebXMLfilterDef = true;
        }
        boolean urlPatternsSet = false;
        boolean servletNamesSet = false;
        boolean dispatchTypesSet = false;
        for (ElementValuePair evp2 : evps) {
            String name = evp2.getNameString();
            if ("value".equals(name) || "urlPatterns".equals(name)) {
                if (urlPatternsSet) {
                    throw new IllegalArgumentException(sm.getString("contextConfig.urlPatternValue", "WebFilter", className));
                }
                String[] urlPatterns = processAnnotationsStringArray(evp2.getValue());
                urlPatternsSet = urlPatterns.length > 0;
                for (String urlPattern : urlPatterns) {
                    filterMap.addURLPattern(urlPattern);
                }
            } else if ("servletNames".equals(name)) {
                String[] servletNames = processAnnotationsStringArray(evp2.getValue());
                servletNamesSet = servletNames.length > 0;
                for (String servletName : servletNames) {
                    filterMap.addServletName(servletName);
                }
            } else if ("dispatcherTypes".equals(name)) {
                String[] dispatcherTypes = processAnnotationsStringArray(evp2.getValue());
                dispatchTypesSet = dispatcherTypes.length > 0;
                for (String dispatcherType : dispatcherTypes) {
                    filterMap.setDispatcher(dispatcherType);
                }
            } else if ("description".equals(name)) {
                if (filterDef.getDescription() == null) {
                    filterDef.setDescription(evp2.getValue().stringifyValue());
                }
            } else if ("displayName".equals(name)) {
                if (filterDef.getDisplayName() == null) {
                    filterDef.setDisplayName(evp2.getValue().stringifyValue());
                }
            } else if ("largeIcon".equals(name)) {
                if (filterDef.getLargeIcon() == null) {
                    filterDef.setLargeIcon(evp2.getValue().stringifyValue());
                }
            } else if ("smallIcon".equals(name)) {
                if (filterDef.getSmallIcon() == null) {
                    filterDef.setSmallIcon(evp2.getValue().stringifyValue());
                }
            } else if ("asyncSupported".equals(name)) {
                if (filterDef.getAsyncSupported() == null) {
                    filterDef.setAsyncSupported(evp2.getValue().stringifyValue());
                }
            } else if ("initParams".equals(name)) {
                Map<String, String> initParams = processAnnotationWebInitParams(evp2.getValue());
                if (isWebXMLfilterDef) {
                    Map<String, String> webXMLInitParams = filterDef.getParameterMap();
                    for (Map.Entry<String, String> entry : initParams.entrySet()) {
                        if (webXMLInitParams.get(entry.getKey()) == null) {
                            filterDef.addInitParameter(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    for (Map.Entry<String, String> entry2 : initParams.entrySet()) {
                        filterDef.addInitParameter(entry2.getKey(), entry2.getValue());
                    }
                }
            }
        }
        if (!isWebXMLfilterDef) {
            fragment.addFilter(filterDef);
            if (urlPatternsSet || servletNamesSet) {
                filterMap.setFilterName(filterName);
                fragment.addFilterMapping(filterMap);
            }
        }
        if (urlPatternsSet || dispatchTypesSet) {
            Set<FilterMap> fmap = fragment.getFilterMappings();
            FilterMap descMap = null;
            Iterator<FilterMap> it2 = fmap.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                FilterMap map = it2.next();
                if (filterName.equals(map.getFilterName())) {
                    descMap = map;
                    break;
                }
            }
            if (descMap != null) {
                String[] urlsPatterns = descMap.getURLPatterns();
                if (urlPatternsSet && (urlsPatterns == null || urlsPatterns.length == 0)) {
                    for (String urlPattern2 : filterMap.getURLPatterns()) {
                        descMap.addURLPattern(urlPattern2);
                    }
                }
                String[] dispatcherNames2 = descMap.getDispatcherNames();
                if (dispatchTypesSet) {
                    if (dispatcherNames2 == null || dispatcherNames2.length == 0) {
                        for (String dis : filterMap.getDispatcherNames()) {
                            descMap.setDispatcher(dis);
                        }
                    }
                }
            }
        }
    }

    protected String[] processAnnotationsStringArray(ElementValue ev) {
        List<String> values = new ArrayList<>();
        if (ev instanceof ArrayElementValue) {
            ElementValue[] arrayValues = ((ArrayElementValue) ev).getElementValuesArray();
            for (ElementValue value : arrayValues) {
                values.add(value.stringifyValue());
            }
        } else {
            values.add(ev.stringifyValue());
        }
        String[] result = new String[values.size()];
        return (String[]) values.toArray(result);
    }

    protected Map<String, String> processAnnotationWebInitParams(ElementValue ev) {
        Map<String, String> result = new HashMap<>();
        if (ev instanceof ArrayElementValue) {
            ElementValue[] arrayValues = ((ArrayElementValue) ev).getElementValuesArray();
            for (ElementValue value : arrayValues) {
                if (value instanceof AnnotationElementValue) {
                    List<ElementValuePair> evps = ((AnnotationElementValue) value).getAnnotationEntry().getElementValuePairs();
                    String initParamName = null;
                    String initParamValue = null;
                    for (ElementValuePair evp : evps) {
                        if ("name".equals(evp.getNameString())) {
                            initParamName = evp.getValue().stringifyValue();
                        } else if ("value".equals(evp.getNameString())) {
                            initParamValue = evp.getValue().stringifyValue();
                        }
                    }
                    result.put(initParamName, initParamValue);
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ContextConfig$DefaultWebXmlCacheEntry.class */
    public static class DefaultWebXmlCacheEntry {
        private final WebXml webXml;
        private final long globalTimeStamp;
        private final long hostTimeStamp;

        public DefaultWebXmlCacheEntry(WebXml webXml, long globalTimeStamp, long hostTimeStamp) {
            this.webXml = webXml;
            this.globalTimeStamp = globalTimeStamp;
            this.hostTimeStamp = hostTimeStamp;
        }

        public WebXml getWebXml() {
            return this.webXml;
        }

        public long getGlobalTimeStamp() {
            return this.globalTimeStamp;
        }

        public long getHostTimeStamp() {
            return this.hostTimeStamp;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ContextConfig$JavaClassCacheEntry.class */
    public static class JavaClassCacheEntry {
        public final String superclassName;
        public final String[] interfaceNames;
        private Set<ServletContainerInitializer> sciSet = null;

        public JavaClassCacheEntry(JavaClass javaClass) {
            this.superclassName = javaClass.getSuperclassName();
            this.interfaceNames = javaClass.getInterfaceNames();
        }

        public String getSuperclassName() {
            return this.superclassName;
        }

        public String[] getInterfaceNames() {
            return this.interfaceNames;
        }

        public Set<ServletContainerInitializer> getSciSet() {
            return this.sciSet;
        }

        public void setSciSet(Set<ServletContainerInitializer> sciSet) {
            this.sciSet = sciSet;
        }
    }
}