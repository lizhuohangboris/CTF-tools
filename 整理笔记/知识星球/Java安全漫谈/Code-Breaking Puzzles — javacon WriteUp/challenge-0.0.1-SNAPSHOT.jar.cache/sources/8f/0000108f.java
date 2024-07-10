package org.hibernate.validator.internal.engine.constraintvalidation;

import java.time.Duration;
import javax.validation.ClockProvider;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/HibernateConstraintValidatorInitializationContextImpl.class */
public class HibernateConstraintValidatorInitializationContextImpl implements HibernateConstraintValidatorInitializationContext {
    private final ScriptEvaluatorFactory scriptEvaluatorFactory;
    private final ClockProvider clockProvider;
    private final Duration temporalValidationTolerance;
    private final int hashCode = createHashCode();

    public HibernateConstraintValidatorInitializationContextImpl(ScriptEvaluatorFactory scriptEvaluatorFactory, ClockProvider clockProvider, Duration temporalValidationTolerance) {
        this.scriptEvaluatorFactory = scriptEvaluatorFactory;
        this.clockProvider = clockProvider;
        this.temporalValidationTolerance = temporalValidationTolerance;
    }

    public static HibernateConstraintValidatorInitializationContextImpl of(HibernateConstraintValidatorInitializationContextImpl defaultContext, ScriptEvaluatorFactory scriptEvaluatorFactory, ClockProvider clockProvider, Duration temporalValidationTolerance) {
        if (scriptEvaluatorFactory == defaultContext.scriptEvaluatorFactory && clockProvider == defaultContext.clockProvider && temporalValidationTolerance.equals(defaultContext.temporalValidationTolerance)) {
            return defaultContext;
        }
        return new HibernateConstraintValidatorInitializationContextImpl(scriptEvaluatorFactory, clockProvider, temporalValidationTolerance);
    }

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext
    public ScriptEvaluator getScriptEvaluatorForLanguage(String languageName) {
        return this.scriptEvaluatorFactory.getScriptEvaluatorByLanguageName(languageName);
    }

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext
    public ClockProvider getClockProvider() {
        return this.clockProvider;
    }

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext
    public Duration getTemporalValidationTolerance() {
        return this.temporalValidationTolerance;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HibernateConstraintValidatorInitializationContextImpl hibernateConstraintValidatorInitializationContextImpl = (HibernateConstraintValidatorInitializationContextImpl) o;
        if (this.scriptEvaluatorFactory != hibernateConstraintValidatorInitializationContextImpl.scriptEvaluatorFactory || this.clockProvider != hibernateConstraintValidatorInitializationContextImpl.clockProvider || !this.temporalValidationTolerance.equals(hibernateConstraintValidatorInitializationContextImpl.temporalValidationTolerance)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int createHashCode() {
        int result = System.identityHashCode(this.scriptEvaluatorFactory);
        return (31 * ((31 * result) + System.identityHashCode(this.clockProvider))) + this.temporalValidationTolerance.hashCode();
    }
}