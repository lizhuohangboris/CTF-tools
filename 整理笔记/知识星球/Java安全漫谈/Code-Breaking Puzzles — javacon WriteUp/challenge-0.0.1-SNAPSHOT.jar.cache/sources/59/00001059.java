package org.hibernate.validator.internal.constraintvalidators.hv.br;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.internal.constraintvalidators.hv.Mod11CheckValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/br/CNPJValidator.class */
public class CNPJValidator implements ConstraintValidator<CNPJ, CharSequence> {
    private static final Pattern DIGITS_ONLY = Pattern.compile("\\d+");
    private final Mod11CheckValidator withSeparatorMod11Validator1 = new Mod11CheckValidator();
    private final Mod11CheckValidator withSeparatorMod11Validator2 = new Mod11CheckValidator();
    private final Mod11CheckValidator withoutSeparatorMod11Validator1 = new Mod11CheckValidator();
    private final Mod11CheckValidator withoutSeparatorMod11Validator2 = new Mod11CheckValidator();

    @Override // javax.validation.ConstraintValidator
    public void initialize(CNPJ constraintAnnotation) {
        this.withSeparatorMod11Validator1.initialize(0, 14, 16, true, 9, '0', '0', Mod11Check.ProcessingDirection.RIGHT_TO_LEFT, new int[0]);
        this.withSeparatorMod11Validator2.initialize(0, 16, 17, true, 9, '0', '0', Mod11Check.ProcessingDirection.RIGHT_TO_LEFT, new int[0]);
        this.withoutSeparatorMod11Validator1.initialize(0, 11, 12, true, 9, '0', '0', Mod11Check.ProcessingDirection.RIGHT_TO_LEFT, new int[0]);
        this.withoutSeparatorMod11Validator2.initialize(0, 12, 13, true, 9, '0', '0', Mod11Check.ProcessingDirection.RIGHT_TO_LEFT, new int[0]);
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return DIGITS_ONLY.matcher(value).matches() ? this.withoutSeparatorMod11Validator1.isValid(value, context) && this.withoutSeparatorMod11Validator2.isValid(value, context) : this.withSeparatorMod11Validator1.isValid(value, context) && this.withSeparatorMod11Validator2.isValid(value, context);
    }
}