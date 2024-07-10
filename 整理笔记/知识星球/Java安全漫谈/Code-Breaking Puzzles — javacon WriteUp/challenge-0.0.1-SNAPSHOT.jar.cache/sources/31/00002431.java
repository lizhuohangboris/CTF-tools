package org.springframework.web.bind.support;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/SpringWebConstraintValidatorFactory.class */
public class SpringWebConstraintValidatorFactory implements ConstraintValidatorFactory {
    @Override // javax.validation.ConstraintValidatorFactory
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T) getWebApplicationContext().getAutowireCapableBeanFactory().createBean(key);
    }

    @Override // javax.validation.ConstraintValidatorFactory
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        getWebApplicationContext().getAutowireCapableBeanFactory().destroyBean(instance);
    }

    protected WebApplicationContext getWebApplicationContext() {
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext registered for current thread - consider overriding SpringWebConstraintValidatorFactory.getWebApplicationContext()");
        }
        return wac;
    }
}