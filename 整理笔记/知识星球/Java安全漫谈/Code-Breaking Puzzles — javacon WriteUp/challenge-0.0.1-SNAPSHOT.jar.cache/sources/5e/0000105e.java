package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.util.List;
import org.hibernate.validator.constraints.pl.REGON;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/pl/REGONValidator.class */
public class REGONValidator extends PolishNumberValidator<REGON> {
    private static final int[] WEIGHTS_REGON_14 = {2, 4, 8, 5, 0, 9, 7, 3, 6, 1, 2, 4, 8};
    private static final int[] WEIGHTS_REGON_9 = {8, 9, 2, 3, 4, 5, 6, 7};

    @Override // javax.validation.ConstraintValidator
    public void initialize(REGON constraintAnnotation) {
        super.initialize(0, Integer.MAX_VALUE, -1, false);
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.pl.PolishNumberValidator
    protected int[] getWeights(List<Integer> digits) {
        if (digits.size() == 8) {
            return WEIGHTS_REGON_9;
        }
        if (digits.size() == 13) {
            return WEIGHTS_REGON_14;
        }
        return new int[0];
    }
}