package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/MBeanExporterListener.class */
public interface MBeanExporterListener {
    void mbeanRegistered(ObjectName objectName);

    void mbeanUnregistered(ObjectName objectName);
}