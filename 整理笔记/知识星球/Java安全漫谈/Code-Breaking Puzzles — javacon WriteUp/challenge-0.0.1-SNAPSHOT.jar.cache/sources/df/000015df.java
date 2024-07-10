package org.springframework.boot.autoconfigure.context;

import java.time.Duration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties
@Configuration
@ConditionalOnMissingBean(value = {MessageSource.class}, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Integer.MIN_VALUE)
@Conditional({ResourceBundleCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/context/MessageSourceAutoConfiguration.class */
public class MessageSourceAutoConfiguration {
    private static final Resource[] NO_RESOURCES = new Resource[0];

    @ConfigurationProperties(prefix = "spring.messages")
    @Bean
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/context/MessageSourceAutoConfiguration$ResourceBundleCondition.class */
    protected static class ResourceBundleCondition extends SpringBootCondition {
        private static ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap<>();

        protected ResourceBundleCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String basename = context.getEnvironment().getProperty("spring.messages.basename", "messages");
            ConditionOutcome outcome = cache.get(basename);
            if (outcome == null) {
                outcome = getMatchOutcomeForBasename(context, basename);
                cache.put(basename, outcome);
            }
            return outcome;
        }

        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
            String[] commaDelimitedListToStringArray;
            Resource[] resources;
            ConditionMessage.Builder message = ConditionMessage.forCondition("ResourceBundle", new Object[0]);
            for (String name : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename))) {
                for (Resource resource : getResources(context.getClassLoader(), name)) {
                    if (resource.exists()) {
                        return ConditionOutcome.match(message.found("bundle").items(resource));
                    }
                }
            }
            return ConditionOutcome.noMatch(message.didNotFind("bundle with basename " + basename).atAll());
        }

        private Resource[] getResources(ClassLoader classLoader, String name) {
            String target = name.replace('.', '/');
            try {
                return new PathMatchingResourcePatternResolver(classLoader).getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + target + ".properties");
            } catch (Exception e) {
                return MessageSourceAutoConfiguration.NO_RESOURCES;
            }
        }
    }
}