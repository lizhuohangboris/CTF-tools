package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Instant;
import java.util.Date;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/PastValidatorForDate.class */
public class PastValidatorForDate extends AbstractPastInstantBasedValidator<Date> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    public Instant getInstant(Date value) {
        return value.toInstant();
    }
}