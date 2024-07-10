package org.springframework.boot.autoconfigure.validation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/validation/ValidatorAdapter.class */
public class ValidatorAdapter implements SmartValidator, ApplicationContextAware, InitializingBean, DisposableBean {
    private final SmartValidator target;
    private final boolean existingBean;

    ValidatorAdapter(SmartValidator target, boolean existingBean) {
        this.target = target;
        this.existingBean = existingBean;
    }

    public final Validator getTarget() {
        return this.target;
    }

    @Override // org.springframework.validation.Validator
    public boolean supports(Class<?> clazz) {
        return this.target.supports(clazz);
    }

    @Override // org.springframework.validation.Validator
    public void validate(Object target, Errors errors) {
        this.target.validate(target, errors);
    }

    @Override // org.springframework.validation.SmartValidator
    public void validate(Object target, Errors errors, Object... validationHints) {
        this.target.validate(target, errors, validationHints);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (!this.existingBean && (this.target instanceof ApplicationContextAware)) {
            ((ApplicationContextAware) this.target).setApplicationContext(applicationContext);
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (!this.existingBean && (this.target instanceof InitializingBean)) {
            ((InitializingBean) this.target).afterPropertiesSet();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        if (!this.existingBean && (this.target instanceof DisposableBean)) {
            ((DisposableBean) this.target).destroy();
        }
    }

    public static Validator get(ApplicationContext applicationContext, Validator validator) {
        if (validator != null) {
            return wrap(validator, false);
        }
        return getExistingOrCreate(applicationContext);
    }

    private static Validator getExistingOrCreate(ApplicationContext applicationContext) {
        Validator existing = getExisting(applicationContext);
        if (existing != null) {
            return wrap(existing, true);
        }
        return create();
    }

    private static Validator getExisting(ApplicationContext applicationContext) {
        try {
            javax.validation.Validator validator = (javax.validation.Validator) applicationContext.getBean(javax.validation.Validator.class);
            if (validator instanceof Validator) {
                return (Validator) validator;
            }
            return new SpringValidatorAdapter(validator);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private static Validator create() {
        OptionalValidatorFactoryBean validator = new OptionalValidatorFactoryBean();
        validator.setMessageInterpolator(new MessageInterpolatorFactory().getObject());
        return wrap(validator, false);
    }

    private static Validator wrap(Validator validator, boolean existingBean) {
        if (validator instanceof javax.validation.Validator) {
            if (validator instanceof SpringValidatorAdapter) {
                return new ValidatorAdapter((SpringValidatorAdapter) validator, existingBean);
            }
            return new ValidatorAdapter(new SpringValidatorAdapter((javax.validation.Validator) validator), existingBean);
        }
        return validator;
    }
}