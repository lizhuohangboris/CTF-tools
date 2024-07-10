package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.YearMonth;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/FutureOrPresentValidatorForYearMonth.class */
public class FutureOrPresentValidatorForYearMonth extends AbstractFutureOrPresentJavaTimeValidator<YearMonth> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public YearMonth getReferenceValue(Clock reference) {
        return YearMonth.now(reference);
    }
}