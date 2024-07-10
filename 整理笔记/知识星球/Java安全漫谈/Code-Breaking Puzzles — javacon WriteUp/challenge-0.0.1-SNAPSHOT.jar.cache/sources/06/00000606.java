package javax.security.auth.message;

import javax.security.auth.login.LoginException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/AuthException.class */
public class AuthException extends LoginException {
    private static final long serialVersionUID = -1156951780670243758L;

    public AuthException() {
    }

    public AuthException(String msg) {
        super(msg);
    }
}