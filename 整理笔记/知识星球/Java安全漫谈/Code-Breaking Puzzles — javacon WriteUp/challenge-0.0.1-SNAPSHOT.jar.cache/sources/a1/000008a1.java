package org.apache.catalina.realm;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/MessageDigestCredentialHandler.class */
public class MessageDigestCredentialHandler extends DigestCredentialHandlerBase {
    private static final Log log = LogFactory.getLog(MessageDigestCredentialHandler.class);
    public static final int DEFAULT_ITERATIONS = 1;
    private Charset encoding = StandardCharsets.UTF_8;
    private String algorithm = null;

    public String getEncoding() {
        return this.encoding.name();
    }

    public void setEncoding(String encodingName) {
        if (encodingName == null) {
            this.encoding = StandardCharsets.UTF_8;
            return;
        }
        try {
            this.encoding = B2CConverter.getCharset(encodingName);
        } catch (UnsupportedEncodingException e) {
            log.warn(sm.getString("mdCredentialHandler.unknownEncoding", encodingName, this.encoding.name()));
        }
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        ConcurrentMessageDigest.init(algorithm);
        this.algorithm = algorithm;
    }

    /* JADX WARN: Type inference failed for: r1v17, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r1v3, types: [byte[], byte[][]] */
    @Override // org.apache.catalina.CredentialHandler
    public boolean matches(String inputCredentials, String storedCredentials) {
        if (inputCredentials == null || storedCredentials == null) {
            return false;
        }
        if (getAlgorithm() == null) {
            return storedCredentials.equals(inputCredentials);
        }
        if (storedCredentials.startsWith("{MD5}") || storedCredentials.startsWith("{SHA}")) {
            String serverDigest = storedCredentials.substring(5);
            return Base64.encodeBase64String(ConcurrentMessageDigest.digest(getAlgorithm(), new byte[]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1)})).equals(serverDigest);
        } else if (storedCredentials.startsWith("{SSHA}")) {
            String serverDigestPlusSalt = storedCredentials.substring(6);
            byte[] serverDigestPlusSaltBytes = Base64.decodeBase64(serverDigestPlusSalt);
            byte[] serverDigestBytes = new byte[20];
            System.arraycopy(serverDigestPlusSaltBytes, 0, serverDigestBytes, 0, 20);
            int saltLength = serverDigestPlusSaltBytes.length - 20;
            byte[] serverSaltBytes = new byte[saltLength];
            System.arraycopy(serverDigestPlusSaltBytes, 20, serverSaltBytes, 0, saltLength);
            byte[] userDigestBytes = ConcurrentMessageDigest.digest(getAlgorithm(), new byte[]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1), serverSaltBytes});
            return Arrays.equals(userDigestBytes, serverDigestBytes);
        } else if (storedCredentials.indexOf(36) > -1) {
            return matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
        } else {
            String userDigest = mutate(inputCredentials, null, 1);
            if (userDigest == null) {
                return false;
            }
            return storedCredentials.equalsIgnoreCase(userDigest);
        }
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [byte[], byte[][]] */
    /* JADX WARN: Type inference failed for: r2v3, types: [byte[], byte[][]] */
    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        byte[] userDigest;
        if (this.algorithm == null) {
            return inputCredentials;
        }
        if (salt == null) {
            userDigest = ConcurrentMessageDigest.digest(this.algorithm, iterations, new byte[]{inputCredentials.getBytes(this.encoding)});
        } else {
            userDigest = ConcurrentMessageDigest.digest(this.algorithm, iterations, new byte[]{salt, inputCredentials.getBytes(this.encoding)});
        }
        return HexUtils.toHexString(userDigest);
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected int getDefaultIterations() {
        return 1;
    }

    @Override // org.apache.catalina.realm.DigestCredentialHandlerBase
    protected Log getLog() {
        return log;
    }
}