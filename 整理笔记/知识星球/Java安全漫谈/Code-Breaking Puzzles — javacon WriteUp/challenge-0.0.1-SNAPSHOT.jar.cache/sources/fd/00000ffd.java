package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.OffsetDateTime;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForOffsetDateTime.class */
public class FutureValidatorForOffsetDateTime extends AbstractFutureJavaTimeValidator<OffsetDateTime> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public OffsetDateTime getReferenceValue(Clock reference) {
        return OffsetDateTime.now(reference);
    }
}