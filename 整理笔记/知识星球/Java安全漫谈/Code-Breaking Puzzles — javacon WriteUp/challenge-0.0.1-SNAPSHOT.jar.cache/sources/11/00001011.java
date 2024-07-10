package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.MonthDay;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/FutureOrPresentValidatorForMonthDay.class */
public class FutureOrPresentValidatorForMonthDay extends AbstractFutureOrPresentJavaTimeValidator<MonthDay> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public MonthDay getReferenceValue(Clock reference) {
        return MonthDay.now(reference);
    }
}