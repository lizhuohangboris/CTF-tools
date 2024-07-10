package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/naming/SelfNaming.class */
public interface SelfNaming {
    ObjectName getObjectName() throws MalformedObjectNameException;
}