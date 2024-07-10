package org.springframework.boot.autoconfigure.data.neo4j;

import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.event.EventListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.web.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({Neo4jProperties.class})
@Configuration
@ConditionalOnClass({SessionFactory.class, Neo4jTransactionManager.class, PlatformTransactionManager.class})
@ConditionalOnMissingBean({SessionFactory.class})
@Import({Neo4jBookmarkManagementConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jDataAutoConfiguration.class */
public class Neo4jDataAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public org.neo4j.ogm.config.Configuration configuration(Neo4jProperties properties) {
        return properties.createConfiguration();
    }

    @Bean
    public SessionFactory sessionFactory(org.neo4j.ogm.config.Configuration configuration, ApplicationContext applicationContext, ObjectProvider<EventListener> eventListeners) {
        SessionFactory sessionFactory = new SessionFactory(configuration, getPackagesToScan(applicationContext));
        Stream<EventListener> stream = eventListeners.stream();
        sessionFactory.getClass();
        stream.forEach(this::register);
        return sessionFactory;
    }

    @ConditionalOnMissingBean({PlatformTransactionManager.class})
    @Bean
    public Neo4jTransactionManager transactionManager(SessionFactory sessionFactory, Neo4jProperties properties, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        return customize(new Neo4jTransactionManager(sessionFactory), transactionManagerCustomizers.getIfAvailable());
    }

    private Neo4jTransactionManager customize(Neo4jTransactionManager transactionManager, TransactionManagerCustomizers customizers) {
        if (customizers != null) {
            customizers.customize(transactionManager);
        }
        return transactionManager;
    }

    private String[] getPackagesToScan(ApplicationContext applicationContext) {
        List<String> packages = EntityScanPackages.get(applicationContext).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(applicationContext)) {
            packages = AutoConfigurationPackages.get(applicationContext);
        }
        return StringUtils.toStringArray(packages);
    }

    @Configuration
    @ConditionalOnClass({WebMvcConfigurer.class, OpenSessionInViewInterceptor.class})
    @ConditionalOnMissingBean({OpenSessionInViewInterceptor.class})
    @ConditionalOnProperty(prefix = "spring.data.neo4j", name = {"open-in-view"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jDataAutoConfiguration$Neo4jWebConfiguration.class */
    protected static class Neo4jWebConfiguration {
        protected Neo4jWebConfiguration() {
        }

        @Configuration
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jDataAutoConfiguration$Neo4jWebConfiguration$Neo4jWebMvcConfiguration.class */
        protected static class Neo4jWebMvcConfiguration implements WebMvcConfigurer {
            private static final Log logger = LogFactory.getLog(Neo4jWebMvcConfiguration.class);
            private final Neo4jProperties neo4jProperties;

            protected Neo4jWebMvcConfiguration(Neo4jProperties neo4jProperties) {
                this.neo4jProperties = neo4jProperties;
            }

            @Bean
            public OpenSessionInViewInterceptor neo4jOpenSessionInViewInterceptor() {
                if (this.neo4jProperties.getOpenInView() == null) {
                    logger.warn("spring.data.neo4j.open-in-view is enabled by default.Therefore, database queries may be performed during view rendering. Explicitly configure spring.data.neo4j.open-in-view to disable this warning");
                }
                return new OpenSessionInViewInterceptor();
            }

            @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addWebRequestInterceptor(neo4jOpenSessionInViewInterceptor());
            }
        }
    }
}