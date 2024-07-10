package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.LuhnCheck;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/LuhnCheckDef.class */
public class LuhnCheckDef extends ConstraintDef<LuhnCheckDef, LuhnCheck> {
    public LuhnCheckDef() {
        super(LuhnCheck.class);
    }

    public LuhnCheckDef startIndex(int index) {
        addParameter("startIndex", Integer.valueOf(index));
        return this;
    }

    public LuhnCheckDef endIndex(int index) {
        addParameter("endIndex", Integer.valueOf(index));
        return this;
    }

    public LuhnCheckDef checkDigitIndex(int index) {
        addParameter("checkDigitIndex", Integer.valueOf(index));
        return this;
    }

    public LuhnCheckDef ignoreNonDigitCharacters(boolean ignore) {
        addParameter("ignoreNonDigitCharacters", Boolean.valueOf(ignore));
        return this;
    }
}