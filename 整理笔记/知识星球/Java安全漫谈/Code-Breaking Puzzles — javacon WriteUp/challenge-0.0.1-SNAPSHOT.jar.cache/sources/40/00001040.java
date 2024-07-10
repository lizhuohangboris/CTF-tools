package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.chrono.ThaiBuddhistDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/pastorpresent/PastOrPresentValidatorForThaiBuddhistDate.class */
public class PastOrPresentValidatorForThaiBuddhistDate extends AbstractPastOrPresentJavaTimeValidator<ThaiBuddhistDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public ThaiBuddhistDate getReferenceValue(Clock reference) {
        return ThaiBuddhistDate.now(reference);
    }
}