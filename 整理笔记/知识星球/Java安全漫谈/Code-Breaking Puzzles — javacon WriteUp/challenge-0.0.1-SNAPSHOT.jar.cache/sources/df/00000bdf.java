package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Lock.class */
public class Lock {
    public static final int APR_LOCK_FCNTL = 0;
    public static final int APR_LOCK_FLOCK = 1;
    public static final int APR_LOCK_SYSVSEM = 2;
    public static final int APR_LOCK_PROC_PTHREAD = 3;
    public static final int APR_LOCK_POSIXSEM = 4;
    public static final int APR_LOCK_DEFAULT = 5;

    public static native long create(String str, int i, long j) throws Error;

    public static native long childInit(String str, long j) throws Error;

    public static native int lock(long j);

    public static native int trylock(long j);

    public static native int unlock(long j);

    public static native int destroy(long j);

    public static native String lockfile(long j);

    public static native String name(long j);

    public static native String defname();
}