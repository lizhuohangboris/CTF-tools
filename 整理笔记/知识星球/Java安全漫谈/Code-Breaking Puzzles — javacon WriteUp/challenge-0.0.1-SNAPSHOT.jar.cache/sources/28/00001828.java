package org.springframework.boot.autoconfigure.transaction;

import org.springframework.transaction.PlatformTransactionManager;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/PlatformTransactionManagerCustomizer.class */
public interface PlatformTransactionManagerCustomizer<T extends PlatformTransactionManager> {
    void customize(T transactionManager);
}