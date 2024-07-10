package org.apache.tomcat.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/security/ConcurrentMessageDigest.class */
public class ConcurrentMessageDigest {
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA-1";
    private static final Map<String, Queue<MessageDigest>> queues = new HashMap();

    static {
        try {
            init(MD5);
            init(SHA1);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ConcurrentMessageDigest() {
    }

    public static byte[] digestMD5(byte[]... input) {
        return digest(MD5, input);
    }

    public static byte[] digestSHA1(byte[]... input) {
        return digest(SHA1, input);
    }

    public static byte[] digest(String algorithm, byte[]... input) {
        return digest(algorithm, 1, input);
    }

    public static byte[] digest(String algorithm, int rounds, byte[]... input) {
        Queue<MessageDigest> queue = queues.get(algorithm);
        if (queue == null) {
            throw new IllegalStateException("Must call init() first");
        }
        MessageDigest md = queue.poll();
        if (md == null) {
            try {
                md = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Must call init() first");
            }
        }
        for (byte[] bytes : input) {
            md.update(bytes);
        }
        byte[] result = md.digest();
        if (rounds > 1) {
            for (int i = 1; i < rounds; i++) {
                md.update(result);
                result = md.digest();
            }
        }
        queue.add(md);
        return result;
    }

    public static void init(String algorithm) throws NoSuchAlgorithmException {
        synchronized (queues) {
            if (!queues.containsKey(algorithm)) {
                MessageDigest md = MessageDigest.getInstance(algorithm);
                Queue<MessageDigest> queue = new ConcurrentLinkedQueue<>();
                queue.add(md);
                queues.put(algorithm, queue);
            }
        }
    }
}