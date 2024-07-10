package org.springframework.validation.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/SpringConstraintValidatorFactory.class */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {
    private final AutowireCapableBeanFactory beanFactory;

    public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    @Override // javax.validation.ConstraintValidatorFactory
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T) this.beanFactory.createBean(key);
    }

    @Override // javax.validation.ConstraintValidatorFactory
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        this.beanFactory.destroyBean(instance);
    }
}