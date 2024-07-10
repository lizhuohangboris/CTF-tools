package org.slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticLoggerBinder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/slf4j-api-1.7.25.jar:org/slf4j/LoggerFactory.class */
public final class LoggerFactory {
    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
    static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
    static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
    static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
    static final String LOGGER_NAME_MISMATCH_URL = "http://www.slf4j.org/codes.html#loggerNameMismatch";
    static final String REPLAY_URL = "http://www.slf4j.org/codes.html#replay";
    static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final int UNINITIALIZED = 0;
    static final int ONGOING_INITIALIZATION = 1;
    static final int FAILED_INITIALIZATION = 2;
    static final int SUCCESSFUL_INITIALIZATION = 3;
    static final int NOP_FALLBACK_INITIALIZATION = 4;
    static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
    static volatile int INITIALIZATION_STATE = 0;
    static final SubstituteLoggerFactory SUBST_FACTORY = new SubstituteLoggerFactory();
    static final NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
    static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
    static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty(DETECT_LOGGER_NAME_MISMATCH_PROPERTY);
    private static final String[] API_COMPATIBILITY_LIST = {"1.6", "1.7"};
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    private LoggerFactory() {
    }

    static void reset() {
        INITIALIZATION_STATE = 0;
    }

    private static final void performInitialization() {
        bind();
        if (INITIALIZATION_STATE == 3) {
            versionSanityCheck();
        }
    }

    private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
        if (msg == null) {
            return false;
        }
        if (msg.contains("org/slf4j/impl/StaticLoggerBinder") || msg.contains("org.slf4j.impl.StaticLoggerBinder")) {
            return true;
        }
        return false;
    }

    private static final void bind() {
        try {
            Set<URL> staticLoggerBinderPathSet = null;
            if (!isAndroid()) {
                staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
                reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
            }
            StaticLoggerBinder.getSingleton();
            INITIALIZATION_STATE = 3;
            reportActualBinding(staticLoggerBinderPathSet);
            fixSubstituteLoggers();
            replayEvents();
            SUBST_FACTORY.clear();
        } catch (Exception e) {
            failedBinding(e);
            throw new IllegalStateException("Unexpected initialization failure", e);
        } catch (NoClassDefFoundError ncde) {
            if (messageContainsOrgSlf4jImplStaticLoggerBinder(ncde.getMessage())) {
                INITIALIZATION_STATE = 4;
                Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
                Util.report("Defaulting to no-operation (NOP) logger implementation");
                Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
                return;
            }
            failedBinding(ncde);
            throw ncde;
        } catch (NoSuchMethodError nsme) {
            String msg = nsme.getMessage();
            if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
                INITIALIZATION_STATE = 2;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x.");
            }
            throw nsme;
        }
    }

    private static void fixSubstituteLoggers() {
        synchronized (SUBST_FACTORY) {
            SUBST_FACTORY.postInitialization();
            for (SubstituteLogger substLogger : SUBST_FACTORY.getLoggers()) {
                Logger logger = getLogger(substLogger.getName());
                substLogger.setDelegate(logger);
            }
        }
    }

    static void failedBinding(Throwable t) {
        INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }

    /* JADX WARN: Incorrect condition in loop: B:25:0x002b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void replayEvents() {
        /*
            org.slf4j.helpers.SubstituteLoggerFactory r0 = org.slf4j.LoggerFactory.SUBST_FACTORY
            java.util.concurrent.LinkedBlockingQueue r0 = r0.getEventQueue()
            r4 = r0
            r0 = r4
            int r0 = r0.size()
            r5 = r0
            r0 = 0
            r6 = r0
            r0 = 128(0x80, float:1.794E-43)
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = r0
            r2 = 128(0x80, float:1.794E-43)
            r1.<init>(r2)
            r8 = r0
        L1e:
            r0 = r4
            r1 = r8
            r2 = 128(0x80, float:1.794E-43)
            int r0 = r0.drainTo(r1, r2)
            r9 = r0
            r0 = r9
            if (r0 != 0) goto L31
            goto L6f
        L31:
            r0 = r8
            java.util.Iterator r0 = r0.iterator()
            r10 = r0
        L3a:
            r0 = r10
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L65
            r0 = r10
            java.lang.Object r0 = r0.next()
            org.slf4j.event.SubstituteLoggingEvent r0 = (org.slf4j.event.SubstituteLoggingEvent) r0
            r11 = r0
            r0 = r11
            replaySingleEvent(r0)
            r0 = r6
            int r6 = r6 + 1
            if (r0 != 0) goto L62
            r0 = r11
            r1 = r5
            emitReplayOrSubstituionWarning(r0, r1)
        L62:
            goto L3a
        L65:
            r0 = r8
            r0.clear()
            goto L1e
        L6f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.slf4j.LoggerFactory.replayEvents():void");
    }

    private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent event, int queueSize) {
        if (event.getLogger().isDelegateEventAware()) {
            emitReplayWarning(queueSize);
        } else if (!event.getLogger().isDelegateNOP()) {
            emitSubstitutionWarning();
        }
    }

    private static void replaySingleEvent(SubstituteLoggingEvent event) {
        if (event == null) {
            return;
        }
        SubstituteLogger substLogger = event.getLogger();
        String loggerName = substLogger.getName();
        if (substLogger.isDelegateNull()) {
            throw new IllegalStateException("Delegate logger cannot be null at this state.");
        }
        if (!substLogger.isDelegateNOP()) {
            if (substLogger.isDelegateEventAware()) {
                substLogger.log(event);
            } else {
                Util.report(loggerName);
            }
        }
    }

    private static void emitSubstitutionWarning() {
        Util.report("The following set of substitute loggers may have been accessed");
        Util.report("during the initialization phase. Logging calls during this");
        Util.report("phase were not honored. However, subsequent logging calls to these");
        Util.report("loggers will work as normally expected.");
        Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
    }

    private static void emitReplayWarning(int eventCount) {
        Util.report("A number (" + eventCount + ") of logging calls during the initialization phase have been intercepted and are");
        Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
        Util.report("See also http://www.slf4j.org/codes.html#replay");
    }

    private static final void versionSanityCheck() {
        try {
            String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
            boolean match = false;
            String[] arr$ = API_COMPATIBILITY_LIST;
            for (String aAPI_COMPATIBILITY_LIST : arr$) {
                if (requested.startsWith(aAPI_COMPATIBILITY_LIST)) {
                    match = true;
                }
            }
            if (!match) {
                Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
                Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
            }
        } catch (NoSuchFieldError e) {
        } catch (Throwable e2) {
            Util.report("Unexpected problem occured during version sanity check", e2);
        }
    }

    static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        Enumeration<URL> paths;
        Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<>();
        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            if (loggerFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
            } else {
                paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            }
            while (paths.hasMoreElements()) {
                URL path = paths.nextElement();
                staticLoggerBinderPathSet.add(path);
            }
        } catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderPathSet;
    }

    private static boolean isAmbiguousStaticLoggerBinderPathSet(Set<URL> binderPathSet) {
        return binderPathSet.size() > 1;
    }

    private static void reportMultipleBindingAmbiguity(Set<URL> binderPathSet) {
        if (isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
            Util.report("Class path contains multiple SLF4J bindings.");
            for (URL path : binderPathSet) {
                Util.report("Found binding in [" + path + "]");
            }
            Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
        }
    }

    private static boolean isAndroid() {
        String vendor = Util.safeGetSystemProperty(JAVA_VENDOR_PROPERTY);
        if (vendor == null) {
            return false;
        }
        return vendor.toLowerCase().contains("android");
    }

    private static void reportActualBinding(Set<URL> binderPathSet) {
        if (binderPathSet != null && isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
            Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
        }
    }

    public static Logger getLogger(String name) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        Class<?> autoComputedCallingClass;
        Logger logger = getLogger(clazz.getName());
        if (DETECT_LOGGER_NAME_MISMATCH && (autoComputedCallingClass = Util.getCallingClass()) != null && nonMatchingClasses(clazz, autoComputedCallingClass)) {
            Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", logger.getName(), autoComputedCallingClass.getName()));
            Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
        }
        return logger;
    }

    private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
        return !autoComputedCallingClass.isAssignableFrom(clazz);
    }

    public static ILoggerFactory getILoggerFactory() {
        if (INITIALIZATION_STATE == 0) {
            synchronized (LoggerFactory.class) {
                if (INITIALIZATION_STATE == 0) {
                    INITIALIZATION_STATE = 1;
                    performInitialization();
                }
            }
        }
        switch (INITIALIZATION_STATE) {
            case 1:
                return SUBST_FACTORY;
            case 2:
                throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
            case 3:
                return StaticLoggerBinder.getSingleton().getLoggerFactory();
            case 4:
                return NOP_FALLBACK_FACTORY;
            default:
                throw new IllegalStateException("Unreachable code");
        }
    }
}