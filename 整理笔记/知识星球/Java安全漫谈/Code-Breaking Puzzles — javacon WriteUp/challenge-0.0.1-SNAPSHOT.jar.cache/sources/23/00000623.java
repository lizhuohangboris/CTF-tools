package javax.security.auth.message.config;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/config/ClientAuthConfig.class */
public interface ClientAuthConfig extends AuthConfig {
    ClientAuthContext getAuthContext(String str, Subject subject, Map map) throws AuthException;
}