package javax.security.auth.message;

import javax.security.auth.Subject;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/ServerAuth.class */
public interface ServerAuth {
    AuthStatus validateRequest(MessageInfo messageInfo, Subject subject, Subject subject2) throws AuthException;

    AuthStatus secureResponse(MessageInfo messageInfo, Subject subject) throws AuthException;

    void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException;
}