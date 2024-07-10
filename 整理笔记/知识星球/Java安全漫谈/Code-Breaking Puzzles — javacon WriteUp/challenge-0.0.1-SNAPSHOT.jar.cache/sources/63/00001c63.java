package org.springframework.cglib.transform.impl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/InterceptFieldCallback.class */
public interface InterceptFieldCallback {
    int writeInt(Object obj, String str, int i, int i2);

    char writeChar(Object obj, String str, char c, char c2);

    byte writeByte(Object obj, String str, byte b, byte b2);

    boolean writeBoolean(Object obj, String str, boolean z, boolean z2);

    short writeShort(Object obj, String str, short s, short s2);

    float writeFloat(Object obj, String str, float f, float f2);

    double writeDouble(Object obj, String str, double d, double d2);

    long writeLong(Object obj, String str, long j, long j2);

    Object writeObject(Object obj, String str, Object obj2, Object obj3);

    int readInt(Object obj, String str, int i);

    char readChar(Object obj, String str, char c);

    byte readByte(Object obj, String str, byte b);

    boolean readBoolean(Object obj, String str, boolean z);

    short readShort(Object obj, String str, short s);

    float readFloat(Object obj, String str, float f);

    double readDouble(Object obj, String str, double d);

    long readLong(Object obj, String str, long j);

    Object readObject(Object obj, String str, Object obj2);
}