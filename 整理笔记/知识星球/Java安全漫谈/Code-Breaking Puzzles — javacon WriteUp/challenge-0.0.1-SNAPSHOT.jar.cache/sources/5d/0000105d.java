package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/pl/PolishNumberValidator.class */
public abstract class PolishNumberValidator<T extends Annotation> extends ModCheckBase implements ConstraintValidator<T, CharSequence> {
    protected abstract int[] getWeights(List<Integer> list);

    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return super.isValid(charSequence, constraintValidatorContext);
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        Collections.reverse(digits);
        int modResult = 11 - ModUtil.calculateModXCheckWithWeights(digits, 11, Integer.MAX_VALUE, getWeights(digits));
        switch (modResult) {
            case 10:
            case 11:
                return checkDigit == '0';
            default:
                return Character.isDigit(checkDigit) && modResult == extractDigit(checkDigit);
        }
    }
}