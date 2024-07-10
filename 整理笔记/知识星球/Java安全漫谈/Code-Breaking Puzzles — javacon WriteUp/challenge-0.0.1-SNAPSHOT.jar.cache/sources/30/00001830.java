package org.springframework.boot.autoconfigure.transaction.jta;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import java.io.File;
import java.util.Properties;
import javax.jms.Message;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.coyote.http11.Constants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.boot.jta.atomikos.AtomikosDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.jta.atomikos.AtomikosProperties;
import org.springframework.boot.jta.atomikos.AtomikosXAConnectionFactoryWrapper;
import org.springframework.boot.jta.atomikos.AtomikosXADataSourceWrapper;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({AtomikosProperties.class, JtaProperties.class})
@Configuration
@ConditionalOnClass({JtaTransactionManager.class, UserTransactionManager.class})
@ConditionalOnMissingBean({PlatformTransactionManager.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/AtomikosJtaConfiguration.class */
class AtomikosJtaConfiguration {
    private final JtaProperties jtaProperties;
    private final TransactionManagerCustomizers transactionManagerCustomizers;

    AtomikosJtaConfiguration(JtaProperties jtaProperties, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        this.jtaProperties = jtaProperties;
        this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @ConditionalOnMissingBean({UserTransactionService.class})
    @Bean(initMethod = "init", destroyMethod = "shutdownWait")
    public UserTransactionServiceImp userTransactionService(AtomikosProperties atomikosProperties) {
        Properties properties = new Properties();
        if (StringUtils.hasText(this.jtaProperties.getTransactionManagerId())) {
            properties.setProperty("com.atomikos.icatch.tm_unique_name", this.jtaProperties.getTransactionManagerId());
        }
        properties.setProperty("com.atomikos.icatch.log_base_dir", getLogBaseDir());
        properties.putAll(atomikosProperties.asProperties());
        return new UserTransactionServiceImp(properties);
    }

    private String getLogBaseDir() {
        if (StringUtils.hasLength(this.jtaProperties.getLogDir())) {
            return this.jtaProperties.getLogDir();
        }
        File home = new ApplicationHome().getDir();
        return new File(home, "transaction-logs").getAbsolutePath();
    }

    @ConditionalOnMissingBean
    @Bean(initMethod = "init", destroyMethod = Constants.CLOSE)
    public UserTransactionManager atomikosTransactionManager(UserTransactionService userTransactionService) throws Exception {
        UserTransactionManager manager = new UserTransactionManager();
        manager.setStartupTransactionService(false);
        manager.setForceShutdown(true);
        return manager;
    }

    @ConditionalOnMissingBean({XADataSourceWrapper.class})
    @Bean
    public AtomikosXADataSourceWrapper xaDataSourceWrapper() {
        return new AtomikosXADataSourceWrapper();
    }

    @ConditionalOnMissingBean
    @Bean
    public static AtomikosDependsOnBeanFactoryPostProcessor atomikosDependsOnBeanFactoryPostProcessor() {
        return new AtomikosDependsOnBeanFactoryPostProcessor();
    }

    @Bean
    public JtaTransactionManager transactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {
        PlatformTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(jtaTransactionManager);
        }
        return jtaTransactionManager;
    }

    @Configuration
    @ConditionalOnClass({Message.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/AtomikosJtaConfiguration$AtomikosJtaJmsConfiguration.class */
    static class AtomikosJtaJmsConfiguration {
        AtomikosJtaJmsConfiguration() {
        }

        @ConditionalOnMissingBean({XAConnectionFactoryWrapper.class})
        @Bean
        public AtomikosXAConnectionFactoryWrapper xaConnectionFactoryWrapper() {
            return new AtomikosXAConnectionFactoryWrapper();
        }
    }
}