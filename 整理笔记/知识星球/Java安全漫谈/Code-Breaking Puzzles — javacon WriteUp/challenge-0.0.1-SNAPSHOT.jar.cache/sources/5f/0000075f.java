package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Engine.class */
public interface Engine extends Container {
    String getDefaultHost();

    void setDefaultHost(String str);

    String getJvmRoute();

    void setJvmRoute(String str);

    Service getService();

    void setService(Service service);
}