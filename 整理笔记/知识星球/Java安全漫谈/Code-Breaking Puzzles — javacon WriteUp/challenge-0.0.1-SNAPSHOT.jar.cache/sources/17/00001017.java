package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.Year;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/FutureOrPresentValidatorForYear.class */
public class FutureOrPresentValidatorForYear extends AbstractFutureOrPresentJavaTimeValidator<Year> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public Year getReferenceValue(Clock reference) {
        return Year.now(reference);
    }
}