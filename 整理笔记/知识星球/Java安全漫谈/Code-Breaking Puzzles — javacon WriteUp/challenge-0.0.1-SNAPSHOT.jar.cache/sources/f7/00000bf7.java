package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/User.class */
public class User {
    public static native long uidCurrent(long j) throws Error;

    public static native long gidCurrent(long j) throws Error;

    public static native long uid(String str, long j) throws Error;

    public static native long usergid(String str, long j) throws Error;

    public static native long gid(String str, long j) throws Error;

    public static native String username(long j, long j2) throws Error;

    public static native String groupname(long j, long j2) throws Error;

    public static native int uidcompare(long j, long j2);

    public static native int gidcompare(long j, long j2);

    public static native String homepath(String str, long j) throws Error;
}