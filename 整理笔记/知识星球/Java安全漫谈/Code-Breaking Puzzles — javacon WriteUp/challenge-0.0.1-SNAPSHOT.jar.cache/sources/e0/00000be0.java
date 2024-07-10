package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Mmap.class */
public class Mmap {
    public static final int APR_MMAP_READ = 1;
    public static final int APR_MMAP_WRITE = 2;

    public static native long create(long j, long j2, long j3, int i, long j4) throws Error;

    public static native long dup(long j, long j2) throws Error;

    public static native int delete(long j);

    public static native long offset(long j, long j2) throws Error;
}