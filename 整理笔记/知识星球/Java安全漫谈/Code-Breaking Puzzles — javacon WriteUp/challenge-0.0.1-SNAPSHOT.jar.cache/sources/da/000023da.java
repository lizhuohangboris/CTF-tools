package org.springframework.validation.beanvalidation;

import java.lang.annotation.Annotation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/MethodValidationPostProcessor.class */
public class MethodValidationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor implements InitializingBean {
    private Class<? extends Annotation> validatedAnnotationType = Validated.class;
    @Nullable
    private Validator validator;

    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, "'validatedAnnotationType' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }

    public void setValidator(Validator validator) {
        if (validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean) validator).getValidator();
        } else if (validator instanceof SpringValidatorAdapter) {
            this.validator = (Validator) validator.unwrap(Validator.class);
        } else {
            this.validator = validator;
        }
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        this.advisor = new DefaultPointcutAdvisor(pointcut, createMethodValidationAdvice(this.validator));
    }

    protected Advice createMethodValidationAdvice(@Nullable Validator validator) {
        return validator != null ? new MethodValidationInterceptor(validator) : new MethodValidationInterceptor();
    }
}