package org.hibernate.validator.internal.constraintvalidators.bv.time.future;

import java.time.Clock;
import java.time.chrono.JapaneseDate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/time/future/FutureValidatorForJapaneseDate.class */
public class FutureValidatorForJapaneseDate extends AbstractFutureJavaTimeValidator<JapaneseDate> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.time.AbstractJavaTimeValidator
    public JapaneseDate getReferenceValue(Clock reference) {
        return JapaneseDate.now(reference);
    }
}