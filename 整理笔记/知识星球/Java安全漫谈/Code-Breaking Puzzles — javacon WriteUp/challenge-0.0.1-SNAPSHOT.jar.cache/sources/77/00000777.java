package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/StoreManager.class */
public interface StoreManager extends DistributedManager {
    Store getStore();

    void removeSuper(Session session);
}