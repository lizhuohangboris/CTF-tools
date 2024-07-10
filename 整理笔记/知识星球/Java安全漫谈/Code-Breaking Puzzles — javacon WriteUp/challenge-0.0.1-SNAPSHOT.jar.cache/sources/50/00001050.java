package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.ModCheck;
import org.hibernate.validator.internal.util.ModUtil;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/ModCheckValidator.class */
public class ModCheckValidator extends ModCheckBase implements ConstraintValidator<ModCheck, CharSequence> {
    private int multiplier;
    private ModCheck.ModType modType;

    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return super.isValid(charSequence, constraintValidatorContext);
    }

    @Override // javax.validation.ConstraintValidator
    public void initialize(ModCheck constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitPosition(), constraintAnnotation.ignoreNonDigitCharacters());
        this.modType = constraintAnnotation.modType();
        this.multiplier = constraintAnnotation.multiplier();
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult;
        int checkValue = extractDigit(checkDigit);
        if (this.modType.equals(ModCheck.ModType.MOD11)) {
            modResult = ModUtil.calculateMod11Check(digits, this.multiplier);
            if (modResult == 10 || modResult == 11) {
                modResult = 0;
            }
        } else {
            modResult = ModUtil.calculateLuhnMod10Check(digits);
        }
        return checkValue == modResult;
    }
}