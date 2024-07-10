package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.HijrahDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/pastorpresent/PastOrPresentValidatorForHijrahDate.class */
public class PastOrPresentValidatorForHijrahDate extends AbstractPastOrPresentJavaTimeValidator<HijrahDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public HijrahDate getReferenceValue(Clock reference) {
        return HijrahDate.now(reference);
    }
}