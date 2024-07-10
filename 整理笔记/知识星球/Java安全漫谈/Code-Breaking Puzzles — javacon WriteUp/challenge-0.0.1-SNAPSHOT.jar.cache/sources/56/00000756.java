package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Cluster.class */
public interface Cluster extends Contained {
    String getClusterName();

    void setClusterName(String str);

    Manager createManager(String str);

    void registerManager(Manager manager);

    void removeManager(Manager manager);

    void backgroundProcess();
}