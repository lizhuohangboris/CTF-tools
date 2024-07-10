package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Arrays;
import javax.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.ClassUtils;

@EnableConfigurationProperties({JpaProperties.class})
@Configuration
@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, EntityManager.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@Conditional({HibernateEntityManagerCondition.class})
@Import({HibernateJpaConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration.class */
public class HibernateJpaAutoConfiguration {

    @Order(-2147483628)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration$HibernateEntityManagerCondition.class */
    static class HibernateEntityManagerCondition extends SpringBootCondition {
        private static final String[] CLASS_NAMES = {"org.hibernate.ejb.HibernateEntityManager", "org.hibernate.jpa.HibernateEntityManager"};

        HibernateEntityManagerCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String[] strArr;
            ConditionMessage.Builder message = ConditionMessage.forCondition("HibernateEntityManager", new Object[0]);
            for (String className : CLASS_NAMES) {
                if (ClassUtils.isPresent(className, context.getClassLoader())) {
                    return ConditionOutcome.match(message.found("class").items(ConditionMessage.Style.QUOTE, className));
                }
            }
            return ConditionOutcome.noMatch(message.didNotFind("class", "classes").items(ConditionMessage.Style.QUOTE, Arrays.asList(CLASS_NAMES)));
        }
    }
}