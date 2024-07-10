package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.security.SecurityConfig;
import org.apache.juli.ClassLoaderLogManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Time;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/Catalina.class */
public class Catalina {
    protected boolean await = false;
    protected String configFile = "conf/server.xml";
    protected ClassLoader parentClassLoader = Catalina.class.getClassLoader();
    protected Server server = null;
    protected boolean useShutdownHook = true;
    protected Thread shutdownHook = null;
    protected boolean useNaming = true;
    protected boolean loaded = false;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final Log log = LogFactory.getLog(Catalina.class);

    public Catalina() {
        setSecurityProtection();
        ExceptionUtils.preload();
    }

    public void setConfigFile(String file) {
        this.configFile = file;
    }

    public String getConfigFile() {
        return this.configFile;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    public boolean getUseShutdownHook() {
        return this.useShutdownHook;
    }

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public boolean isUseNaming() {
        return this.useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }

    public void setAwait(boolean b) {
        this.await = b;
    }

    public boolean isAwait() {
        return this.await;
    }

    protected boolean arguments(String[] args) {
        boolean isConfig = false;
        if (args.length < 1) {
            usage();
            return false;
        }
        for (int i = 0; i < args.length; i++) {
            if (isConfig) {
                this.configFile = args[i];
                isConfig = false;
            } else if (args[i].equals("-config")) {
                isConfig = true;
            } else if (args[i].equals("-nonaming")) {
                setUseNaming(false);
            } else if (args[i].equals("-help")) {
                usage();
                return false;
            } else if (!args[i].equals(Lifecycle.START_EVENT) && !args[i].equals("configtest") && !args[i].equals(Lifecycle.STOP_EVENT)) {
                usage();
                return false;
            }
        }
        return true;
    }

    protected File configFile() {
        File file = new File(this.configFile);
        if (!file.isAbsolute()) {
            file = new File(Bootstrap.getCatalinaBase(), this.configFile);
        }
        return file;
    }

    protected Digester createStartDigester() {
        long t1 = System.currentTimeMillis();
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        Map<Class<?>, List<String>> fakeAttributes = new HashMap<>();
        List<String> attrs = new ArrayList<>();
        attrs.add("className");
        fakeAttributes.put(Object.class, attrs);
        digester.setFakeAttributes(fakeAttributes);
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate(HttpHeaders.SERVER, "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties(HttpHeaders.SERVER);
        digester.addSetNext(HttpHeaders.SERVER, "setServer", "org.apache.catalina.Server");
        digester.addObjectCreate("Server/GlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addSetProperties("Server/GlobalNamingResources");
        digester.addSetNext("Server/GlobalNamingResources", "setGlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addObjectCreate("Server/Listener", null, "className");
        digester.addSetProperties("Server/Listener");
        digester.addSetNext("Server/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service", "org.apache.catalina.core.StandardService", "className");
        digester.addSetProperties("Server/Service");
        digester.addSetNext("Server/Service", "addService", "org.apache.catalina.Service");
        digester.addObjectCreate("Server/Service/Listener", null, "className");
        digester.addSetProperties("Server/Service/Listener");
        digester.addSetNext("Server/Service/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Executor", "org.apache.catalina.core.StandardThreadExecutor", "className");
        digester.addSetProperties("Server/Service/Executor");
        digester.addSetNext("Server/Service/Executor", "addExecutor", "org.apache.catalina.Executor");
        digester.addRule("Server/Service/Connector", new ConnectorCreateRule());
        digester.addRule("Server/Service/Connector", new SetAllPropertiesRule(new String[]{"executor", "sslImplementationName", "protocol"}));
        digester.addSetNext("Server/Service/Connector", "addConnector", "org.apache.catalina.connector.Connector");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig", "addSslHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", new CertificateCreateRule());
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", new SetAllPropertiesRule(new String[]{"type"}));
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/Certificate", "addCertificate", "org.apache.tomcat.util.net.SSLHostConfigCertificate");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "setOpenSslConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "addCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addObjectCreate("Server/Service/Connector/Listener", null, "className");
        digester.addSetProperties("Server/Service/Connector/Listener");
        digester.addSetNext("Server/Service/Connector/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Connector/UpgradeProtocol", null, "className");
        digester.addSetProperties("Server/Service/Connector/UpgradeProtocol");
        digester.addSetNext("Server/Service/Connector/UpgradeProtocol", "addUpgradeProtocol", "org.apache.coyote.UpgradeProtocol");
        digester.addRuleSet(new NamingRuleSet("Server/GlobalNamingResources/"));
        digester.addRuleSet(new EngineRuleSet("Server/Service/"));
        digester.addRuleSet(new HostRuleSet("Server/Service/Engine/"));
        digester.addRuleSet(new ContextRuleSet("Server/Service/Engine/Host/"));
        addClusterRuleSet(digester, "Server/Service/Engine/Host/Cluster/");
        digester.addRuleSet(new NamingRuleSet("Server/Service/Engine/Host/Context/"));
        digester.addRule("Server/Service/Engine", new SetParentClassLoaderRule(this.parentClassLoader));
        addClusterRuleSet(digester, "Server/Service/Engine/Cluster/");
        long t2 = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("Digester for server.xml created " + (t2 - t1));
        }
        return digester;
    }

    private void addClusterRuleSet(Digester digester, String prefix) {
        try {
            Class<?> clazz = Class.forName("org.apache.catalina.ha.ClusterRuleSet");
            Constructor<?> constructor = clazz.getConstructor(String.class);
            RuleSet ruleSet = (RuleSet) constructor.newInstance(prefix);
            digester.addRuleSet(ruleSet);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("catalina.noCluster", e.getClass().getName() + ": " + e.getMessage()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("catalina.noCluster", e.getClass().getName() + ": " + e.getMessage()));
            }
        }
    }

    protected Digester createStopDigester() {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate(HttpHeaders.SERVER, "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties(HttpHeaders.SERVER);
        digester.addSetNext(HttpHeaders.SERVER, "setServer", "org.apache.catalina.Server");
        return digester;
    }

    public void stopServer() {
        stopServer(null);
    }

    public void stopServer(String[] arguments) {
        if (arguments != null) {
            arguments(arguments);
        }
        Server s = getServer();
        if (s != null) {
            try {
                s.stop();
                return;
            } catch (LifecycleException e) {
                log.error("Catalina.stop: ", e);
                return;
            }
        }
        Digester digester = createStopDigester();
        File file = configFile();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputSource is = new InputSource(file.toURI().toURL().toString());
            is.setByteStream(fis);
            digester.push(this);
            digester.parse(is);
            if (fis != null) {
                if (0 != 0) {
                    fis.close();
                } else {
                    fis.close();
                }
            }
        } catch (Exception e2) {
            log.error("Catalina.stop: ", e2);
            System.exit(1);
        }
        Server s2 = getServer();
        if (s2.getPort() <= 0) {
            log.error(sm.getString("catalina.stopServer"));
            System.exit(1);
            return;
        }
        try {
            Socket socket = new Socket(s2.getAddress(), s2.getPort());
            Throwable th = null;
            try {
                OutputStream stream = socket.getOutputStream();
                Throwable th2 = null;
                try {
                    String shutdown = s2.getShutdown();
                    for (int i = 0; i < shutdown.length(); i++) {
                        stream.write(shutdown.charAt(i));
                    }
                    stream.flush();
                    if (stream != null) {
                        if (0 != 0) {
                            try {
                                stream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            stream.close();
                        }
                    }
                    if (socket != null) {
                        if (0 != 0) {
                            try {
                                socket.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            socket.close();
                        }
                    }
                } catch (Throwable th5) {
                    try {
                        throw th5;
                    } catch (Throwable th6) {
                        if (stream != null) {
                            if (th5 != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th7) {
                                    th5.addSuppressed(th7);
                                }
                            } else {
                                stream.close();
                            }
                        }
                        throw th6;
                    }
                }
            } catch (Throwable th8) {
                try {
                    throw th8;
                } catch (Throwable th9) {
                    if (socket != null) {
                        if (th8 != null) {
                            try {
                                socket.close();
                            } catch (Throwable th10) {
                                th8.addSuppressed(th10);
                            }
                        } else {
                            socket.close();
                        }
                    }
                    throw th9;
                }
            }
        } catch (ConnectException ce) {
            log.error(sm.getString("catalina.stopServer.connectException", s2.getAddress(), String.valueOf(s2.getPort())));
            log.error("Catalina.stop: ", ce);
            System.exit(1);
        } catch (IOException e3) {
            log.error("Catalina.stop: ", e3);
            System.exit(1);
        }
    }

    public void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        long t1 = System.nanoTime();
        initDirs();
        initNaming();
        Digester digester = createStartDigester();
        InputSource inputSource = null;
        InputStream inputStream = null;
        File file = null;
        try {
            try {
                file = configFile();
                inputStream = new FileInputStream(file);
                inputSource = new InputSource(file.toURI().toURL().toString());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        } catch (Exception e2) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("catalina.configFail", file), e2);
            }
        }
        if (inputStream == null) {
            try {
                inputStream = getClass().getClassLoader().getResourceAsStream(getConfigFile());
                inputSource = new InputSource(getClass().getClassLoader().getResource(getConfigFile()).toString());
            } catch (Exception e3) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("catalina.configFail", getConfigFile()), e3);
                }
            }
        }
        if (inputStream == null) {
            try {
                inputStream = getClass().getClassLoader().getResourceAsStream("server-embed.xml");
                inputSource = new InputSource(getClass().getClassLoader().getResource("server-embed.xml").toString());
            } catch (Exception e4) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("catalina.configFail", "server-embed.xml"), e4);
                }
            }
        }
        if (inputStream == null || inputSource == null) {
            if (file == null) {
                log.warn(sm.getString("catalina.configFail", getConfigFile() + "] or [server-embed.xml]"));
            } else {
                log.warn(sm.getString("catalina.configFail", file.getAbsolutePath()));
                if (file.exists() && !file.canRead()) {
                    log.warn("Permissions incorrect, read permission is not allowed on the file.");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                    return;
                } catch (IOException e5) {
                    return;
                }
            }
            return;
        }
        try {
            try {
                inputSource.setByteStream(inputStream);
                digester.push(this);
                digester.parse(inputSource);
                getServer().setCatalina(this);
                getServer().setCatalinaHome(Bootstrap.getCatalinaHomeFile());
                getServer().setCatalinaBase(Bootstrap.getCatalinaBaseFile());
                initStreams();
                try {
                    getServer().init();
                } catch (LifecycleException e6) {
                    if (Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE")) {
                        throw new Error(e6);
                    }
                    log.error("Catalina.start", e6);
                }
                long t2 = System.nanoTime();
                if (log.isInfoEnabled()) {
                    log.info("Initialization processed in " + ((t2 - t1) / Time.APR_USEC_PER_SEC) + " ms");
                }
            } catch (Exception e7) {
                log.warn("Catalina.start using " + getConfigFile() + ": ", e7);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e8) {
                    }
                }
            }
        } catch (SAXParseException spe) {
            log.warn("Catalina.start using " + getConfigFile() + ": " + spe.getMessage());
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e9) {
                }
            }
        }
    }

    public void load(String[] args) {
        try {
            if (arguments(args)) {
                load();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void start() {
        if (getServer() == null) {
            load();
        }
        if (getServer() == null) {
            log.fatal("Cannot start server. Server instance is not configured.");
            return;
        }
        long t1 = System.nanoTime();
        try {
            getServer().start();
            long t2 = System.nanoTime();
            if (log.isInfoEnabled()) {
                log.info("Server startup in " + ((t2 - t1) / Time.APR_USEC_PER_SEC) + " ms");
            }
            if (this.useShutdownHook) {
                if (this.shutdownHook == null) {
                    this.shutdownHook = new CatalinaShutdownHook();
                }
                Runtime.getRuntime().addShutdownHook(this.shutdownHook);
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager).setUseShutdownHook(false);
                }
            }
            if (this.await) {
                await();
                stop();
            }
        } catch (LifecycleException e) {
            log.fatal(sm.getString("catalina.serverStartFail"), e);
            try {
                getServer().destroy();
            } catch (LifecycleException e1) {
                log.debug("destroy() failed for failed Server ", e1);
            }
        }
    }

    public void stop() {
        try {
            if (this.useShutdownHook) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager).setUseShutdownHook(true);
                }
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        try {
            Server s = getServer();
            LifecycleState state = s.getState();
            if (LifecycleState.STOPPING_PREP.compareTo(state) > 0 || LifecycleState.DESTROYED.compareTo(state) < 0) {
                s.stop();
                s.destroy();
            }
        } catch (LifecycleException e) {
            log.error("Catalina.stop", e);
        }
    }

    public void await() {
        getServer().await();
    }

    protected void usage() {
        System.out.println("usage: java org.apache.catalina.startup.Catalina [ -config {pathname} ] [ -nonaming ]  { -help | start | stop }");
    }

    protected void initDirs() {
        String temp = System.getProperty("java.io.tmpdir");
        if (temp == null || !new File(temp).isDirectory()) {
            log.error(sm.getString("embedded.notmp", temp));
        }
    }

    protected void initStreams() {
        System.setOut(new SystemLogHandler(System.out));
        System.setErr(new SystemLogHandler(System.err));
    }

    protected void initNaming() {
        if (!this.useNaming) {
            log.info("Catalina naming disabled");
            System.setProperty("catalina.useNaming", "false");
            return;
        }
        System.setProperty("catalina.useNaming", "true");
        String value = "org.apache.naming";
        String oldValue = System.getProperty("java.naming.factory.url.pkgs");
        if (oldValue != null) {
            value = value + ":" + oldValue;
        }
        System.setProperty("java.naming.factory.url.pkgs", value);
        if (log.isDebugEnabled()) {
            log.debug("Setting naming prefix=" + value);
        }
        String value2 = System.getProperty("java.naming.factory.initial");
        if (value2 == null) {
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        } else {
            log.debug("INITIAL_CONTEXT_FACTORY already set " + value2);
        }
    }

    protected void setSecurityProtection() {
        SecurityConfig securityConfig = SecurityConfig.newInstance();
        securityConfig.setPackageDefinition();
        securityConfig.setPackageAccess();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/Catalina$CatalinaShutdownHook.class */
    protected class CatalinaShutdownHook extends Thread {
        protected CatalinaShutdownHook() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (Catalina.this.getServer() != null) {
                    Catalina.this.stop();
                }
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager).shutdown();
                }
            } catch (Throwable ex) {
                try {
                    ExceptionUtils.handleThrowable(ex);
                    Catalina.log.error(Catalina.sm.getString("catalina.shutdownHookFail"), ex);
                    LogManager logManager2 = LogManager.getLogManager();
                    if (logManager2 instanceof ClassLoaderLogManager) {
                        ((ClassLoaderLogManager) logManager2).shutdown();
                    }
                } catch (Throwable th) {
                    LogManager logManager3 = LogManager.getLogManager();
                    if (logManager3 instanceof ClassLoaderLogManager) {
                        ((ClassLoaderLogManager) logManager3).shutdown();
                    }
                    throw th;
                }
            }
        }
    }
}