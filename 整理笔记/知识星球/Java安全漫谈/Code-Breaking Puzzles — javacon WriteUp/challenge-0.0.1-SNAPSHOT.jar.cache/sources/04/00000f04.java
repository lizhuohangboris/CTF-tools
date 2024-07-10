package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.Mod11Check;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/Mod11CheckDef.class */
public class Mod11CheckDef extends ConstraintDef<Mod11CheckDef, Mod11Check> {
    public Mod11CheckDef() {
        super(Mod11Check.class);
    }

    public Mod11CheckDef threshold(int threshold) {
        addParameter("threshold", Integer.valueOf(threshold));
        return this;
    }

    public Mod11CheckDef startIndex(int startIndex) {
        addParameter("startIndex", Integer.valueOf(startIndex));
        return this;
    }

    public Mod11CheckDef endIndex(int endIndex) {
        addParameter("endIndex", Integer.valueOf(endIndex));
        return this;
    }

    public Mod11CheckDef checkDigitIndex(int checkDigitIndex) {
        addParameter("checkDigitIndex", Integer.valueOf(checkDigitIndex));
        return this;
    }

    public Mod11CheckDef ignoreNonDigitCharacters(boolean ignoreNonDigitCharacters) {
        addParameter("ignoreNonDigitCharacters", Boolean.valueOf(ignoreNonDigitCharacters));
        return this;
    }

    public Mod11CheckDef treatCheck10As(char treatCheck10As) {
        addParameter("treatCheck10As", Character.valueOf(treatCheck10As));
        return this;
    }

    public Mod11CheckDef treatCheck11As(char treatCheck11As) {
        addParameter("treatCheck11As", Character.valueOf(treatCheck11As));
        return this;
    }

    public Mod11CheckDef processingDirection(Mod11Check.ProcessingDirection processingDirection) {
        addParameter("processingDirection", processingDirection);
        return this;
    }
}