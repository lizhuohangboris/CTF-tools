package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Pool.class */
public class Pool {
    public static native long create(long j);

    public static native void clear(long j);

    public static native void destroy(long j);

    public static native long parentGet(long j);

    public static native boolean isAncestor(long j, long j2);

    public static native long cleanupRegister(long j, Object obj);

    public static native void cleanupKill(long j, long j2);

    public static native void noteSubprocess(long j, long j2, int i);

    public static native ByteBuffer alloc(long j, int i);

    public static native ByteBuffer calloc(long j, int i);

    public static native int dataSet(long j, String str, Object obj);

    public static native Object dataGet(long j, String str);

    public static native void cleanupForExec();
}