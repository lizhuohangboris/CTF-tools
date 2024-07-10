package org.hibernate.validator.internal.constraintvalidators.hv.pl;

import java.util.List;
import org.hibernate.validator.constraints.pl.NIP;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/pl/NIPValidator.class */
public class NIPValidator extends PolishNumberValidator<NIP> {
    private static final int[] WEIGHTS_NIP = {6, 5, 7, 2, 3, 4, 5, 6, 7};

    @Override // javax.validation.ConstraintValidator
    public void initialize(NIP constraintAnnotation) {
        super.initialize(0, Integer.MAX_VALUE, -1, true);
    }

    @Override // org.hibernate.validator.internal.constraintvalidators.hv.pl.PolishNumberValidator
    protected int[] getWeights(List<Integer> digits) {
        return WEIGHTS_NIP;
    }
}