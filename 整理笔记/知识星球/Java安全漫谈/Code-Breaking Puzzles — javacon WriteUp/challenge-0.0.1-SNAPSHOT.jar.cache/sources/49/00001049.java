package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.function.Function;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.ISBN;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/ISBNValidator.class */
public class ISBNValidator implements ConstraintValidator<ISBN, CharSequence> {
    private static Pattern NOT_DIGITS_OR_NOT_X = Pattern.compile("[^\\dX]");
    private int length;
    private Function<String, Boolean> checkChecksumFunction;

    @Override // javax.validation.ConstraintValidator
    public void initialize(ISBN constraintAnnotation) {
        switch (constraintAnnotation.type()) {
            case ISBN_10:
                this.length = 10;
                this.checkChecksumFunction = this::checkChecksumISBN10;
                return;
            case ISBN_13:
                this.length = 13;
                this.checkChecksumFunction = this::checkChecksumISBN13;
                return;
            default:
                return;
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return true;
        }
        String digits = NOT_DIGITS_OR_NOT_X.matcher(isbn).replaceAll("");
        if (digits.length() != this.length) {
            return false;
        }
        return this.checkChecksumFunction.apply(digits).booleanValue();
    }

    private boolean checkChecksumISBN10(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            sum += (isbn.charAt(i) - '0') * (i + 1);
        }
        char checkSum = isbn.charAt(9);
        return sum % 11 == (checkSum == 'X' ? 10 : checkSum - '0');
    }

    private boolean checkChecksumISBN13(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            sum += (isbn.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        char checkSum = isbn.charAt(12);
        return 10 - (sum % 10) == checkSum - '0';
    }
}