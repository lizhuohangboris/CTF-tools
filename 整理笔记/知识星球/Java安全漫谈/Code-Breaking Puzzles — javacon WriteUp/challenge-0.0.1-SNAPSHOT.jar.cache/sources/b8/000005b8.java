package io.tricking.challenge;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/io/tricking/challenge/Encryptor.class */
public class Encryptor {
    static Logger logger = LoggerFactory.getLogger(Encryptor.class);

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(UriEscape.DEFAULT_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UriEscape.DEFAULT_ENCODING), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(1, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(UriEscape.DEFAULT_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(UriEscape.DEFAULT_ENCODING), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }
}