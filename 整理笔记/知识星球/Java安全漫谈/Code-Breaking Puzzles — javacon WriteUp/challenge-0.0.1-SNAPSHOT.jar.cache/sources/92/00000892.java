package org.apache.catalina.realm;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.catalina.CredentialHandler;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/DigestCredentialHandlerBase.class */
public abstract class DigestCredentialHandlerBase implements CredentialHandler {
    protected static final StringManager sm = StringManager.getManager(DigestCredentialHandlerBase.class);
    public static final int DEFAULT_SALT_LENGTH = 32;
    private int iterations = getDefaultIterations();
    private int saltLength = getDefaultSaltLength();
    private final Object randomLock = new Object();
    private volatile Random random = null;
    private boolean logInvalidStoredCredentials = false;

    protected abstract String mutate(String str, byte[] bArr, int i);

    public abstract void setAlgorithm(String str) throws NoSuchAlgorithmException;

    public abstract String getAlgorithm();

    protected abstract int getDefaultIterations();

    protected abstract Log getLog();

    public int getIterations() {
        return this.iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public boolean getLogInvalidStoredCredentials() {
        return this.logInvalidStoredCredentials;
    }

    public void setLogInvalidStoredCredentials(boolean logInvalidStoredCredentials) {
        this.logInvalidStoredCredentials = logInvalidStoredCredentials;
    }

    @Override // org.apache.catalina.CredentialHandler
    public String mutate(String userCredential) {
        byte[] salt = null;
        int iterations = getIterations();
        int saltLength = getSaltLength();
        if (saltLength == 0) {
            salt = new byte[0];
        } else if (saltLength > 0) {
            if (this.random == null) {
                synchronized (this.randomLock) {
                    if (this.random == null) {
                        this.random = new SecureRandom();
                    }
                }
            }
            salt = new byte[saltLength];
            this.random.nextBytes(salt);
        }
        String serverCredential = mutate(userCredential, salt, iterations);
        if (serverCredential == null) {
            return null;
        }
        if (saltLength == 0 && iterations == 1) {
            return serverCredential;
        }
        StringBuilder result = new StringBuilder((saltLength << 1) + 10 + serverCredential.length() + 2);
        result.append(HexUtils.toHexString(salt));
        result.append('$');
        result.append(iterations);
        result.append('$');
        result.append(serverCredential);
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean matchesSaltIterationsEncoded(String inputCredentials, String storedCredentials) {
        if (storedCredentials == null) {
            logInvalidStoredCredentials(null);
            return false;
        }
        int sep1 = storedCredentials.indexOf(36);
        int sep2 = storedCredentials.indexOf(36, sep1 + 1);
        if (sep1 < 0 || sep2 < 0) {
            logInvalidStoredCredentials(storedCredentials);
            return false;
        }
        String hexSalt = storedCredentials.substring(0, sep1);
        int iterations = Integer.parseInt(storedCredentials.substring(sep1 + 1, sep2));
        String storedHexEncoded = storedCredentials.substring(sep2 + 1);
        try {
            byte[] salt = HexUtils.fromHexString(hexSalt);
            String inputHexEncoded = mutate(inputCredentials, salt, iterations, HexUtils.fromHexString(storedHexEncoded).length * 8);
            if (inputHexEncoded == null) {
                return false;
            }
            return storedHexEncoded.equalsIgnoreCase(inputHexEncoded);
        } catch (IllegalArgumentException e) {
            logInvalidStoredCredentials(storedCredentials);
            return false;
        }
    }

    private void logInvalidStoredCredentials(String storedCredentials) {
        if (this.logInvalidStoredCredentials) {
            getLog().warn(sm.getString("credentialHandler.invalidStoredCredential", storedCredentials));
        }
    }

    protected int getDefaultSaltLength() {
        return 32;
    }

    protected String mutate(String inputCredentials, byte[] salt, int iterations, int keyLength) {
        return mutate(inputCredentials, salt, iterations);
    }
}