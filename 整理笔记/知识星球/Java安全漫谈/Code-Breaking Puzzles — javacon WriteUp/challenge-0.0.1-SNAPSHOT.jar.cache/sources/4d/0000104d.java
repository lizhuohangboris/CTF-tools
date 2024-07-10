package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.internal.util.ModUtil;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/Mod10CheckValidator.class */
public class Mod10CheckValidator extends ModCheckBase implements ConstraintValidator<Mod10Check, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int multiplier;
    private int weight;

    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        return super.isValid(charSequence, constraintValidatorContext);
    }

    @Override // javax.validation.ConstraintValidator
    public void initialize(Mod10Check constraintAnnotation) {
        super.initialize(constraintAnnotation.startIndex(), constraintAnnotation.endIndex(), constraintAnnotation.checkDigitIndex(), constraintAnnotation.ignoreNonDigitCharacters());
        this.multiplier = constraintAnnotation.multiplier();
        this.weight = constraintAnnotation.weight();
        if (this.multiplier < 0) {
            throw LOG.getMultiplierCannotBeNegativeException(this.multiplier);
        }
        if (this.weight < 0) {
            throw LOG.getWeightCannotBeNegativeException(this.weight);
        }
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.ModCheckBase
    public boolean isCheckDigitValid(List<Integer> digits, char checkDigit) {
        int modResult = ModUtil.calculateMod10Check(digits, this.multiplier, this.weight);
        if (!Character.isDigit(checkDigit)) {
            return false;
        }
        int checkValue = extractDigit(checkDigit);
        return checkValue == modResult;
    }
}