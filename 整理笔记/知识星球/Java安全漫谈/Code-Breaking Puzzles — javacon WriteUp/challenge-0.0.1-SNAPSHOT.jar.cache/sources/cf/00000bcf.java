package org.apache.tomcat;

import javax.servlet.ServletContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/JarScanner.class */
public interface JarScanner {
    void scan(JarScanType jarScanType, ServletContext servletContext, JarScannerCallback jarScannerCallback);

    JarScanFilter getJarScanFilter();

    void setJarScanFilter(JarScanFilter jarScanFilter);
}