package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Duration;
import javax.validation.constraints.Past;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/AbstractPastInstantBasedValidator.class */
public abstract class AbstractPastInstantBasedValidator<T> extends AbstractInstantBasedTimeValidator<Past, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    protected boolean isValid(int result) {
        return result < 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance;
    }
}