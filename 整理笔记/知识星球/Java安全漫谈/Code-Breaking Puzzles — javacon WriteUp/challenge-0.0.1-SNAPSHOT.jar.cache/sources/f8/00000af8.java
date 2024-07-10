package org.apache.juli;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/WebappProperties.class */
public interface WebappProperties {
    String getWebappName();

    String getHostName();

    String getServiceName();

    boolean hasLoggingConfig();
}