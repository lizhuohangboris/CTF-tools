package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ISBN;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/ISBNDef.class */
public class ISBNDef extends ConstraintDef<ISBNDef, ISBN> {
    public ISBNDef() {
        super(ISBN.class);
    }

    public ISBNDef type(ISBN.Type type) {
        addParameter("type", type);
        return this;
    }
}