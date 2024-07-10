package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Duration;
import javax.validation.constraints.PastOrPresent;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/pastorpresent/AbstractPastOrPresentEpochBasedValidator.class */
public abstract class AbstractPastOrPresentEpochBasedValidator<T> extends AbstractEpochBasedTimeValidator<PastOrPresent, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected boolean isValid(int result) {
        return result <= 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance;
    }
}