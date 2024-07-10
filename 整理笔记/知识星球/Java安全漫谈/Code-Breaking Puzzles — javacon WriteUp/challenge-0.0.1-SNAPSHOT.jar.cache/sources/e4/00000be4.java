package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Poll.class */
public class Poll {
    public static final int APR_POLLIN = 1;
    public static final int APR_POLLPRI = 2;
    public static final int APR_POLLOUT = 4;
    public static final int APR_POLLERR = 16;
    public static final int APR_POLLHUP = 32;
    public static final int APR_POLLNVAL = 64;
    public static final int APR_POLLSET_THREADSAFE = 1;
    public static final int APR_NO_DESC = 0;
    public static final int APR_POLL_SOCKET = 1;
    public static final int APR_POLL_FILE = 2;
    public static final int APR_POLL_LASTDESC = 3;

    public static native long create(int i, long j, int i2, long j2) throws Error;

    public static native int destroy(long j);

    public static native int add(long j, long j2, int i);

    public static native int addWithTimeout(long j, long j2, int i, long j3);

    public static native int remove(long j, long j2);

    public static native int poll(long j, long j2, long[] jArr, boolean z);

    public static native int maintain(long j, long[] jArr, boolean z);

    public static native void setTtl(long j, long j2);

    public static native long getTtl(long j);

    public static native int pollset(long j, long[] jArr);

    public static native int interrupt(long j);

    public static native boolean wakeable(long j);
}