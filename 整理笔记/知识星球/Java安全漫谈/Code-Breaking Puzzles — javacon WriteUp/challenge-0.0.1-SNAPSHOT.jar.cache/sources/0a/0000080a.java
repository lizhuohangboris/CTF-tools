package org.apache.catalina.core;

import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Security;
import java.sql.DriverManager;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.startup.SafeForkJoinWorkerThreadFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.compat.JreVendor;
import org.apache.tomcat.util.res.StringManager;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/JreMemoryLeakPreventionListener.class */
public class JreMemoryLeakPreventionListener implements LifecycleListener {
    private static final Log log = LogFactory.getLog(JreMemoryLeakPreventionListener.class);
    private static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final String FORK_JOIN_POOL_THREAD_FACTORY_PROPERTY = "java.util.concurrent.ForkJoinPool.common.threadFactory";
    private boolean awtThreadProtection = false;
    private boolean gcDaemonProtection = true;
    private boolean tokenPollerProtection = true;
    private boolean urlCacheProtection = true;
    private boolean xmlParsingProtection = true;
    private boolean ldapPoolProtection = true;
    private boolean driverManagerProtection = true;
    private boolean forkJoinCommonPoolProtection = true;
    private String classesToInitialize = null;

    public boolean isAWTThreadProtection() {
        return this.awtThreadProtection;
    }

    public void setAWTThreadProtection(boolean awtThreadProtection) {
        this.awtThreadProtection = awtThreadProtection;
    }

    public boolean isGcDaemonProtection() {
        return this.gcDaemonProtection;
    }

    public void setGcDaemonProtection(boolean gcDaemonProtection) {
        this.gcDaemonProtection = gcDaemonProtection;
    }

    public boolean isTokenPollerProtection() {
        return this.tokenPollerProtection;
    }

    public void setTokenPollerProtection(boolean tokenPollerProtection) {
        this.tokenPollerProtection = tokenPollerProtection;
    }

    public boolean isUrlCacheProtection() {
        return this.urlCacheProtection;
    }

    public void setUrlCacheProtection(boolean urlCacheProtection) {
        this.urlCacheProtection = urlCacheProtection;
    }

    public boolean isXmlParsingProtection() {
        return this.xmlParsingProtection;
    }

    public void setXmlParsingProtection(boolean xmlParsingProtection) {
        this.xmlParsingProtection = xmlParsingProtection;
    }

    public boolean isLdapPoolProtection() {
        return this.ldapPoolProtection;
    }

    public void setLdapPoolProtection(boolean ldapPoolProtection) {
        this.ldapPoolProtection = ldapPoolProtection;
    }

    public boolean isDriverManagerProtection() {
        return this.driverManagerProtection;
    }

    public void setDriverManagerProtection(boolean driverManagerProtection) {
        this.driverManagerProtection = driverManagerProtection;
    }

    public boolean getForkJoinCommonPoolProtection() {
        return this.forkJoinCommonPoolProtection;
    }

    public void setForkJoinCommonPoolProtection(boolean forkJoinCommonPoolProtection) {
        this.forkJoinCommonPoolProtection = forkJoinCommonPoolProtection;
    }

    public String getClassesToInitialize() {
        return this.classesToInitialize;
    }

    public void setClassesToInitialize(String classesToInitialize) {
        this.classesToInitialize = classesToInitialize;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
            if (this.driverManagerProtection) {
                DriverManager.getDrivers();
            }
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                if (this.awtThreadProtection && !JreCompat.isJre9Available()) {
                    Toolkit.getDefaultToolkit();
                }
                if (this.gcDaemonProtection && !JreCompat.isJre9Available()) {
                    try {
                        try {
                            Class<?> clazz = Class.forName("sun.misc.GC");
                            Method method = clazz.getDeclaredMethod("requestLatency", Long.TYPE);
                            method.invoke(null, 9223372036854775806L);
                        } catch (ClassNotFoundException e) {
                            if (JreVendor.IS_ORACLE_JVM) {
                                log.error(sm.getString("jreLeakListener.gcDaemonFail"), e);
                            } else {
                                log.debug(sm.getString("jreLeakListener.gcDaemonFail"), e);
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e2) {
                            log.error(sm.getString("jreLeakListener.gcDaemonFail"), e2);
                        }
                    } catch (InvocationTargetException e3) {
                        ExceptionUtils.handleThrowable(e3.getCause());
                        log.error(sm.getString("jreLeakListener.gcDaemonFail"), e3);
                    }
                }
                if (this.tokenPollerProtection && !JreCompat.isJre9Available()) {
                    Security.getProviders();
                }
                if (this.urlCacheProtection) {
                    try {
                        JreCompat.getInstance().disableCachingForJarUrlConnections();
                    } catch (IOException e4) {
                        log.error(sm.getString("jreLeakListener.jarUrlConnCacheFail"), e4);
                    }
                }
                if (this.xmlParsingProtection && !JreCompat.isJre9Available()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    try {
                        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                        Document document = documentBuilder.newDocument();
                        document.createElement("dummy");
                        DOMImplementationLS implementation = (DOMImplementationLS) document.getImplementation();
                        implementation.createLSSerializer().writeToString(document);
                        document.normalize();
                    } catch (ParserConfigurationException e5) {
                        log.error(sm.getString("jreLeakListener.xmlParseFail"), e5);
                    }
                }
                if (this.ldapPoolProtection && !JreCompat.isJre9Available()) {
                    try {
                        Class.forName("com.sun.jndi.ldap.LdapPoolManager");
                    } catch (ClassNotFoundException e6) {
                        if (JreVendor.IS_ORACLE_JVM) {
                            log.error(sm.getString("jreLeakListener.ldapPoolManagerFail"), e6);
                        } else {
                            log.debug(sm.getString("jreLeakListener.ldapPoolManagerFail"), e6);
                        }
                    }
                }
                if (this.forkJoinCommonPoolProtection && !JreCompat.isJre9Available() && System.getProperty(FORK_JOIN_POOL_THREAD_FACTORY_PROPERTY) == null) {
                    System.setProperty(FORK_JOIN_POOL_THREAD_FACTORY_PROPERTY, SafeForkJoinWorkerThreadFactory.class.getName());
                }
                if (this.classesToInitialize != null) {
                    StringTokenizer strTok = new StringTokenizer(this.classesToInitialize, ", \r\n\t");
                    while (strTok.hasMoreTokens()) {
                        String classNameToLoad = strTok.nextToken();
                        try {
                            Class.forName(classNameToLoad);
                        } catch (ClassNotFoundException e7) {
                            log.error(sm.getString("jreLeakListener.classToInitializeFail", classNameToLoad), e7);
                        }
                    }
                }
                Thread.currentThread().setContextClassLoader(loader);
            } catch (Throwable th) {
                Thread.currentThread().setContextClassLoader(loader);
                throw th;
            }
        }
    }
}