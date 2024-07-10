package org.apache.logging.log4j.util;

import org.apache.tomcat.jni.SSL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/Constants.class */
public final class Constants {
    public static final boolean IS_WEB_APP = PropertiesUtil.getProperties().getBooleanProperty("log4j2.is.webapp", isClassAvailable("javax.servlet.Servlet"));
    public static final boolean ENABLE_THREADLOCALS;
    public static final int JAVA_MAJOR_VERSION;
    public static final int MAX_REUSABLE_MESSAGE_SIZE;
    public static final String LOG4J2_DEBUG = "log4j2.debug";

    static {
        ENABLE_THREADLOCALS = !IS_WEB_APP && PropertiesUtil.getProperties().getBooleanProperty("log4j2.enable.threadlocals", true);
        JAVA_MAJOR_VERSION = getMajorVersion();
        MAX_REUSABLE_MESSAGE_SIZE = size("log4j.maxReusableMsgSize", SSL.SSL_INFO_SERVER_A_KEY);
    }

    private static int size(String property, int defaultValue) {
        return PropertiesUtil.getProperties().getIntegerProperty(property, defaultValue);
    }

    private static boolean isClassAvailable(String className) {
        try {
            return LoaderUtil.loadClass(className) != null;
        } catch (Throwable th) {
            return false;
        }
    }

    private Constants() {
    }

    private static int getMajorVersion() {
        String version = System.getProperty("java.version");
        String[] parts = version.split("-|\\.");
        try {
            int token = Integer.parseInt(parts[0]);
            boolean isJEP223 = token != 1;
            if (isJEP223) {
                return token;
            }
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }
}