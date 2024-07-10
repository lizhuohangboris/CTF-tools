package org.springframework.boot.autoconfigure.transaction.jta;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.jndi.BitronixContext;
import java.io.File;
import javax.jms.Message;
import javax.transaction.TransactionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.boot.jta.bitronix.BitronixDependentBeanFactoryPostProcessor;
import org.springframework.boot.jta.bitronix.BitronixXAConnectionFactoryWrapper;
import org.springframework.boot.jta.bitronix.BitronixXADataSourceWrapper;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({JtaProperties.class})
@Configuration
@ConditionalOnClass({JtaTransactionManager.class, BitronixContext.class})
@ConditionalOnMissingBean({PlatformTransactionManager.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/BitronixJtaConfiguration.class */
class BitronixJtaConfiguration {
    private final JtaProperties jtaProperties;
    private final TransactionManagerCustomizers transactionManagerCustomizers;

    BitronixJtaConfiguration(JtaProperties jtaProperties, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        this.jtaProperties = jtaProperties;
        this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "spring.jta.bitronix.properties")
    @Bean
    public bitronix.tm.Configuration bitronixConfiguration() {
        bitronix.tm.Configuration config = TransactionManagerServices.getConfiguration();
        if (StringUtils.hasText(this.jtaProperties.getTransactionManagerId())) {
            config.setServerId(this.jtaProperties.getTransactionManagerId());
        }
        File logBaseDir = getLogBaseDir();
        config.setLogPart1Filename(new File(logBaseDir, "part1.btm").getAbsolutePath());
        config.setLogPart2Filename(new File(logBaseDir, "part2.btm").getAbsolutePath());
        config.setDisableJmx(true);
        return config;
    }

    private File getLogBaseDir() {
        if (StringUtils.hasLength(this.jtaProperties.getLogDir())) {
            return new File(this.jtaProperties.getLogDir());
        }
        File home = new ApplicationHome().getDir();
        return new File(home, "transaction-logs");
    }

    @ConditionalOnMissingBean({TransactionManager.class})
    @Bean
    public BitronixTransactionManager bitronixTransactionManager(bitronix.tm.Configuration configuration) {
        return TransactionManagerServices.getTransactionManager();
    }

    @ConditionalOnMissingBean({XADataSourceWrapper.class})
    @Bean
    public BitronixXADataSourceWrapper xaDataSourceWrapper() {
        return new BitronixXADataSourceWrapper();
    }

    @ConditionalOnMissingBean
    @Bean
    public static BitronixDependentBeanFactoryPostProcessor bitronixDependentBeanFactoryPostProcessor() {
        return new BitronixDependentBeanFactoryPostProcessor();
    }

    @Bean
    public JtaTransactionManager transactionManager(TransactionManager transactionManager) {
        PlatformTransactionManager jtaTransactionManager = new JtaTransactionManager(transactionManager);
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(jtaTransactionManager);
        }
        return jtaTransactionManager;
    }

    @Configuration
    @ConditionalOnClass({Message.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/BitronixJtaConfiguration$BitronixJtaJmsConfiguration.class */
    static class BitronixJtaJmsConfiguration {
        BitronixJtaJmsConfiguration() {
        }

        @ConditionalOnMissingBean({XAConnectionFactoryWrapper.class})
        @Bean
        public BitronixXAConnectionFactoryWrapper xaConnectionFactoryWrapper() {
            return new BitronixXAConnectionFactoryWrapper();
        }
    }
}