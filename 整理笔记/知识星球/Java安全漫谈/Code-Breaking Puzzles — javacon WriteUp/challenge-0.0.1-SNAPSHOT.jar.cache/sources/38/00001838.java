package org.springframework.boot.autoconfigure.validation;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ConditionalOnClass({ExecutableValidator.class})
@ConditionalOnResource(resources = {"classpath:META-INF/services/javax.validation.spi.ValidationProvider"})
@Import({PrimaryDefaultValidatorPostProcessor.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/validation/ValidationAutoConfiguration.class */
public class ValidationAutoConfiguration {
    @ConditionalOnMissingBean({Validator.class})
    @Bean
    @Role(2)
    public static LocalValidatorFactoryBean defaultValidator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        return factoryBean;
    }

    @ConditionalOnMissingBean
    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor(Environment environment, @Lazy Validator validator) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        boolean proxyTargetClass = ((Boolean) environment.getProperty("spring.aop.proxy-target-class", Boolean.class, true)).booleanValue();
        processor.setProxyTargetClass(proxyTargetClass);
        processor.setValidator(validator);
        return processor;
    }
}