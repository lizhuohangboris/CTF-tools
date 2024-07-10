package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Duration;
import javax.validation.constraints.Past;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/AbstractPastEpochBasedValidator.class */
public abstract class AbstractPastEpochBasedValidator<T> extends AbstractEpochBasedTimeValidator<Past, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected boolean isValid(int result) {
        return result < 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance;
    }
}