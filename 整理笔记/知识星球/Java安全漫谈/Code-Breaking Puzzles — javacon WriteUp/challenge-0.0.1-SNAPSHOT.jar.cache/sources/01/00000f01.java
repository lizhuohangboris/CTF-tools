package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Max;
import org.hibernate.validator.cfg.ConstraintDef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/MaxDef.class */
public class MaxDef extends ConstraintDef<MaxDef, Max> {
    public MaxDef() {
        super(Max.class);
    }

    public MaxDef value(long max) {
        addParameter("value", Long.valueOf(max));
        return this;
    }
}