package org.apache.catalina;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/DistributedManager.class */
public interface DistributedManager {
    int getActiveSessionsFull();

    Set<String> getSessionIdsFull();
}