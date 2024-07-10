package org.apache.tomcat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/JarScanFilter.class */
public interface JarScanFilter {
    boolean check(JarScanType jarScanType, String str);
}