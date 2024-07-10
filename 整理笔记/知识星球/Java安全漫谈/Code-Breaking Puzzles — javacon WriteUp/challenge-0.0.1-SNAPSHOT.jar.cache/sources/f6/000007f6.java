package org.apache.catalina.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.LibraryNotFoundError;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/AprLifecycleListener.class */
public class AprLifecycleListener implements LifecycleListener {
    protected static final int TCN_REQUIRED_MAJOR = 1;
    protected static final int TCN_REQUIRED_MINOR = 2;
    protected static final int TCN_REQUIRED_PATCH = 14;
    protected static final int TCN_RECOMMENDED_MINOR = 2;
    protected static final int TCN_RECOMMENDED_PV = 14;
    private static final int FIPS_ON = 1;
    private static final int FIPS_OFF = 0;
    private static final Log log = LogFactory.getLog(AprLifecycleListener.class);
    private static boolean instanceCreated = false;
    private static final List<String> initInfoLogMessages = new ArrayList(3);
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected static String SSLEngine = CustomBooleanEditor.VALUE_ON;
    protected static String FIPSMode = CustomBooleanEditor.VALUE_OFF;
    protected static String SSLRandomSeed = "builtin";
    protected static boolean sslInitialized = false;
    protected static boolean aprInitialized = false;
    protected static boolean aprAvailable = false;
    protected static boolean useAprConnector = false;
    protected static boolean useOpenSSL = true;
    protected static boolean fipsModeActive = false;
    protected static final Object lock = new Object();

    public static boolean isAprAvailable() {
        if (instanceCreated) {
            synchronized (lock) {
                init();
            }
        }
        return aprAvailable;
    }

    public AprLifecycleListener() {
        instanceCreated = true;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getType())) {
            synchronized (lock) {
                init();
                for (String msg : initInfoLogMessages) {
                    log.info(msg);
                }
                initInfoLogMessages.clear();
                if (aprAvailable) {
                    initializeSSL();
                }
                if (null != FIPSMode && !CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(FIPSMode) && !isFIPSModeActive()) {
                    Error e = new Error(sm.getString("aprListener.initializeFIPSFailed"));
                    log.fatal(e.getMessage(), e);
                    throw e;
                }
            }
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getType())) {
            synchronized (lock) {
                if (aprAvailable) {
                    terminateAPR();
                }
            }
        }
    }

    private static void terminateAPR() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = Class.forName("org.apache.tomcat.jni.Library").getMethod("terminate", null);
        method.invoke(null, null);
        aprAvailable = false;
        aprInitialized = false;
        sslInitialized = false;
        fipsModeActive = false;
    }

    private static void init() {
        if (aprInitialized) {
            return;
        }
        aprInitialized = true;
        try {
            Library.initialize(null);
            int major = Library.TCN_MAJOR_VERSION;
            int minor = Library.TCN_MINOR_VERSION;
            int patch = Library.TCN_PATCH_VERSION;
            int apver = (major * 1000) + (minor * 100) + patch;
            if (apver < 1214) {
                log.error(sm.getString("aprListener.tcnInvalid", major + "." + minor + "." + patch, "1.2.14"));
                try {
                    terminateAPR();
                    return;
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(ExceptionUtils.unwrapInvocationTargetException(t));
                    return;
                }
            }
            if (apver < 1214) {
                initInfoLogMessages.add(sm.getString("aprListener.tcnVersion", major + "." + minor + "." + patch, "1.2.14"));
            }
            initInfoLogMessages.add(sm.getString("aprListener.tcnValid", major + "." + minor + "." + patch, Library.APR_MAJOR_VERSION + "." + Library.APR_MINOR_VERSION + "." + Library.APR_PATCH_VERSION));
            initInfoLogMessages.add(sm.getString("aprListener.flags", Boolean.valueOf(Library.APR_HAVE_IPV6), Boolean.valueOf(Library.APR_HAS_SENDFILE), Boolean.valueOf(Library.APR_HAS_SO_ACCEPTFILTER), Boolean.valueOf(Library.APR_HAS_RANDOM)));
            initInfoLogMessages.add(sm.getString("aprListener.config", Boolean.valueOf(useAprConnector), Boolean.valueOf(useOpenSSL)));
            aprAvailable = true;
        } catch (LibraryNotFoundError lnfe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("aprListener.aprInitDebug", lnfe.getLibraryNames(), System.getProperty("java.library.path"), lnfe.getMessage()), lnfe);
            }
            initInfoLogMessages.add(sm.getString("aprListener.aprInit", System.getProperty("java.library.path")));
        } catch (Throwable t2) {
            Throwable t3 = ExceptionUtils.unwrapInvocationTargetException(t2);
            ExceptionUtils.handleThrowable(t3);
            log.warn(sm.getString("aprListener.aprInitError", t3.getMessage()), t3);
        }
    }

    private static void initializeSSL() throws Exception {
        boolean enterFipsMode;
        if (CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(SSLEngine) || sslInitialized) {
            return;
        }
        sslInitialized = true;
        Class<?>[] paramTypes = {String.class};
        Object[] paramValues = {SSLRandomSeed};
        Class<?> clazz = Class.forName("org.apache.tomcat.jni.SSL");
        Method method = clazz.getMethod("randSet", paramTypes);
        method.invoke(null, paramValues);
        paramValues[0] = CustomBooleanEditor.VALUE_ON.equalsIgnoreCase(SSLEngine) ? null : SSLEngine;
        Method method2 = clazz.getMethod("initialize", paramTypes);
        method2.invoke(null, paramValues);
        if (null != FIPSMode && !CustomBooleanEditor.VALUE_OFF.equalsIgnoreCase(FIPSMode)) {
            fipsModeActive = false;
            int fipsModeState = SSL.fipsModeGet();
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("aprListener.currentFIPSMode", Integer.valueOf(fipsModeState)));
            }
            if (CustomBooleanEditor.VALUE_ON.equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 1) {
                    log.info(sm.getString("aprListener.skipFIPSInitialization"));
                    fipsModeActive = true;
                    enterFipsMode = false;
                } else {
                    enterFipsMode = true;
                }
            } else if ("require".equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 1) {
                    fipsModeActive = true;
                    enterFipsMode = false;
                } else {
                    throw new IllegalStateException(sm.getString("aprListener.requireNotInFIPSMode"));
                }
            } else if ("enter".equalsIgnoreCase(FIPSMode)) {
                if (fipsModeState == 0) {
                    enterFipsMode = true;
                } else {
                    throw new IllegalStateException(sm.getString("aprListener.enterAlreadyInFIPSMode", Integer.valueOf(fipsModeState)));
                }
            } else {
                throw new IllegalArgumentException(sm.getString("aprListener.wrongFIPSMode", FIPSMode));
            }
            if (enterFipsMode) {
                log.info(sm.getString("aprListener.initializingFIPS"));
                if (SSL.fipsModeSet(1) != 1) {
                    String message = sm.getString("aprListener.initializeFIPSFailed");
                    log.error(message);
                    throw new IllegalStateException(message);
                }
                fipsModeActive = true;
                log.info(sm.getString("aprListener.initializeFIPSSuccess"));
            }
        }
        log.info(sm.getString("aprListener.initializedOpenSSL", SSL.versionString()));
    }

    public String getSSLEngine() {
        return SSLEngine;
    }

    public void setSSLEngine(String SSLEngine2) {
        if (!SSLEngine2.equals(SSLEngine)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLEngine"));
            }
            SSLEngine = SSLEngine2;
        }
    }

    public String getSSLRandomSeed() {
        return SSLRandomSeed;
    }

    public void setSSLRandomSeed(String SSLRandomSeed2) {
        if (!SSLRandomSeed2.equals(SSLRandomSeed)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForSSLRandomSeed"));
            }
            SSLRandomSeed = SSLRandomSeed2;
        }
    }

    public String getFIPSMode() {
        return FIPSMode;
    }

    public void setFIPSMode(String FIPSMode2) {
        if (!FIPSMode2.equals(FIPSMode)) {
            if (sslInitialized) {
                throw new IllegalStateException(sm.getString("aprListener.tooLateForFIPSMode"));
            }
            FIPSMode = FIPSMode2;
        }
    }

    public boolean isFIPSModeActive() {
        return fipsModeActive;
    }

    public void setUseAprConnector(boolean useAprConnector2) {
        if (useAprConnector2 != useAprConnector) {
            useAprConnector = useAprConnector2;
        }
    }

    public static boolean getUseAprConnector() {
        return useAprConnector;
    }

    public void setUseOpenSSL(boolean useOpenSSL2) {
        if (useOpenSSL2 != useOpenSSL) {
            useOpenSSL = useOpenSSL2;
        }
    }

    public static boolean getUseOpenSSL() {
        return useOpenSSL;
    }
}