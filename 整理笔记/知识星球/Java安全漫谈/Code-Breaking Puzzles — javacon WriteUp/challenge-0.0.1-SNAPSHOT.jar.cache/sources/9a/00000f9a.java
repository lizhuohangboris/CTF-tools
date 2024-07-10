package org.hibernate.validator.internal.constraintvalidators.bv.notempty;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/notempty/NotEmptyValidatorForCollection.class */
public class NotEmptyValidatorForCollection implements ConstraintValidator<NotEmpty, Collection> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
        return collection != null && collection.size() > 0;
    }
}