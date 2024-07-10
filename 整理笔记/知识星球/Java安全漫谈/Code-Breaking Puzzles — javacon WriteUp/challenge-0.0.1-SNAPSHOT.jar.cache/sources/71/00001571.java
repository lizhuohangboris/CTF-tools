package org.springframework.boot.autoconfigure.batch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/JpaBatchConfigurer.class */
public class JpaBatchConfigurer extends BasicBatchConfigurer {
    private static final Log logger = LogFactory.getLog(JpaBatchConfigurer.class);
    private final EntityManagerFactory entityManagerFactory;

    /* JADX INFO: Access modifiers changed from: protected */
    public JpaBatchConfigurer(BatchProperties properties, DataSource dataSource, TransactionManagerCustomizers transactionManagerCustomizers, EntityManagerFactory entityManagerFactory) {
        super(properties, dataSource, transactionManagerCustomizers);
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override // org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
    protected String determineIsolationLevel() {
        logger.warn("JPA does not support custom isolation levels, so locks may not be taken when launching Jobs");
        return "ISOLATION_DEFAULT";
    }

    @Override // org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer
    protected PlatformTransactionManager createTransactionManager() {
        return new JpaTransactionManager(this.entityManagerFactory);
    }
}