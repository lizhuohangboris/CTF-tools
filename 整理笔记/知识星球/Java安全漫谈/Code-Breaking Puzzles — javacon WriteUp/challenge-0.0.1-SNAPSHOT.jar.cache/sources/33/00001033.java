package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Instant;
import java.util.Date;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/pastorpresent/PastOrPresentValidatorForDate.class */
public class PastOrPresentValidatorForDate extends AbstractPastOrPresentInstantBasedValidator<Date> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    public Instant getInstant(Date value) {
        return value.toInstant();
    }
}