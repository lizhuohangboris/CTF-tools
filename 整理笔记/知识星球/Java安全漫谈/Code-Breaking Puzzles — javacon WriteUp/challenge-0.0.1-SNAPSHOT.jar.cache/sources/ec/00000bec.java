package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/SSLConf.class */
public final class SSLConf {
    public static native long make(long j, int i) throws Exception;

    public static native void free(long j);

    public static native int check(long j, String str, String str2) throws Exception;

    public static native void assign(long j, long j2);

    public static native int apply(long j, String str, String str2) throws Exception;

    public static native int finish(long j);
}