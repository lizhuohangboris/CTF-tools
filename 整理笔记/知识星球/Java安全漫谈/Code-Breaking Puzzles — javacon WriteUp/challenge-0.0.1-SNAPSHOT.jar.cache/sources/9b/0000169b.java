package org.springframework.boot.autoconfigure.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@ConditionalOnClass({HazelcastInstance.class, LocalContainerEntityManagerFactoryBean.class})
@AutoConfigureAfter({HazelcastAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastJpaDependencyAutoConfiguration.class */
public class HazelcastJpaDependencyAutoConfiguration {
    @Conditional({OnHazelcastAndJpaCondition.class})
    @Bean
    public static HazelcastInstanceJpaDependencyPostProcessor hazelcastInstanceJpaDependencyPostProcessor() {
        return new HazelcastInstanceJpaDependencyPostProcessor();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastJpaDependencyAutoConfiguration$HazelcastInstanceJpaDependencyPostProcessor.class */
    private static class HazelcastInstanceJpaDependencyPostProcessor extends EntityManagerFactoryDependsOnPostProcessor {
        HazelcastInstanceJpaDependencyPostProcessor() {
            super("hazelcastInstance");
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastJpaDependencyAutoConfiguration$OnHazelcastAndJpaCondition.class */
    static class OnHazelcastAndJpaCondition extends AllNestedConditions {
        OnHazelcastAndJpaCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(name = {"hazelcastInstance"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastJpaDependencyAutoConfiguration$OnHazelcastAndJpaCondition$HasHazelcastInstance.class */
        static class HasHazelcastInstance {
            HasHazelcastInstance() {
            }
        }

        @ConditionalOnBean({AbstractEntityManagerFactoryBean.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastJpaDependencyAutoConfiguration$OnHazelcastAndJpaCondition$HasJpa.class */
        static class HasJpa {
            HasJpa() {
            }
        }
    }
}