package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/MBeanExportOperations.class */
public interface MBeanExportOperations {
    ObjectName registerManagedResource(Object obj) throws MBeanExportException;

    void registerManagedResource(Object obj, ObjectName objectName) throws MBeanExportException;

    void unregisterManagedResource(ObjectName objectName);
}