package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Min;
import org.hibernate.validator.cfg.ConstraintDef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/MinDef.class */
public class MinDef extends ConstraintDef<MinDef, Min> {
    public MinDef() {
        super(Min.class);
    }

    public MinDef value(long min) {
        addParameter("value", Long.valueOf(min));
        return this;
    }
}