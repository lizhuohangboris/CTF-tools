package org.apache.catalina.authenticator;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/SSLAuthenticator.class */
public class SSLAuthenticator extends AuthenticatorBase {
    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug(" Looking up certificates");
        }
        X509Certificate[] certs = getRequestCertificates(request);
        if (certs == null || certs.length < 1) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("  No certificates included with this request");
            }
            response.sendError(401, sm.getString("authenticator.certificates"));
            return false;
        }
        Principal principal = this.context.getRealm().authenticate(certs);
        if (principal == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("  Realm.authenticate() returned false");
            }
            response.sendError(401, sm.getString("authenticator.unauthorized"));
            return false;
        }
        register(request, response, principal, HttpServletRequest.CLIENT_CERT_AUTH, null, null);
        return true;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.CLIENT_CERT_AUTH;
    }
}