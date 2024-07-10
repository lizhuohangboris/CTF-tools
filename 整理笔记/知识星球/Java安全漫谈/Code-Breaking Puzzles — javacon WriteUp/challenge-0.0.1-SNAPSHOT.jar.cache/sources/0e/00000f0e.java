package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/PatternDef.class */
public class PatternDef extends ConstraintDef<PatternDef, Pattern> {
    public PatternDef() {
        super(Pattern.class);
    }

    public PatternDef flags(Pattern.Flag[] flags) {
        addParameter("flags", flags);
        return this;
    }

    public PatternDef regexp(String regexp) {
        addParameter("regexp", regexp);
        return this;
    }
}