package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/OS.class */
public class OS {
    private static final int UNIX = 1;
    private static final int WIN32 = 3;
    private static final int WIN64 = 4;
    private static final int LINUX = 5;
    private static final int SOLARIS = 6;
    private static final int BSD = 7;
    private static final int MACOSX = 8;
    public static final int LOG_EMERG = 1;
    public static final int LOG_ERROR = 2;
    public static final int LOG_NOTICE = 3;
    public static final int LOG_WARN = 4;
    public static final int LOG_INFO = 5;
    public static final int LOG_DEBUG = 6;
    @Deprecated
    public static final boolean IS_NETWARE = false;
    public static final boolean IS_UNIX = is(1);
    public static final boolean IS_WIN32 = is(3);
    public static final boolean IS_WIN64 = is(4);
    public static final boolean IS_LINUX = is(5);
    public static final boolean IS_SOLARIS = is(6);
    public static final boolean IS_BSD = is(7);
    public static final boolean IS_MACOSX = is(8);

    private static native boolean is(int i);

    public static native String defaultEncoding(long j);

    public static native String localeEncoding(long j);

    public static native int random(byte[] bArr, int i);

    public static native int info(long[] jArr);

    public static native String expand(String str);

    public static native void sysloginit(String str);

    public static native void syslog(int i, String str);
}