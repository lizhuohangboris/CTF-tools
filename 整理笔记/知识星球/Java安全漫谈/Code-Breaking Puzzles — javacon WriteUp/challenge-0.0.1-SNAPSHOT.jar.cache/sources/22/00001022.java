package org.hibernate.validator.internal.constraintvalidators.bv.time.past;

import java.time.Clock;
import java.time.LocalDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/past/PastValidatorForLocalDate.class */
public class PastValidatorForLocalDate extends AbstractPastJavaTimeValidator<LocalDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public LocalDate getReferenceValue(Clock reference) {
        return LocalDate.now(reference);
    }
}