package org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent;

import java.time.Clock;
import java.time.ZonedDateTime;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/pastorpresent/PastOrPresentValidatorForZonedDateTime.class */
public class PastOrPresentValidatorForZonedDateTime extends AbstractPastOrPresentJavaTimeValidator<ZonedDateTime> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public ZonedDateTime getReferenceValue(Clock reference) {
        return ZonedDateTime.now(reference);
    }
}