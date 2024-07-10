package org.springframework.boot.autoconfigure.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.transaction.PlatformTransactionManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/TransactionManagerCustomizers.class */
public class TransactionManagerCustomizers {
    private final List<PlatformTransactionManagerCustomizer<?>> customizers;

    public TransactionManagerCustomizers(Collection<? extends PlatformTransactionManagerCustomizer<?>> customizers) {
        this.customizers = customizers != null ? new ArrayList<>(customizers) : Collections.emptyList();
    }

    public void customize(PlatformTransactionManager transactionManager) {
        LambdaSafe.callbacks(PlatformTransactionManagerCustomizer.class, this.customizers, transactionManager, new Object[0]).withLogger(TransactionManagerCustomizers.class).invoke(customizer -> {
            customizer.customize(transactionManager);
        });
    }
}