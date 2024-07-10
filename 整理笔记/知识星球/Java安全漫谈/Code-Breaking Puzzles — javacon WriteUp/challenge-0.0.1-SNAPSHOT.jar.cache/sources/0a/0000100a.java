package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.chrono.HijrahDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/FutureOrPresentValidatorForHijrahDate.class */
public class FutureOrPresentValidatorForHijrahDate extends AbstractFutureOrPresentJavaTimeValidator<HijrahDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}