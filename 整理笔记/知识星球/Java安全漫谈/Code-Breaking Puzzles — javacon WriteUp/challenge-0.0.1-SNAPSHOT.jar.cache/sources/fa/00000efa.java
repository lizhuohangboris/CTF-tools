package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.EAN;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/EANDef.class */
public class EANDef extends ConstraintDef<EANDef, EAN> {
    public EANDef() {
        super(EAN.class);
    }

    public EANDef type(EAN.Type type) {
        addParameter("type", type);
        return this;
    }
}