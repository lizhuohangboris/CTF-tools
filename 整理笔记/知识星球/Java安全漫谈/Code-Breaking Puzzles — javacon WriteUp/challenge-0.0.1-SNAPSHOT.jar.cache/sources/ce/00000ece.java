package org.hibernate.validator;

import java.time.Duration;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidatorContext;
import javax.validation.valueextraction.ValueExtractor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/HibernateValidatorContext.class */
public interface HibernateValidatorContext extends ValidatorContext {
    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext messageInterpolator(MessageInterpolator messageInterpolator);

    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext traversableResolver(TraversableResolver traversableResolver);

    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext constraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory);

    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext parameterNameProvider(ParameterNameProvider parameterNameProvider);

    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext clockProvider(ClockProvider clockProvider);

    @Override // javax.validation.ValidatorContext
    HibernateValidatorContext addValueExtractor(ValueExtractor<?> valueExtractor);

    HibernateValidatorContext failFast(boolean z);

    HibernateValidatorContext allowOverridingMethodAlterParameterConstraint(boolean z);

    HibernateValidatorContext allowMultipleCascadedValidationOnReturnValues(boolean z);

    HibernateValidatorContext allowParallelMethodsDefineParameterConstraints(boolean z);

    HibernateValidatorContext enableTraversableResolverResultCache(boolean z);

    @Incubating
    HibernateValidatorContext temporalValidationTolerance(Duration duration);

    @Incubating
    HibernateValidatorContext constraintValidatorPayload(Object obj);

    @Override // javax.validation.ValidatorContext
    /* bridge */ /* synthetic */ default ValidatorContext addValueExtractor(ValueExtractor valueExtractor) {
        return addValueExtractor((ValueExtractor<?>) valueExtractor);
    }
}