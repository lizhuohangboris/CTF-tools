package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/BIOCallback.class */
public interface BIOCallback {
    int write(byte[] bArr);

    int read(byte[] bArr);

    int puts(String str);

    String gets(int i);
}