package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.Mod10Check;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/Mod10CheckDef.class */
public class Mod10CheckDef extends ConstraintDef<Mod10CheckDef, Mod10Check> {
    public Mod10CheckDef() {
        super(Mod10Check.class);
    }

    public Mod10CheckDef multiplier(int multiplier) {
        addParameter("multiplier", Integer.valueOf(multiplier));
        return this;
    }

    public Mod10CheckDef weight(int weight) {
        addParameter("weight", Integer.valueOf(weight));
        return this;
    }

    public Mod10CheckDef startIndex(int startIndex) {
        addParameter("startIndex", Integer.valueOf(startIndex));
        return this;
    }

    public Mod10CheckDef endIndex(int endIndex) {
        addParameter("endIndex", Integer.valueOf(endIndex));
        return this;
    }

    public Mod10CheckDef checkDigitIndex(int checkDigitIndex) {
        addParameter("checkDigitIndex", Integer.valueOf(checkDigitIndex));
        return this;
    }

    public Mod10CheckDef ignoreNonDigitCharacters(boolean ignoreNonDigitCharacters) {
        addParameter("ignoreNonDigitCharacters", Boolean.valueOf(ignoreNonDigitCharacters));
        return this;
    }
}