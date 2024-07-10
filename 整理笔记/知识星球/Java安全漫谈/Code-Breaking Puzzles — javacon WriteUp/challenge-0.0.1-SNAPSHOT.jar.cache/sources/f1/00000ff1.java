package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Duration;
import javax.validation.constraints.Future;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/AbstractFutureInstantBasedValidator.class */
public abstract class AbstractFutureInstantBasedValidator<T> extends AbstractInstantBasedTimeValidator<Future, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    protected boolean isValid(int result) {
        return result > 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}