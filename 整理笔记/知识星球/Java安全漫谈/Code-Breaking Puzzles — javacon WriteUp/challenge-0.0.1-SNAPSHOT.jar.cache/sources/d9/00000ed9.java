package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.Constrainable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/Constrainable.class */
public interface Constrainable<C extends Constrainable<C>> {
    C constraint(ConstraintDef<?, ?> constraintDef);
}