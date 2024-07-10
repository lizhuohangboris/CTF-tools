package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.internal.util.ModUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/LuhnCheckValidator.class */
public class LuhnCheckValidator extends ModCheckBase implements ConstraintValidator<LuhnCheck, CharSequence> {
    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return super.isValid(charSequence, constraintValidatorContext);
    }

    @Override // javax.validation.ConstraintValidator
    public void initialize(LuhnCheck constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitIndex(), constraintAnnotation.ignoreNonDigitCharacters());
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult = ModUtil.calculateLuhnMod10Check(digits);
        if (!Character.isDigit(checkDigit)) {
            return false;
        }
        int checkValue = extractDigit(checkDigit);
        return checkValue == modResult;
    }
}