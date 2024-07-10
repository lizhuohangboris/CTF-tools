package org.apache.catalina.authenticator;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/NonLoginAuthenticator.class */
public final class NonLoginAuthenticator extends AuthenticatorBase {
    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, true)) {
            if (this.cache) {
                request.getSessionInternal(true).setPrincipal(request.getPrincipal());
                return true;
            }
            return true;
        } else if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug("User authenticated without any roles");
            return true;
        } else {
            return true;
        }
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return "NONE";
    }
}