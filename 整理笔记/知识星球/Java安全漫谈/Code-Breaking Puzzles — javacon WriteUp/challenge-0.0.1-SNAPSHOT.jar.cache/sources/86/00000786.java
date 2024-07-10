package org.apache.catalina.authenticator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.codec.binary.Base64;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/BasicAuthenticator.class */
public class BasicAuthenticator extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(BasicAuthenticator.class);
    private Charset charset = StandardCharsets.ISO_8859_1;
    private String charsetString = null;

    public String getCharset() {
        return this.charsetString;
    }

    public void setCharset(String charsetString) {
        if (charsetString == null || charsetString.isEmpty()) {
            this.charset = StandardCharsets.ISO_8859_1;
        } else if (UriEscape.DEFAULT_ENCODING.equalsIgnoreCase(charsetString)) {
            this.charset = StandardCharsets.UTF_8;
        } else {
            throw new IllegalArgumentException(sm.getString("basicAuthenticator.invalidCharset"));
        }
        this.charsetString = charsetString;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization != null) {
            authorization.toBytes();
            ByteChunk authorizationBC = authorization.getByteChunk();
            try {
                BasicCredentials credentials = new BasicCredentials(authorizationBC, this.charset);
                String username = credentials.getUsername();
                String password = credentials.getPassword();
                Principal principal = this.context.getRealm().authenticate(username, password);
                if (principal != null) {
                    register(request, response, principal, HttpServletRequest.BASIC_AUTH, username, password);
                    return true;
                }
            } catch (IllegalArgumentException iae) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Invalid Authorization" + iae.getMessage());
                }
            }
        }
        StringBuilder value = new StringBuilder(16);
        value.append("Basic realm=\"");
        value.append(getRealmName(this.context));
        value.append('\"');
        if (this.charsetString != null && !this.charsetString.isEmpty()) {
            value.append(", charset=");
            value.append(this.charsetString);
        }
        response.setHeader("WWW-Authenticate", value.toString());
        response.sendError(401);
        return false;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.BASIC_AUTH;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/BasicAuthenticator$BasicCredentials.class */
    public static class BasicCredentials {
        private static final String METHOD = "basic ";
        private final Charset charset;
        private final ByteChunk authorization;
        private final int initialOffset;
        private int base64blobOffset;
        private int base64blobLength;
        private String username = null;
        private String password = null;

        public BasicCredentials(ByteChunk input, Charset charset) throws IllegalArgumentException {
            this.authorization = input;
            this.initialOffset = input.getOffset();
            this.charset = charset;
            parseMethod();
            byte[] decoded = parseBase64();
            parseCredentials(decoded);
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        private void parseMethod() throws IllegalArgumentException {
            if (this.authorization.startsWithIgnoreCase(METHOD, 0)) {
                this.base64blobOffset = this.initialOffset + METHOD.length();
                this.base64blobLength = this.authorization.getLength() - METHOD.length();
                return;
            }
            throw new IllegalArgumentException("Authorization header method is not \"Basic\"");
        }

        private byte[] parseBase64() throws IllegalArgumentException {
            byte[] decoded = Base64.decodeBase64(this.authorization.getBuffer(), this.base64blobOffset, this.base64blobLength);
            this.authorization.setOffset(this.initialOffset);
            if (decoded == null) {
                throw new IllegalArgumentException("Basic Authorization credentials are not Base64");
            }
            return decoded;
        }

        private void parseCredentials(byte[] decoded) throws IllegalArgumentException {
            int colon = -1;
            int i = 0;
            while (true) {
                if (i >= decoded.length) {
                    break;
                } else if (decoded[i] != 58) {
                    i++;
                } else {
                    colon = i;
                    break;
                }
            }
            if (colon < 0) {
                this.username = new String(decoded, this.charset);
            } else {
                this.username = new String(decoded, 0, colon, this.charset);
                this.password = new String(decoded, colon + 1, (decoded.length - colon) - 1, this.charset);
                if (this.password.length() > 1) {
                    this.password = this.password.trim();
                }
            }
            if (this.username.length() > 1) {
                this.username = this.username.trim();
            }
        }
    }
}