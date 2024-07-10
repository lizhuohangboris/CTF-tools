package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.chrono.MinguoDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/PastValidatorForMinguoDate.class */
public class PastValidatorForMinguoDate extends AbstractPastJavaTimeValidator<MinguoDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public MinguoDate getReferenceValue(Clock reference) {
        return MinguoDate.now(reference);
    }
}