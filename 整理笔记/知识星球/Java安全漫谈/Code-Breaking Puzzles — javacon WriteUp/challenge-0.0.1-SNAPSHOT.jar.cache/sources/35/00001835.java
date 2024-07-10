package org.springframework.boot.autoconfigure.transaction.jta;

import javax.transaction.Transaction;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfigureBefore({XADataSourceAutoConfiguration.class, ActiveMQAutoConfiguration.class, ArtemisAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties({JtaProperties.class})
@Configuration
@ConditionalOnClass({Transaction.class})
@ConditionalOnProperty(prefix = "spring.jta", value = {"enabled"}, matchIfMissing = true)
@Import({JndiJtaConfiguration.class, BitronixJtaConfiguration.class, AtomikosJtaConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/JtaAutoConfiguration.class */
public class JtaAutoConfiguration {
}