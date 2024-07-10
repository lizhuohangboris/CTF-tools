package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Duration;
import javax.validation.constraints.Future;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/AbstractFutureEpochBasedValidator.class */
public abstract class AbstractFutureEpochBasedValidator<T> extends AbstractEpochBasedTimeValidator<Future, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected boolean isValid(int result) {
        return result > 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}