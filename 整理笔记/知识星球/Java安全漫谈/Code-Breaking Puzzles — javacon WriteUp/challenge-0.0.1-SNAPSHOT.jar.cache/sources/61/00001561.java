package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RetryTemplateFactory.class */
class RetryTemplateFactory {
    private final List<RabbitRetryTemplateCustomizer> customizers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RetryTemplateFactory(List<RabbitRetryTemplateCustomizer> customizers) {
        this.customizers = customizers;
    }

    public RetryTemplate createRetryTemplate(RabbitProperties.Retry properties, RabbitRetryTemplateCustomizer.Target target) {
        PropertyMapper map = PropertyMapper.get();
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getMaxAttempts);
        policy.getClass();
        from.to((v1) -> {
            r1.setMaxAttempts(v1);
        });
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        properties.getClass();
        PropertyMapper.Source as = map.from(this::getInitialInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        backOffPolicy.getClass();
        as.to((v1) -> {
            r1.setInitialInterval(v1);
        });
        properties.getClass();
        PropertyMapper.Source from2 = map.from(this::getMultiplier);
        backOffPolicy.getClass();
        from2.to((v1) -> {
            r1.setMultiplier(v1);
        });
        properties.getClass();
        PropertyMapper.Source as2 = map.from(this::getMaxInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        backOffPolicy.getClass();
        as2.to((v1) -> {
            r1.setMaxInterval(v1);
        });
        template.setBackOffPolicy(backOffPolicy);
        if (this.customizers != null) {
            for (RabbitRetryTemplateCustomizer customizer : this.customizers) {
                customizer.customize(target, template);
            }
        }
        return template;
    }
}