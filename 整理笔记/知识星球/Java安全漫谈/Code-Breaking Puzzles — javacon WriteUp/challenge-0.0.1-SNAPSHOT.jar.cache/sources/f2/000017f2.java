package org.springframework.boot.autoconfigure.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({SessionProperties.class})
@AutoConfigureBefore({HttpHandlerAutoConfiguration.class})
@Configuration
@ConditionalOnClass({Session.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HazelcastAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class})
@ConditionalOnWebApplication
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration.class */
public class SessionAutoConfiguration {

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Import({ServletSessionRepositoryValidator.class, SessionRepositoryFilterConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration.class */
    static class ServletSessionConfiguration {
        ServletSessionConfiguration() {
        }

        @ConditionalOnMissingBean({SessionRepository.class})
        @Configuration
        @Import({ServletSessionRepositoryImplementationValidator.class, ServletSessionConfigurationImportSelector.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration$ServletSessionRepositoryConfiguration.class */
        static class ServletSessionRepositoryConfiguration {
            ServletSessionRepositoryConfiguration() {
            }
        }
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Import({ReactiveSessionRepositoryValidator.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfiguration.class */
    static class ReactiveSessionConfiguration {
        ReactiveSessionConfiguration() {
        }

        @ConditionalOnMissingBean({ReactiveSessionRepository.class})
        @Configuration
        @Import({ReactiveSessionRepositoryImplementationValidator.class, ReactiveSessionConfigurationImportSelector.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfiguration$ReactiveSessionRepositoryConfiguration.class */
        static class ReactiveSessionRepositoryConfiguration {
            ReactiveSessionRepositoryConfiguration() {
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$SessionConfigurationImportSelector.class */
    static abstract class SessionConfigurationImportSelector implements ImportSelector {
        SessionConfigurationImportSelector() {
        }

        protected final String[] selectImports(WebApplicationType webApplicationType) {
            List<String> imports = new ArrayList<>();
            StoreType[] types = StoreType.values();
            for (StoreType storeType : types) {
                imports.add(SessionStoreMappings.getConfigurationClass(webApplicationType, storeType));
            }
            return StringUtils.toStringArray(imports);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfigurationImportSelector.class */
    static class ReactiveSessionConfigurationImportSelector extends SessionConfigurationImportSelector {
        ReactiveSessionConfigurationImportSelector() {
        }

        @Override // org.springframework.context.annotation.ImportSelector
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return super.selectImports(WebApplicationType.REACTIVE);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfigurationImportSelector.class */
    static class ServletSessionConfigurationImportSelector extends SessionConfigurationImportSelector {
        ServletSessionConfigurationImportSelector() {
        }

        @Override // org.springframework.context.annotation.ImportSelector
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return super.selectImports(WebApplicationType.SERVLET);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$AbstractSessionRepositoryImplementationValidator.class */
    static abstract class AbstractSessionRepositoryImplementationValidator {
        private final List<String> candidates;
        private final ClassLoader classLoader;
        private final SessionProperties sessionProperties;

        AbstractSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties, List<String> candidates) {
            this.classLoader = applicationContext.getClassLoader();
            this.sessionProperties = sessionProperties;
            this.candidates = candidates;
        }

        @PostConstruct
        public void checkAvailableImplementations() {
            List<Class<?>> availableCandidates = new ArrayList<>();
            for (String candidate : this.candidates) {
                addCandidateIfAvailable(availableCandidates, candidate);
            }
            StoreType storeType = this.sessionProperties.getStoreType();
            if (availableCandidates.size() > 1 && storeType == null) {
                throw new NonUniqueSessionRepositoryException(availableCandidates);
            }
        }

        private void addCandidateIfAvailable(List<Class<?>> candidates, String type) {
            try {
                Class<?> candidate = this.classLoader.loadClass(type);
                if (candidate != null) {
                    candidates.add(candidate);
                }
            } catch (Throwable th) {
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionRepositoryImplementationValidator.class */
    static class ServletSessionRepositoryImplementationValidator extends AbstractSessionRepositoryImplementationValidator {
        ServletSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties) {
            super(applicationContext, sessionProperties, Arrays.asList("org.springframework.session.hazelcast.HazelcastSessionRepository", "org.springframework.session.jdbc.JdbcOperationsSessionRepository", "org.springframework.session.data.mongo.MongoOperationsSessionRepository", "org.springframework.session.data.redis.RedisOperationsSessionRepository"));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionRepositoryImplementationValidator.class */
    static class ReactiveSessionRepositoryImplementationValidator extends AbstractSessionRepositoryImplementationValidator {
        ReactiveSessionRepositoryImplementationValidator(ApplicationContext applicationContext, SessionProperties sessionProperties) {
            super(applicationContext, sessionProperties, Arrays.asList("org.springframework.session.data.redis.ReactiveRedisOperationsSessionRepository", "org.springframework.session.data.mongo.ReactiveMongoOperationsSessionRepository"));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$AbstractSessionRepositoryValidator.class */
    static abstract class AbstractSessionRepositoryValidator {
        private final SessionProperties sessionProperties;
        private final ObjectProvider<?> sessionRepositoryProvider;

        protected AbstractSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<?> sessionRepositoryProvider) {
            this.sessionProperties = sessionProperties;
            this.sessionRepositoryProvider = sessionRepositoryProvider;
        }

        @PostConstruct
        public void checkSessionRepository() {
            StoreType storeType = this.sessionProperties.getStoreType();
            if (storeType != StoreType.NONE && this.sessionRepositoryProvider.getIfAvailable() == null && storeType != null) {
                throw new SessionRepositoryUnavailableException("No session repository could be auto-configured, check your configuration (session store type is '" + storeType.name().toLowerCase(Locale.ENGLISH) + "')", storeType);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionRepositoryValidator.class */
    static class ServletSessionRepositoryValidator extends AbstractSessionRepositoryValidator {
        ServletSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<SessionRepository<?>> sessionRepositoryProvider) {
            super(sessionProperties, sessionRepositoryProvider);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionRepositoryValidator.class */
    static class ReactiveSessionRepositoryValidator extends AbstractSessionRepositoryValidator {
        ReactiveSessionRepositoryValidator(SessionProperties sessionProperties, ObjectProvider<ReactiveSessionRepository<?>> sessionRepositoryProvider) {
            super(sessionProperties, sessionRepositoryProvider);
        }
    }
}