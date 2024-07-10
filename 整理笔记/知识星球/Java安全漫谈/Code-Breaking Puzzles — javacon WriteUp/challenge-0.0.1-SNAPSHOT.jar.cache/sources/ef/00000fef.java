package org.hibernate.validator.internal.constraintvalidators.bv.time;

import java.lang.Comparable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/AbstractJavaTimeValidator.class */
public abstract class AbstractJavaTimeValidator<C extends Annotation, T extends TemporalAccessor & Comparable<? super T>> implements HibernateConstraintValidator<C, T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected Clock referenceClock;

    protected abstract Duration getEffectiveTemporalValidationTolerance(Duration duration);

    protected abstract T getReferenceValue(Clock clock);

    protected abstract boolean isValid(int i);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        return isValid((AbstractJavaTimeValidator<C, T>) ((TemporalAccessor) obj), constraintValidatorContext);
    }

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidator
    public void initialize(ConstraintDescriptor<C> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        try {
            this.referenceClock = Clock.offset(initializationContext.getClockProvider().getClock(), getEffectiveTemporalValidationTolerance(initializationContext.getTemporalValidationTolerance()));
        } catch (Exception e) {
            throw LOG.getUnableToGetCurrentTimeFromClockProvider(e);
        }
    }

    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int result = ((Comparable) value).compareTo(getReferenceValue(this.referenceClock));
        return isValid(result);
    }
}