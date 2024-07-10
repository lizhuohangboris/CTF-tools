package org.apache.tomcat.websocket;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.security.MD5Encoder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/DigestAuthenticator.class */
public class DigestAuthenticator extends Authenticator {
    public static final String schemeName = "digest";
    private SecureRandom cnonceGenerator;
    private int nonceCount = 0;
    private long cNonce;

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getAuthorization(String requestUri, String WWWAuthenticate, Map<String, Object> userProperties) throws AuthenticationException {
        String userName = (String) userProperties.get(Constants.WS_AUTHENTICATION_USER_NAME);
        String password = (String) userProperties.get(Constants.WS_AUTHENTICATION_PASSWORD);
        if (userName == null || password == null) {
            throw new AuthenticationException("Failed to perform Digest authentication due to  missing user/password");
        }
        Map<String, String> wwwAuthenticate = parseWWWAuthenticateHeader(WWWAuthenticate);
        String realm = wwwAuthenticate.get("realm");
        String nonce = wwwAuthenticate.get("nonce");
        String messageQop = wwwAuthenticate.get("qop");
        String algorithm = wwwAuthenticate.get("algorithm") == null ? "MD5" : wwwAuthenticate.get("algorithm");
        String opaque = wwwAuthenticate.get("opaque");
        StringBuilder challenge = new StringBuilder();
        if (!messageQop.isEmpty()) {
            if (this.cnonceGenerator == null) {
                this.cnonceGenerator = new SecureRandom();
            }
            this.cNonce = this.cnonceGenerator.nextLong();
            this.nonceCount++;
        }
        challenge.append("Digest ");
        challenge.append("username =\"" + userName + "\",");
        challenge.append("realm=\"" + realm + "\",");
        challenge.append("nonce=\"" + nonce + "\",");
        challenge.append("uri=\"" + requestUri + "\",");
        try {
            challenge.append("response=\"" + calculateRequestDigest(requestUri, userName, password, realm, nonce, messageQop, algorithm) + "\",");
            challenge.append("algorithm=" + algorithm + ",");
            challenge.append("opaque=\"" + opaque + "\",");
            if (!messageQop.isEmpty()) {
                challenge.append("qop=\"" + messageQop + "\"");
                challenge.append(",cnonce=\"" + this.cNonce + "\",");
                challenge.append("nc=" + String.format("%08X", Integer.valueOf(this.nonceCount)));
            }
            return challenge.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException("Unable to generate request digest " + e.getMessage());
        }
    }

    private String calculateRequestDigest(String requestUri, String userName, String password, String realm, String nonce, String qop, String algorithm) throws NoSuchAlgorithmException {
        String A1;
        StringBuilder preDigest = new StringBuilder();
        if (algorithm.equalsIgnoreCase("MD5")) {
            A1 = userName + ":" + realm + ":" + password;
        } else {
            A1 = encodeMD5(userName + ":" + realm + ":" + password) + ":" + nonce + ":" + this.cNonce;
        }
        String A2 = "GET:" + requestUri;
        preDigest.append(encodeMD5(A1));
        preDigest.append(":");
        preDigest.append(nonce);
        if (qop.toLowerCase().contains(ResourceRef.AUTH)) {
            preDigest.append(":");
            preDigest.append(String.format("%08X", Integer.valueOf(this.nonceCount)));
            preDigest.append(":");
            preDigest.append(String.valueOf(this.cNonce));
            preDigest.append(":");
            preDigest.append(qop);
        }
        preDigest.append(":");
        preDigest.append(encodeMD5(A2));
        return encodeMD5(preDigest.toString());
    }

    private String encodeMD5(String value) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = value.getBytes(StandardCharsets.ISO_8859_1);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        return MD5Encoder.encode(thedigest);
    }

    @Override // org.apache.tomcat.websocket.Authenticator
    public String getSchemeName() {
        return schemeName;
    }
}