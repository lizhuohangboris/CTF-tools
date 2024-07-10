package org.hibernate.validator.internal.constraintvalidators.bv.notempty;

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/notempty/NotEmptyValidatorForMap.class */
public class NotEmptyValidatorForMap implements ConstraintValidator<NotEmpty, Map> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Map map, ConstraintValidatorContext constraintValidatorContext) {
        return map != null && map.size() > 0;
    }
}