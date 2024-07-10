package org.apache.tomcat.jni;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/jni/CertificateVerifier.class */
public interface CertificateVerifier {
    boolean verify(long j, byte[][] bArr, String str);
}