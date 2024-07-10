package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.security.DeployXmlPermission;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig.class */
public class HostConfig implements LifecycleListener {
    private static final Log log = LogFactory.getLog(HostConfig.class);
    protected static final StringManager sm = StringManager.getManager(HostConfig.class);
    protected static final long FILE_MODIFICATION_RESOLUTION_MS = 1000;
    protected String contextClass = "org.apache.catalina.core.StandardContext";
    protected Host host = null;
    protected ObjectName oname = null;
    protected boolean deployXML = false;
    protected boolean copyXML = false;
    protected boolean unpackWARs = false;
    protected final Map<String, DeployedApplication> deployed = new ConcurrentHashMap();
    protected final ArrayList<String> serviced = new ArrayList<>();
    protected Digester digester = createDigester(this.contextClass);
    private final Object digesterLock = new Object();
    protected final Set<String> invalidWars = new HashSet();

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        if (!oldContextClass.equals(contextClass)) {
            synchronized (this.digesterLock) {
                this.digester = createDigester(getContextClass());
            }
        }
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    private boolean isDeployThisXML(File docBase, ContextName cn) {
        Policy currentPolicy;
        boolean deployThisXML = isDeployXML();
        if (Globals.IS_SECURITY_ENABLED && !deployThisXML && (currentPolicy = Policy.getPolicy()) != null) {
            try {
                URL contextRootUrl = docBase.toURI().toURL();
                CodeSource cs = new CodeSource(contextRootUrl, (Certificate[]) null);
                PermissionCollection pc = currentPolicy.getPermissions(cs);
                Permission p = new DeployXmlPermission(cn.getBaseName());
                if (pc.implies(p)) {
                    deployThisXML = true;
                }
            } catch (MalformedURLException e) {
                log.warn("hostConfig.docBaseUrlInvalid", e);
            }
        }
        return deployThisXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.host = (Host) event.getLifecycle();
            if (this.host instanceof StandardHost) {
                setCopyXML(((StandardHost) this.host).isCopyXML());
                setDeployXML(((StandardHost) this.host).isDeployXML());
                setUnpackWARs(((StandardHost) this.host).isUnpackWARs());
                setContextClass(((StandardHost) this.host).getContextClass());
            }
            if (event.getType().equals(Lifecycle.PERIODIC_EVENT)) {
                check();
            } else if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
                beforeStart();
            } else if (event.getType().equals(Lifecycle.START_EVENT)) {
                start();
            } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
                stop();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("hostConfig.cce", event.getLifecycle()), e);
        }
    }

    public synchronized void addServiced(String name) {
        this.serviced.add(name);
    }

    public synchronized boolean isServiced(String name) {
        return this.serviced.contains(name);
    }

    public synchronized void removeServiced(String name) {
        this.serviced.remove(name);
    }

    public long getDeploymentTime(String name) {
        DeployedApplication app = this.deployed.get(name);
        if (app == null) {
            return 0L;
        }
        return app.timestamp;
    }

    public boolean isDeployed(String name) {
        return this.deployed.containsKey(name);
    }

    protected static Digester createDigester(String contextClassName) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Context", contextClassName, "className");
        digester.addSetProperties("Context");
        return digester;
    }

    protected File returnCanonicalPath(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.host.getCatalinaBase(), path);
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }

    public String getConfigBaseName() {
        return this.host.getConfigBaseFile().getAbsolutePath();
    }

    protected void deployApps() {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        String[] filteredAppPaths = filterAppPaths(appBase.list());
        deployDescriptors(configBase, configBase.list());
        deployWARs(appBase, filteredAppPaths);
        deployDirectories(appBase, filteredAppPaths);
    }

    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        Pattern filter = this.host.getDeployIgnorePattern();
        if (filter == null || unfilteredAppPaths == null) {
            return unfilteredAppPaths;
        }
        List<String> filteredList = new ArrayList<>();
        Matcher matcher = null;
        for (String appPath : unfilteredAppPaths) {
            if (matcher == null) {
                matcher = filter.matcher(appPath);
            } else {
                matcher.reset(appPath);
            }
            if (matcher.matches()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("hostConfig.ignorePath", appPath));
                }
            } else {
                filteredList.add(appPath);
            }
        }
        return (String[]) filteredList.toArray(new String[filteredList.size()]);
    }

    protected void deployApps(String name) {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        ContextName cn = new ContextName(name, false);
        String baseName = cn.getBaseName();
        if (deploymentExists(cn.getName())) {
            return;
        }
        File xml = new File(configBase, baseName + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);
        if (xml.exists()) {
            deployDescriptor(cn, xml);
            return;
        }
        File war = new File(appBase, baseName + ".war");
        if (war.exists()) {
            deployWAR(cn, war);
            return;
        }
        File dir = new File(appBase, baseName);
        if (dir.exists()) {
            deployDirectory(cn, dir);
        }
    }

    protected void deployDescriptors(File configBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File contextXml = new File(configBase, files[i]);
            if (files[i].toLowerCase(Locale.ENGLISH).endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                ContextName cn = new ContextName(files[i], true);
                if (!isServiced(cn.getName()) && !deploymentExists(cn.getName())) {
                    results.add(es.submit(new DeployDescriptor(this, cn, contextXml)));
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDescriptor.threaded.error"), e);
            }
        }
    }

    protected void deployDescriptor(ContextName cn, File contextXml) {
        FileInputStream fis;
        DeployedApplication deployedApp = new DeployedApplication(cn.getName(), true);
        long startTime = 0;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info(sm.getString("hostConfig.deployDescriptor", contextXml.getAbsolutePath()));
        }
        Context context = null;
        boolean isExternalWar = false;
        boolean isExternal = false;
        try {
            fis = new FileInputStream(contextXml);
        } catch (Throwable t) {
            try {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), t);
                File expandedDocBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
                if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    expandedDocBase = new File(context.getDocBase());
                    if (!expandedDocBase.isAbsolute()) {
                        expandedDocBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                }
                boolean unpackWAR = this.unpackWARs;
                if (unpackWAR && (context instanceof StandardContext)) {
                    StandardContext standardContext = null;
                    unpackWAR = standardContext.getUnpackWAR();
                }
                if (0 == 0) {
                    if (0 == 0) {
                        File warDocBase = new File(expandedDocBase.getAbsolutePath() + ".war");
                        if (warDocBase.exists()) {
                            deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), Long.valueOf(warDocBase.lastModified()));
                        } else {
                            deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), 0L);
                        }
                    }
                    if (unpackWAR) {
                        deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), null);
                    } else {
                        addWatchedResources(deployedApp, null, null);
                    }
                    if (0 == 0) {
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    }
                } else if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                addGlobalRedeployResources(deployedApp);
            } catch (Throwable th) {
                File expandedDocBase2 = new File(this.host.getAppBaseFile(), cn.getBaseName());
                if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    expandedDocBase2 = new File(context.getDocBase());
                    if (!expandedDocBase2.isAbsolute()) {
                        expandedDocBase2 = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                }
                boolean unpackWAR2 = this.unpackWARs;
                if (unpackWAR2 && (context instanceof StandardContext)) {
                    StandardContext standardContext2 = null;
                    unpackWAR2 = standardContext2.getUnpackWAR();
                }
                if (0 == 0) {
                    if (0 == 0) {
                        File warDocBase2 = new File(expandedDocBase2.getAbsolutePath() + ".war");
                        if (warDocBase2.exists()) {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), Long.valueOf(warDocBase2.lastModified()));
                        } else {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), 0L);
                        }
                    }
                    if (unpackWAR2) {
                        deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), null);
                    } else {
                        addWatchedResources(deployedApp, null, null);
                    }
                    if (0 == 0) {
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    }
                } else if (unpackWAR2) {
                    deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                addGlobalRedeployResources(deployedApp);
                throw th;
            }
        }
        try {
            synchronized (this.digesterLock) {
                try {
                    context = (Context) this.digester.parse(fis);
                    this.digester.reset();
                    if (context == null) {
                        context = new FailedContext();
                    }
                } catch (Exception e) {
                    log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), e);
                    this.digester.reset();
                    if (context == null) {
                        context = new FailedContext();
                    }
                }
            }
            if (context.getPath() != null) {
                log.warn(sm.getString("hostConfig.deployDescriptor.path", context.getPath(), contextXml.getAbsolutePath()));
            }
            Class<?> clazz = Class.forName(this.host.getConfigClass());
            LifecycleListener listener = (LifecycleListener) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setConfigFile(contextXml.toURI().toURL());
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            if (context.getDocBase() != null) {
                File docBase = new File(context.getDocBase());
                if (!docBase.isAbsolute()) {
                    docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
                if (docBase.getCanonicalPath().startsWith(this.host.getAppBaseFile().getAbsolutePath() + File.separator)) {
                    log.warn(sm.getString("hostConfig.deployDescriptor.localDocBaseSpecified", docBase));
                    context.setDocBase(null);
                } else {
                    isExternal = true;
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
                    if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        isExternalWar = true;
                    }
                    File war = new File(this.host.getAppBaseFile(), cn.getBaseName() + ".war");
                    if (war.exists()) {
                        log.warn(sm.getString("hostConfig.deployDescriptor.hiddenWar", contextXml.getAbsolutePath(), war.getAbsolutePath()));
                    }
                    File dir = new File(this.host.getAppBaseFile(), cn.getBaseName());
                    if (dir.exists()) {
                        log.warn(sm.getString("hostConfig.deployDescriptor.hiddenDir", contextXml.getAbsolutePath(), dir.getAbsolutePath()));
                    }
                }
            }
            this.host.addChild(context);
            if (fis != null) {
                if (0 != 0) {
                    fis.close();
                } else {
                    fis.close();
                }
            }
            File expandedDocBase3 = new File(this.host.getAppBaseFile(), cn.getBaseName());
            if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                expandedDocBase3 = new File(context.getDocBase());
                if (!expandedDocBase3.isAbsolute()) {
                    expandedDocBase3 = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
            }
            boolean unpackWAR3 = this.unpackWARs;
            if (unpackWAR3 && (context instanceof StandardContext)) {
                unpackWAR3 = ((StandardContext) context).getUnpackWAR();
            }
            if (!isExternalWar) {
                if (!isExternal) {
                    File warDocBase3 = new File(expandedDocBase3.getAbsolutePath() + ".war");
                    if (warDocBase3.exists()) {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), Long.valueOf(warDocBase3.lastModified()));
                    } else {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), 0L);
                    }
                }
                if (unpackWAR3) {
                    deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), context);
                } else {
                    addWatchedResources(deployedApp, null, context);
                }
                if (!isExternal) {
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                }
            } else if (unpackWAR3) {
                deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), context);
            } else {
                addWatchedResources(deployedApp, null, context);
            }
            addGlobalRedeployResources(deployedApp);
            if (this.host.findChild(context.getName()) != null) {
                this.deployed.put(context.getName(), deployedApp);
            }
            if (log.isInfoEnabled()) {
                log.info(sm.getString("hostConfig.deployDescriptor.finished", contextXml.getAbsolutePath(), Long.valueOf(System.currentTimeMillis() - startTime)));
            }
        } catch (Throwable th2) {
            this.digester.reset();
            if (context == null) {
                new FailedContext();
            }
            throw th2;
        }
    }

    protected void deployWARs(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equalsIgnoreCase("META-INF") && !files[i].equalsIgnoreCase("WEB-INF")) {
                File war = new File(appBase, files[i]);
                if (files[i].toLowerCase(Locale.ENGLISH).endsWith(".war") && war.isFile() && !this.invalidWars.contains(files[i])) {
                    ContextName cn = new ContextName(files[i], true);
                    if (!isServiced(cn.getName())) {
                        if (deploymentExists(cn.getName())) {
                            DeployedApplication app = this.deployed.get(cn.getName());
                            boolean unpackWAR = this.unpackWARs;
                            if (unpackWAR && (this.host.findChild(cn.getName()) instanceof StandardContext)) {
                                unpackWAR = ((StandardContext) this.host.findChild(cn.getName())).getUnpackWAR();
                            }
                            if (!unpackWAR && app != null) {
                                File dir = new File(appBase, cn.getBaseName());
                                if (dir.exists()) {
                                    if (!app.loggedDirWarning) {
                                        log.warn(sm.getString("hostConfig.deployWar.hiddenDir", dir.getAbsoluteFile(), war.getAbsoluteFile()));
                                        app.loggedDirWarning = true;
                                    }
                                } else {
                                    app.loggedDirWarning = false;
                                }
                            }
                        } else if (!validateContextPath(appBase, cn.getBaseName())) {
                            log.error(sm.getString("hostConfig.illegalWarName", files[i]));
                            this.invalidWars.add(files[i]);
                        } else {
                            results.add(es.submit(new DeployWar(this, cn, war)));
                        }
                    }
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployWar.threaded.error"), e);
            }
        }
    }

    private boolean validateContextPath(File appBase, String contextPath) {
        try {
            String canonicalAppBase = appBase.getCanonicalPath();
            StringBuilder docBase = new StringBuilder(canonicalAppBase);
            if (canonicalAppBase.endsWith(File.separator)) {
                docBase.append(contextPath.substring(1).replace('/', File.separatorChar));
            } else {
                docBase.append(contextPath.replace('/', File.separatorChar));
            }
            String canonicalDocBase = new File(docBase.toString()).getCanonicalPath();
            if (canonicalDocBase.endsWith(File.separator)) {
                docBase.append(File.separator);
            }
            return canonicalDocBase.equals(docBase.toString());
        } catch (IOException e) {
            return false;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(32:1|(8:2|3|4|5|(1:7)|8|(2:10|(1:12)(1:13))|14)|15|(1:23)|24|25|(3:29|30|(23:32|11d|41|42|43|(1:45)|46|(6:48|(1:50)|51|(1:55)|56|(22:60|61|62|63|64|65|66|67|68|69|70|(2:71|(1:74)(1:73))|75|76|(2:78|(2:81|82)(1:80))|86|(2:88|(2:91|92)(1:90))|96|(2:98|(2:101|102)(1:100))|106|(2:108|(1:110)(1:111))|112))|173|(1:266)(1:179)|180|(1:182)|183|184|(1:218)(1:190)|191|192|(1:196)|197|(1:216)(2:201|(1:209))|210|211|(2:213|214)(1:215)))|293|(2:383|(1:388)(1:387))(4:297|1c4|331|332)|43|(0)|46|(0)|173|(1:175)|266|180|(0)|183|184|(1:186)|218|191|192|(2:194|196)|197|(1:199)|216|210|211|(0)(0)|(6:(0)|(1:118)|(1:155)|(1:356)|(1:338)|(1:137))) */
    /* JADX WARN: Code restructure failed: missing block: B:760:0x081a, code lost:
        r25 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:761:0x081c, code lost:
        org.apache.tomcat.util.ExceptionUtils.handleThrowable(r25);
        org.apache.catalina.startup.HostConfig.log.error(org.apache.catalina.startup.HostConfig.sm.getString("hostConfig.deployWar.error", r14.getAbsolutePath()), r25);
     */
    /* JADX WARN: Code restructure failed: missing block: B:762:0x083f, code lost:
        r25 = r12.unpackWARs;
     */
    /* JADX WARN: Code restructure failed: missing block: B:763:0x0846, code lost:
        if (r25 != false) goto L224;
     */
    /* JADX WARN: Code restructure failed: missing block: B:766:0x0851, code lost:
        r25 = ((org.apache.catalina.core.StandardContext) r19).getUnpackWAR();
     */
    /* JADX WARN: Code restructure failed: missing block: B:768:0x085d, code lost:
        if (r25 == false) goto L241;
     */
    /* JADX WARN: Code restructure failed: missing block: B:771:0x086a, code lost:
        r0 = new java.io.File(r12.host.getAppBaseFile(), r13.getBaseName());
        r0.redeployResources.put(r0.getAbsolutePath(), java.lang.Long.valueOf(r0.lastModified()));
        addWatchedResources(r0, r0.getAbsolutePath(), r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:772:0x08a5, code lost:
        if (r0 != false) goto L233;
     */
    /* JADX WARN: Code restructure failed: missing block: B:779:0x08b9, code lost:
        r0.redeployResources.put(r15.getAbsolutePath(), java.lang.Long.valueOf(r15.lastModified()));
     */
    /* JADX WARN: Code restructure failed: missing block: B:781:0x08d0, code lost:
        addWatchedResources(r0, null, r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:782:0x08d9, code lost:
        addGlobalRedeployResources(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:783:0x08e2, code lost:
        r43 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:785:0x08e5, code lost:
        r44 = r12.unpackWARs;
     */
    /* JADX WARN: Code restructure failed: missing block: B:786:0x08ec, code lost:
        if (r44 != false) goto L247;
     */
    /* JADX WARN: Code restructure failed: missing block: B:789:0x08f7, code lost:
        r44 = ((org.apache.catalina.core.StandardContext) r19).getUnpackWAR();
     */
    /* JADX WARN: Code restructure failed: missing block: B:791:0x0903, code lost:
        if (r44 == false) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:794:0x0910, code lost:
        r0 = new java.io.File(r12.host.getAppBaseFile(), r13.getBaseName());
        r0.redeployResources.put(r0.getAbsolutePath(), java.lang.Long.valueOf(r0.lastModified()));
        addWatchedResources(r0, r0.getAbsolutePath(), r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:795:0x094b, code lost:
        if (r0 != false) goto L256;
     */
    /* JADX WARN: Code restructure failed: missing block: B:802:0x095f, code lost:
        r0.redeployResources.put(r15.getAbsolutePath(), java.lang.Long.valueOf(r15.lastModified()));
     */
    /* JADX WARN: Code restructure failed: missing block: B:804:0x0976, code lost:
        addWatchedResources(r0, null, r19);
     */
    /* JADX WARN: Code restructure failed: missing block: B:805:0x097f, code lost:
        addGlobalRedeployResources(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:806:0x0987, code lost:
        throw r43;
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:594:0x03b9  */
    /* JADX WARN: Removed duplicated region for block: B:607:0x0415  */
    /* JADX WARN: Removed duplicated region for block: B:729:0x066f  */
    /* JADX WARN: Removed duplicated region for block: B:809:0x09a3  */
    /* JADX WARN: Removed duplicated region for block: B:865:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void deployWAR(org.apache.catalina.util.ContextName r13, java.io.File r14) {
        /*
            Method dump skipped, instructions count: 2507
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployWAR(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected void deployDirectories(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equalsIgnoreCase("META-INF") && !files[i].equalsIgnoreCase("WEB-INF")) {
                File dir = new File(appBase, files[i]);
                if (dir.isDirectory()) {
                    ContextName cn = new ContextName(files[i], false);
                    if (!isServiced(cn.getName()) && !deploymentExists(cn.getName())) {
                        results.add(es.submit(new DeployDirectory(this, cn, dir)));
                    }
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDir.threaded.error"), e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:198:0x02ea  */
    /* JADX WARN: Removed duplicated region for block: B:247:0x0536  */
    /* JADX WARN: Removed duplicated region for block: B:255:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void deployDirectory(org.apache.catalina.util.ContextName r12, java.io.File r13) {
        /*
            Method dump skipped, instructions count: 1373
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployDirectory(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected boolean deploymentExists(String contextName) {
        return this.deployed.containsKey(contextName) || this.host.findChild(contextName) != null;
    }

    protected void addWatchedResources(DeployedApplication app, String docBase, Context context) {
        File docBaseFile = null;
        if (docBase != null) {
            docBaseFile = new File(docBase);
            if (!docBaseFile.isAbsolute()) {
                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
            }
        }
        String[] watchedResources = context.findWatchedResources();
        for (int i = 0; i < watchedResources.length; i++) {
            File resource = new File(watchedResources[i]);
            if (!resource.isAbsolute()) {
                if (docBase != null) {
                    resource = new File(docBaseFile, watchedResources[i]);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring non-existent WatchedResource '" + resource.getAbsolutePath() + "'");
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Watching WatchedResource '" + resource.getAbsolutePath() + "'");
            }
            app.reloadResources.put(resource.getAbsolutePath(), Long.valueOf(resource.lastModified()));
        }
    }

    protected void addGlobalRedeployResources(DeployedApplication app) {
        File hostContextXml = new File(getConfigBaseName(), Constants.HostContextXml);
        if (hostContextXml.isFile()) {
            app.redeployResources.put(hostContextXml.getAbsolutePath(), Long.valueOf(hostContextXml.lastModified()));
        }
        File globalContextXml = returnCanonicalPath(Constants.DefaultContextXml);
        if (globalContextXml.isFile()) {
            app.redeployResources.put(globalContextXml.getAbsolutePath(), Long.valueOf(globalContextXml.lastModified()));
        }
    }

    protected synchronized void checkResources(DeployedApplication app, boolean skipFileModificationResolutionCheck) {
        String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
        long currentTimeWithResolutionOffset = System.currentTimeMillis() - 1000;
        for (int i = 0; i < resources.length; i++) {
            File resource = new File(resources[i]);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] redeploy resource " + resource);
            }
            long lastModified = app.redeployResources.get(resources[i]).longValue();
            if (resource.exists() || lastModified == 0) {
                if (resource.lastModified() != lastModified && (!this.host.getAutoDeploy() || resource.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) {
                    if (resource.isDirectory()) {
                        app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                    } else if (app.hasDescriptor && resource.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        Context context = (Context) this.host.findChild(app.name);
                        String docBase = context.getDocBase();
                        if (!docBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                            File docBaseFile = new File(docBase);
                            if (!docBaseFile.isAbsolute()) {
                                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
                            }
                            reload(app, docBaseFile, resource.getAbsolutePath());
                        } else {
                            reload(app, null, null);
                        }
                        app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                        app.timestamp = System.currentTimeMillis();
                        boolean unpackWAR = this.unpackWARs;
                        if (unpackWAR && (context instanceof StandardContext)) {
                            unpackWAR = ((StandardContext) context).getUnpackWAR();
                        }
                        if (unpackWAR) {
                            addWatchedResources(app, context.getDocBase(), context);
                            return;
                        } else {
                            addWatchedResources(app, null, context);
                            return;
                        }
                    } else {
                        undeploy(app);
                        deleteRedeployResources(app, resources, i, false);
                        return;
                    }
                }
            } else {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                if (!resource.exists()) {
                    undeploy(app);
                    deleteRedeployResources(app, resources, i, true);
                    return;
                }
            }
        }
        String[] resources2 = (String[]) app.reloadResources.keySet().toArray(new String[0]);
        boolean update = false;
        for (int i2 = 0; i2 < resources2.length; i2++) {
            File resource2 = new File(resources2[i2]);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] reload resource " + resource2);
            }
            if ((resource2.lastModified() != app.reloadResources.get(resources2[i2]).longValue() && (!this.host.getAutoDeploy() || resource2.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) || update) {
                if (!update) {
                    reload(app, null, null);
                    update = true;
                }
                app.reloadResources.put(resources2[i2], Long.valueOf(resource2.lastModified()));
            }
            app.timestamp = System.currentTimeMillis();
        }
    }

    private void reload(DeployedApplication app, File fileToRemove, String newDocBase) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.reload", app.name));
        }
        Context context = (Context) this.host.findChild(app.name);
        if (context.getState().isAvailable()) {
            if (fileToRemove != null && newDocBase != null) {
                context.addLifecycleListener(new ExpandedDirectoryRemovalListener(fileToRemove, newDocBase));
            }
            context.reload();
            return;
        }
        if (fileToRemove != null && newDocBase != null) {
            ExpandWar.delete(fileToRemove);
            context.setDocBase(newDocBase);
        }
        try {
            context.start();
        } catch (Exception e) {
            log.warn(sm.getString("hostConfig.context.restart", app.name), e);
        }
    }

    private void undeploy(DeployedApplication app) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.undeploy", app.name));
        }
        Container context = this.host.findChild(app.name);
        try {
            this.host.removeChild(context);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("hostConfig.context.remove", app.name), t);
        }
        this.deployed.remove(app.name);
    }

    private void deleteRedeployResources(DeployedApplication app, String[] resources, int i, boolean deleteReloadResources) {
        for (int j = i + 1; j < resources.length; j++) {
            File current = new File(resources[j]);
            if (!Constants.HostContextXml.equals(current.getName()) && isDeletableResource(app, current)) {
                if (log.isDebugEnabled()) {
                    log.debug("Delete " + current);
                }
                ExpandWar.delete(current);
            }
        }
        if (deleteReloadResources) {
            String[] resources2 = (String[]) app.reloadResources.keySet().toArray(new String[0]);
            for (String str : resources2) {
                File current2 = new File(str);
                if (!Constants.HostContextXml.equals(current2.getName()) && isDeletableResource(app, current2)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Delete " + current2);
                    }
                    ExpandWar.delete(current2);
                }
            }
        }
    }

    private boolean isDeletableResource(DeployedApplication app, File resource) {
        if (!resource.isAbsolute()) {
            log.warn(sm.getString("hostConfig.resourceNotAbsolute", app.name, resource));
            return false;
        }
        try {
            String canonicalLocation = resource.getParentFile().getCanonicalPath();
            try {
                String canonicalAppBase = this.host.getAppBaseFile().getCanonicalPath();
                if (canonicalLocation.equals(canonicalAppBase)) {
                    return true;
                }
                try {
                    String canonicalConfigBase = this.host.getConfigBaseFile().getCanonicalPath();
                    if (canonicalLocation.equals(canonicalConfigBase) && resource.getName().endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                        return true;
                    }
                    return false;
                } catch (IOException e) {
                    log.warn(sm.getString("hostConfig.canonicalizing", this.host.getConfigBaseFile(), app.name), e);
                    return false;
                }
            } catch (IOException e2) {
                log.warn(sm.getString("hostConfig.canonicalizing", this.host.getAppBaseFile(), app.name), e2);
                return false;
            }
        } catch (IOException e3) {
            log.warn(sm.getString("hostConfig.canonicalizing", resource.getParentFile(), app.name), e3);
            return false;
        }
    }

    public void beforeStart() {
        if (this.host.getCreateDirs()) {
            File[] dirs = {this.host.getAppBaseFile(), this.host.getConfigBaseFile()};
            for (int i = 0; i < dirs.length; i++) {
                if (!dirs[i].mkdirs() && !dirs[i].isDirectory()) {
                    log.error(sm.getString("hostConfig.createDirs", dirs[i]));
                }
            }
        }
    }

    public void start() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.start"));
        }
        try {
            ObjectName hostON = this.host.getObjectName();
            this.oname = new ObjectName(hostON.getDomain() + ":type=Deployer,host=" + this.host.getName());
            Registry.getRegistry(null, null).registerComponent(this, this.oname, getClass().getName());
        } catch (Exception e) {
            log.error(sm.getString("hostConfig.jmx.register", this.oname), e);
        }
        if (!this.host.getAppBaseFile().isDirectory()) {
            log.error(sm.getString("hostConfig.appBase", this.host.getName(), this.host.getAppBaseFile().getPath()));
            this.host.setDeployOnStartup(false);
            this.host.setAutoDeploy(false);
        }
        if (this.host.getDeployOnStartup()) {
            deployApps();
        }
    }

    public void stop() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.stop"));
        }
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.jmx.unregister", this.oname), e);
            }
        }
        this.oname = null;
    }

    protected void check() {
        if (this.host.getAutoDeploy()) {
            DeployedApplication[] apps = (DeployedApplication[]) this.deployed.values().toArray(new DeployedApplication[0]);
            for (int i = 0; i < apps.length; i++) {
                if (!isServiced(apps[i].name)) {
                    checkResources(apps[i], false);
                }
            }
            if (this.host.getUndeployOldVersions()) {
                checkUndeploy();
            }
            deployApps();
        }
    }

    public void check(String name) {
        DeployedApplication app = this.deployed.get(name);
        if (app != null) {
            checkResources(app, true);
        }
        deployApps(name);
    }

    public synchronized void checkUndeploy() {
        Manager manager;
        int sessionCount;
        if (this.deployed.size() < 2) {
            return;
        }
        SortedSet<String> sortedAppNames = new TreeSet<>();
        sortedAppNames.addAll(this.deployed.keySet());
        Iterator<String> iter = sortedAppNames.iterator();
        ContextName previous = new ContextName(iter.next(), false);
        do {
            ContextName current = new ContextName(iter.next(), false);
            if (current.getPath().equals(previous.getPath())) {
                Context previousContext = (Context) this.host.findChild(previous.getName());
                Context currentContext = (Context) this.host.findChild(current.getName());
                if (previousContext != null && currentContext != null && currentContext.getState().isAvailable() && !isServiced(previous.getName()) && (manager = previousContext.getManager()) != null) {
                    if (manager instanceof DistributedManager) {
                        sessionCount = ((DistributedManager) manager).getActiveSessionsFull();
                    } else {
                        sessionCount = manager.getActiveSessions();
                    }
                    if (sessionCount == 0) {
                        if (log.isInfoEnabled()) {
                            log.info(sm.getString("hostConfig.undeployVersion", previous.getName()));
                        }
                        DeployedApplication app = this.deployed.get(previous.getName());
                        String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
                        undeploy(app);
                        deleteRedeployResources(app, resources, -1, true);
                    }
                }
            }
            previous = current;
        } while (iter.hasNext());
    }

    public void manageApp(Context context) {
        String contextName = context.getName();
        if (this.deployed.containsKey(contextName)) {
            return;
        }
        DeployedApplication deployedApp = new DeployedApplication(contextName, false);
        boolean isWar = false;
        if (context.getDocBase() != null) {
            File docBase = new File(context.getDocBase());
            if (!docBase.isAbsolute()) {
                docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                isWar = true;
            }
        }
        this.host.addChild(context);
        boolean unpackWAR = this.unpackWARs;
        if (unpackWAR && (context instanceof StandardContext)) {
            unpackWAR = ((StandardContext) context).getUnpackWAR();
        }
        if (isWar && unpackWAR) {
            File docBase2 = new File(this.host.getAppBaseFile(), context.getBaseName());
            deployedApp.redeployResources.put(docBase2.getAbsolutePath(), Long.valueOf(docBase2.lastModified()));
            addWatchedResources(deployedApp, docBase2.getAbsolutePath(), context);
        } else {
            addWatchedResources(deployedApp, null, context);
        }
        this.deployed.put(contextName, deployedApp);
    }

    public void unmanageApp(String contextName) {
        if (isServiced(contextName)) {
            this.deployed.remove(contextName);
            this.host.removeChild(this.host.findChild(contextName));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig$DeployedApplication.class */
    public static class DeployedApplication {
        public final String name;
        public final boolean hasDescriptor;
        public final LinkedHashMap<String, Long> redeployResources = new LinkedHashMap<>();
        public final HashMap<String, Long> reloadResources = new HashMap<>();
        public long timestamp = System.currentTimeMillis();
        public boolean loggedDirWarning = false;

        public DeployedApplication(String name, boolean hasDescriptor) {
            this.name = name;
            this.hasDescriptor = hasDescriptor;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig$DeployDescriptor.class */
    public static class DeployDescriptor implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File descriptor;

        public DeployDescriptor(HostConfig config, ContextName cn, File descriptor) {
            this.config = config;
            this.cn = cn;
            this.descriptor = descriptor;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.config.deployDescriptor(this.cn, this.descriptor);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig$DeployWar.class */
    public static class DeployWar implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File war;

        public DeployWar(HostConfig config, ContextName cn, File war) {
            this.config = config;
            this.cn = cn;
            this.war = war;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.config.deployWAR(this.cn, this.war);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig$DeployDirectory.class */
    public static class DeployDirectory implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File dir;

        public DeployDirectory(HostConfig config, ContextName cn, File dir) {
            this.config = config;
            this.cn = cn;
            this.dir = dir;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.config.deployDirectory(this.cn, this.dir);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/HostConfig$ExpandedDirectoryRemovalListener.class */
    public static class ExpandedDirectoryRemovalListener implements LifecycleListener {
        private final File toDelete;
        private final String newDocBase;

        public ExpandedDirectoryRemovalListener(File toDelete, String newDocBase) {
            this.toDelete = toDelete;
            this.newDocBase = newDocBase;
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
            if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
                Context context = (Context) event.getLifecycle();
                ExpandWar.delete(this.toDelete);
                context.setDocBase(this.newDocBase);
                context.removeLifecycleListener(this);
            }
        }
    }
}