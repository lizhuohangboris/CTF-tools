package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorContextImpl.class */
public class ValidatorContextImpl implements HibernateValidatorContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidatorFactoryImpl validatorFactory;
    private ConstraintValidatorFactory constraintValidatorFactory;
    private final ValidatorFactoryImpl.ValidatorFactoryScopedContext.Builder validatorFactoryScopedContextBuilder;
    private final ValueExtractorManager valueExtractorManager;
    private final MethodValidationConfiguration.Builder methodValidationConfigurationBuilder;
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> valueExtractorDescriptors = new HashMap();

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public /* bridge */ /* synthetic */ ValidatorContext addValueExtractor(ValueExtractor valueExtractor) {
        return addValueExtractor((ValueExtractor<?>) valueExtractor);
    }

    public ValidatorContextImpl(ValidatorFactoryImpl validatorFactory) {
        this.validatorFactoryScopedContextBuilder = new ValidatorFactoryImpl.ValidatorFactoryScopedContext.Builder(validatorFactory.getValidatorFactoryScopedContext());
        this.validatorFactory = validatorFactory;
        this.constraintValidatorFactory = validatorFactory.getConstraintValidatorFactory();
        this.methodValidationConfigurationBuilder = new MethodValidationConfiguration.Builder(validatorFactory.getMethodValidationConfiguration());
        this.valueExtractorManager = validatorFactory.getValueExtractorManager();
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext messageInterpolator(MessageInterpolator messageInterpolator) {
        this.validatorFactoryScopedContextBuilder.setMessageInterpolator(messageInterpolator);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext traversableResolver(TraversableResolver traversableResolver) {
        this.validatorFactoryScopedContextBuilder.setTraversableResolver(traversableResolver);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory) {
        if (factory == null) {
            this.constraintValidatorFactory = this.validatorFactory.getConstraintValidatorFactory();
        } else {
            this.constraintValidatorFactory = factory;
        }
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext parameterNameProvider(ParameterNameProvider parameterNameProvider) {
        this.validatorFactoryScopedContextBuilder.setParameterNameProvider(parameterNameProvider);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext clockProvider(ClockProvider clockProvider) {
        this.validatorFactoryScopedContextBuilder.setClockProvider(clockProvider);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext, javax.validation.ValidatorContext
    public HibernateValidatorContext addValueExtractor(ValueExtractor<?> extractor) {
        ValueExtractorDescriptor descriptor = new ValueExtractorDescriptor(extractor);
        ValueExtractorDescriptor previous = this.valueExtractorDescriptors.put(descriptor.getKey(), descriptor);
        if (previous != null) {
            throw LOG.getValueExtractorForTypeAndTypeUseAlreadyPresentException(extractor, previous.getValueExtractor());
        }
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext failFast(boolean failFast) {
        this.validatorFactoryScopedContextBuilder.setFailFast(failFast);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext allowOverridingMethodAlterParameterConstraint(boolean allow) {
        this.methodValidationConfigurationBuilder.allowOverridingMethodAlterParameterConstraint(allow);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext allowMultipleCascadedValidationOnReturnValues(boolean allow) {
        this.methodValidationConfigurationBuilder.allowMultipleCascadedValidationOnReturnValues(allow);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext allowParallelMethodsDefineParameterConstraints(boolean allow) {
        this.methodValidationConfigurationBuilder.allowParallelMethodsDefineParameterConstraints(allow);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext enableTraversableResolverResultCache(boolean enabled) {
        this.validatorFactoryScopedContextBuilder.setTraversableResolverResultCacheEnabled(enabled);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext temporalValidationTolerance(Duration temporalValidationTolerance) {
        this.validatorFactoryScopedContextBuilder.setTemporalValidationTolerance(temporalValidationTolerance);
        return this;
    }

    @Override // org.hibernate.validator.HibernateValidatorContext
    public HibernateValidatorContext constraintValidatorPayload(Object dynamicPayload) {
        this.validatorFactoryScopedContextBuilder.setConstraintValidatorPayload(dynamicPayload);
        return this;
    }

    @Override // javax.validation.ValidatorContext
    public Validator getValidator() {
        return this.validatorFactory.createValidator(this.constraintValidatorFactory, this.valueExtractorDescriptors.isEmpty() ? this.valueExtractorManager : new ValueExtractorManager(this.valueExtractorManager, this.valueExtractorDescriptors), this.validatorFactoryScopedContextBuilder.build(), this.methodValidationConfigurationBuilder.build());
    }
}