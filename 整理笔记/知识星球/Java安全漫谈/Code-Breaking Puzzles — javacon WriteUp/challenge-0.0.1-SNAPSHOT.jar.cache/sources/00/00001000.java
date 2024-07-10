package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import org.joda.time.Instant;
import org.joda.time.ReadablePartial;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForReadablePartial.class */
public class FutureValidatorForReadablePartial extends AbstractFutureEpochBasedValidator<ReadablePartial> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    public long getEpochMillis(ReadablePartial value, Clock reference) {
        return value.toDateTime(new Instant(reference.millis())).getMillis();
    }
}