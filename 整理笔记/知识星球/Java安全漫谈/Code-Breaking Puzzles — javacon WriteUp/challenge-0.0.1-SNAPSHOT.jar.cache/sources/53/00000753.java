package org.apache.catalina;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/AccessLog.class */
public interface AccessLog {
    public static final String REMOTE_ADDR_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteAddr";
    public static final String REMOTE_HOST_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteHost";
    public static final String PROTOCOL_ATTRIBUTE = "org.apache.catalina.AccessLog.Protocol";
    public static final String SERVER_PORT_ATTRIBUTE = "org.apache.catalina.AccessLog.ServerPort";

    void log(Request request, Response response, long j);

    void setRequestAttributesEnabled(boolean z);

    boolean getRequestAttributesEnabled();
}