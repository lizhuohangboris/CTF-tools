package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import org.joda.time.ReadableInstant;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/PastValidatorForReadableInstant.class */
public class PastValidatorForReadableInstant extends AbstractPastEpochBasedValidator<ReadableInstant> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractEpochBasedTimeValidator
    public long getEpochMillis(ReadableInstant value, Clock reference) {
        return value.getMillis();
    }
}