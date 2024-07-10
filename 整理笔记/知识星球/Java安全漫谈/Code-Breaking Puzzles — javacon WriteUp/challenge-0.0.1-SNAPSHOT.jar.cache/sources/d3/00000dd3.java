package org.apache.tomcat.websocket;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/BackgroundProcess.class */
public interface BackgroundProcess {
    void backgroundProcess();

    void setProcessPeriod(int i);

    int getProcessPeriod();
}