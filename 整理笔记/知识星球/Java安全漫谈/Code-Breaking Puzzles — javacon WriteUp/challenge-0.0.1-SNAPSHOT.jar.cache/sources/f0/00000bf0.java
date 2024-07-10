package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Shm.class */
public class Shm {
    public static native long create(long j, String str, long j2) throws Error;

    public static native int remove(String str, long j);

    public static native int destroy(long j);

    public static native long attach(String str, long j) throws Error;

    public static native int detach(long j);

    public static native long baseaddr(long j);

    public static native long size(long j);

    public static native ByteBuffer buffer(long j);
}