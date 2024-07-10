package org.springframework.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/SimpleIdGenerator.class */
public class SimpleIdGenerator implements IdGenerator {
    private final AtomicLong mostSigBits = new AtomicLong(0);
    private final AtomicLong leastSigBits = new AtomicLong(0);

    @Override // org.springframework.util.IdGenerator
    public UUID generateId() {
        long leastSigBits = this.leastSigBits.incrementAndGet();
        if (leastSigBits == 0) {
            this.mostSigBits.incrementAndGet();
        }
        return new UUID(this.mostSigBits.get(), leastSigBits);
    }
}