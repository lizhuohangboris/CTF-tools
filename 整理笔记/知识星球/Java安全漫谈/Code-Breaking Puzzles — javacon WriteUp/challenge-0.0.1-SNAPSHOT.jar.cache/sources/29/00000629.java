package javax.security.auth.message.module;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.ServerAuth;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/security/auth/message/module/ServerAuthModule.class */
public interface ServerAuthModule extends ServerAuth {
    void initialize(MessagePolicy messagePolicy, MessagePolicy messagePolicy2, CallbackHandler callbackHandler, Map map) throws AuthException;

    Class[] getSupportedMessageTypes();
}