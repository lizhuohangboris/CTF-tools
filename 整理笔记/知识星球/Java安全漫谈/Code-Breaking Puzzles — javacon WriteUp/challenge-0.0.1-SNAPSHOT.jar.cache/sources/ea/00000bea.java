package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/Registry.class */
public class Registry {
    public static final int HKEY_CLASSES_ROOT = 1;
    public static final int HKEY_CURRENT_CONFIG = 2;
    public static final int HKEY_CURRENT_USER = 3;
    public static final int HKEY_LOCAL_MACHINE = 4;
    public static final int HKEY_USERS = 5;
    public static final int KEY_ALL_ACCESS = 1;
    public static final int KEY_CREATE_LINK = 2;
    public static final int KEY_CREATE_SUB_KEY = 4;
    public static final int KEY_ENUMERATE_SUB_KEYS = 8;
    public static final int KEY_EXECUTE = 16;
    public static final int KEY_NOTIFY = 32;
    public static final int KEY_QUERY_VALUE = 64;
    public static final int KEY_READ = 128;
    public static final int KEY_SET_VALUE = 256;
    public static final int KEY_WOW64_64KEY = 512;
    public static final int KEY_WOW64_32KEY = 1024;
    public static final int KEY_WRITE = 2048;
    public static final int REG_BINARY = 1;
    public static final int REG_DWORD = 2;
    public static final int REG_EXPAND_SZ = 3;
    public static final int REG_MULTI_SZ = 4;
    public static final int REG_QWORD = 5;
    public static final int REG_SZ = 6;

    public static native long create(int i, String str, int i2, long j) throws Error;

    public static native long open(int i, String str, int i2, long j) throws Error;

    public static native int close(long j);

    public static native int getType(long j, String str);

    public static native int getValueI(long j, String str) throws Error;

    public static native long getValueJ(long j, String str) throws Error;

    public static native int getSize(long j, String str);

    public static native String getValueS(long j, String str) throws Error;

    public static native String[] getValueA(long j, String str) throws Error;

    public static native byte[] getValueB(long j, String str) throws Error;

    public static native int setValueI(long j, String str, int i);

    public static native int setValueJ(long j, String str, long j2);

    public static native int setValueS(long j, String str, String str2);

    public static native int setValueE(long j, String str, String str2);

    public static native int setValueA(long j, String str, String[] strArr);

    public static native int setValueB(long j, String str, byte[] bArr);

    public static native String[] enumKeys(long j) throws Error;

    public static native String[] enumValues(long j) throws Error;

    public static native int deleteValue(long j, String str);

    public static native int deleteKey(int i, String str, boolean z);
}