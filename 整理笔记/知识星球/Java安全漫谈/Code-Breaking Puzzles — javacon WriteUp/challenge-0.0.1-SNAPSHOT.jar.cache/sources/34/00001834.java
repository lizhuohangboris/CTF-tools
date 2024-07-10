package org.springframework.boot.autoconfigure.transaction.jta;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

@ConditionalOnJndi({"java:comp/UserTransaction", "java:comp/TransactionManager", "java:appserver/TransactionManager", "java:pm/TransactionManager", "java:/TransactionManager"})
@Configuration
@ConditionalOnClass({JtaTransactionManager.class})
@ConditionalOnMissingBean({PlatformTransactionManager.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/JndiJtaConfiguration.class */
class JndiJtaConfiguration {
    private final TransactionManagerCustomizers transactionManagerCustomizers;

    JndiJtaConfiguration(ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @Bean
    public JtaTransactionManager transactionManager() {
        PlatformTransactionManager object = new JtaTransactionManagerFactoryBean().getObject();
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(object);
        }
        return object;
    }
}