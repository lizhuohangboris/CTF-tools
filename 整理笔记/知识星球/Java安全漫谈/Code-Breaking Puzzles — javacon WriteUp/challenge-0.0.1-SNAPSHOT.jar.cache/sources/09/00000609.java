package javax.security.auth.message;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/MessageInfo.class */
public interface MessageInfo {
    Object getRequestMessage();

    Object getResponseMessage();

    void setRequestMessage(Object obj);

    void setResponseMessage(Object obj);

    Map getMap();
}