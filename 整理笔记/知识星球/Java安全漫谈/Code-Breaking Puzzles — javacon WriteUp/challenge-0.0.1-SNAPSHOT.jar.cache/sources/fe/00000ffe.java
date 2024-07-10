package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.OffsetTime;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForOffsetTime.class */
public class FutureValidatorForOffsetTime extends AbstractFutureJavaTimeValidator<OffsetTime> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public OffsetTime getReferenceValue(Clock reference) {
        return OffsetTime.now(reference);
    }
}