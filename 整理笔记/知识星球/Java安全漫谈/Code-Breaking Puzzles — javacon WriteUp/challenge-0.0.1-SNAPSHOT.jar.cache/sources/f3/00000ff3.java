package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Instant;
import java.util.Calendar;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForCalendar.class */
public class FutureValidatorForCalendar extends AbstractFutureInstantBasedValidator<Calendar> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractInstantBasedTimeValidator
    public Instant getInstant(Calendar value) {
        return value.toInstant();
    }
}