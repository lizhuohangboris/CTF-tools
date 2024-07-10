package org.hibernate.validator.internal.constraintvalidators.bv.time;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Duration;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/AbstractEpochBasedTimeValidator.class */
public abstract class AbstractEpochBasedTimeValidator<C extends Annotation, T> implements HibernateConstraintValidator<C, T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected Clock referenceClock;

    protected abstract Duration getEffectiveTemporalValidationTolerance(Duration duration);

    protected abstract long getEpochMillis(T t, Clock clock);

    protected abstract boolean isValid(int i);

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidator
    public void initialize(ConstraintDescriptor<C> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        try {
            this.referenceClock = Clock.offset(initializationContext.getClockProvider().getClock(), getEffectiveTemporalValidationTolerance(initializationContext.getTemporalValidationTolerance()));
        } catch (Exception e) {
            throw LOG.getUnableToGetCurrentTimeFromClockProvider(e);
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int result = Long.compare(getEpochMillis(value, this.referenceClock), this.referenceClock.millis());
        return isValid(result);
    }
}