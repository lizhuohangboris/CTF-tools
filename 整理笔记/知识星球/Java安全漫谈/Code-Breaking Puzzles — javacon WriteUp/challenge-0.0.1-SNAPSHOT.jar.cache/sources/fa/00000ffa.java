package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.LocalTime;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForLocalTime.class */
public class FutureValidatorForLocalTime extends AbstractFutureJavaTimeValidator<LocalTime> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public LocalTime getReferenceValue(Clock reference) {
        return LocalTime.now(reference);
    }
}