package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.pl.PESEL;
import org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase;
import org.hibernate.validator.internal.util.ModUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/pl/PESELValidator.class */
public class PESELValidator extends ModCheckBase implements ConstraintValidator<PESEL, CharSequence> {
    private static final int[] WEIGHTS_PESEL = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return super.isValid(charSequence, constraintValidatorContext);
    }

    @Override // javax.validation.ConstraintValidator
    public void initialize(PESEL constraintAnnotation) {
        super.initialize(0, Integer.MAX_VALUE, -1, true);
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        Collections.reverse(digits);
        int modResult = ModUtil.calculateModXCheckWithWeights(digits, 10, Integer.MAX_VALUE, WEIGHTS_PESEL);
        switch (modResult) {
            case 10:
                return checkDigit == '0';
            default:
                return Character.isDigit(checkDigit) && modResult == extractDigit(checkDigit);
        }
    }
}