package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/naming/ObjectNamingStrategy.class */
public interface ObjectNamingStrategy {
    ObjectName getObjectName(Object obj, @Nullable String str) throws MalformedObjectNameException;
}