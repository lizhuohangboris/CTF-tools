package org.springframework.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/AlternativeJdkIdGenerator.class */
public class AlternativeJdkIdGenerator implements IdGenerator {
    private final Random random;

    public AlternativeJdkIdGenerator() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] seed = new byte[8];
        secureRandom.nextBytes(seed);
        this.random = new Random(new BigInteger(seed).longValue());
    }

    @Override // org.springframework.util.IdGenerator
    public UUID generateId() {
        byte[] randomBytes = new byte[16];
        this.random.nextBytes(randomBytes);
        long mostSigBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 255);
        }
        long leastSigBits = 0;
        for (int i2 = 8; i2 < 16; i2++) {
            leastSigBits = (leastSigBits << 8) | (randomBytes[i2] & 255);
        }
        return new UUID(mostSigBits, leastSigBits);
    }
}