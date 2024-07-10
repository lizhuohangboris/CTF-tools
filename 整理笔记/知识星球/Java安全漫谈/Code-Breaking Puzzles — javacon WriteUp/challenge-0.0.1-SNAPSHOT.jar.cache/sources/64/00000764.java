package org.apache.catalina;

import javax.management.MBeanRegistration;
import javax.management.ObjectName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/JmxEnabled.class */
public interface JmxEnabled extends MBeanRegistration {
    String getDomain();

    void setDomain(String str);

    ObjectName getObjectName();
}