package javax.security.auth.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/AuthStatus.class */
public class AuthStatus {
    public static final AuthStatus SUCCESS = new AuthStatus("SUCCESS");
    public static final AuthStatus FAILURE = new AuthStatus("FAILURE");
    public static final AuthStatus SEND_SUCCESS = new AuthStatus("SEND_SUCCESS");
    public static final AuthStatus SEND_FAILURE = new AuthStatus("SEND_FAILURE");
    public static final AuthStatus SEND_CONTINUE = new AuthStatus("SEND_CONTINUE");
    private final String name;

    private AuthStatus(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}