package org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent;

import java.time.Clock;
import java.time.LocalDateTime;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/futureorpresent/FutureOrPresentValidatorForLocalDateTime.class */
public class FutureOrPresentValidatorForLocalDateTime extends AbstractFutureOrPresentJavaTimeValidator<LocalDateTime> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public LocalDateTime getReferenceValue(Clock reference) {
        return LocalDateTime.now(reference);
    }
}