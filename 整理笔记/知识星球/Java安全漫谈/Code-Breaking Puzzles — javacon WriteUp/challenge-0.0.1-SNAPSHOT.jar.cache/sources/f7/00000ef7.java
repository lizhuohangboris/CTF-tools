package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Digits;
import org.hibernate.validator.cfg.ConstraintDef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/DigitsDef.class */
public class DigitsDef extends ConstraintDef<DigitsDef, Digits> {
    public DigitsDef() {
        super(Digits.class);
    }

    public DigitsDef integer(int integer) {
        addParameter("integer", Integer.valueOf(integer));
        return this;
    }

    public DigitsDef fraction(int fraction) {
        addParameter("fraction", Integer.valueOf(fraction));
        return this;
    }
}