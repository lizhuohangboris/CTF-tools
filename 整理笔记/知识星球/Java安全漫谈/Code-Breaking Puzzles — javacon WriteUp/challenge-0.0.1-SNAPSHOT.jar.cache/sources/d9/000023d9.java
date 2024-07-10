package org.springframework.validation.beanvalidation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.validation.annotation.Validated;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/MethodValidationInterceptor.class */
public class MethodValidationInterceptor implements MethodInterceptor {
    private final Validator validator;

    public MethodValidationInterceptor() {
        this(Validation.buildDefaultValidatorFactory());
    }

    public MethodValidationInterceptor(ValidatorFactory validatorFactory) {
        this(validatorFactory.getValidator());
    }

    public MethodValidationInterceptor(Validator validator) {
        this.validator = validator;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Set<ConstraintViolation<Object>> result;
        if (isFactoryBeanMetadataMethod(invocation.getMethod())) {
            return invocation.proceed();
        }
        Class<?>[] groups = determineValidationGroups(invocation);
        ExecutableValidator execVal = this.validator.forExecutables();
        Method methodToValidate = invocation.getMethod();
        try {
            result = execVal.validateParameters(invocation.getThis(), methodToValidate, invocation.getArguments(), groups);
        } catch (IllegalArgumentException e) {
            methodToValidate = BridgeMethodResolver.findBridgedMethod(ClassUtils.getMostSpecificMethod(invocation.getMethod(), invocation.getThis().getClass()));
            result = execVal.validateParameters(invocation.getThis(), methodToValidate, invocation.getArguments(), groups);
        }
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
        Object returnValue = invocation.proceed();
        Set<ConstraintViolation<Object>> result2 = execVal.validateReturnValue(invocation.getThis(), methodToValidate, returnValue, groups);
        if (!result2.isEmpty()) {
            throw new ConstraintViolationException(result2);
        }
        return returnValue;
    }

    private boolean isFactoryBeanMetadataMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if (clazz.isInterface()) {
            return (clazz == FactoryBean.class || clazz == SmartFactoryBean.class) && !method.getName().equals("getObject");
        }
        Class<?> factoryBeanType = null;
        if (SmartFactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = SmartFactoryBean.class;
        } else if (FactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = FactoryBean.class;
        }
        return (factoryBeanType == null || method.getName().equals("getObject") || !ClassUtils.hasMethod(factoryBeanType, method.getName(), method.getParameterTypes())) ? false : true;
    }

    protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
        Validated validatedAnn = (Validated) AnnotationUtils.findAnnotation(invocation.getMethod(), (Class<Annotation>) Validated.class);
        if (validatedAnn == null) {
            validatedAnn = (Validated) AnnotationUtils.findAnnotation(invocation.getThis().getClass(), (Class<Annotation>) Validated.class);
        }
        return validatedAnn != null ? validatedAnn.value() : new Class[0];
    }
}