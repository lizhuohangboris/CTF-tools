package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.lang.Comparable;
import java.time.Duration;
import java.time.temporal.TemporalAccessor;
import javax.validation.constraints.FutureOrPresent;
import org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/AbstractFutureOrPresentJavaTimeValidator.class */
public abstract class AbstractFutureOrPresentJavaTimeValidator<T extends TemporalAccessor & Comparable<? super T>> extends AbstractJavaTimeValidator<FutureOrPresent, T> {
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    protected boolean isValid(int result) {
        return result >= 0;
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    protected Duration getEffectiveTemporalValidationTolerance(Duration absoluteTemporalValidationTolerance) {
        return absoluteTemporalValidationTolerance.negated();
    }
}