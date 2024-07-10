package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.Cascadable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/Cascadable.class */
public interface Cascadable<C extends Cascadable<C>> {
    C valid();

    GroupConversionTargetContext<C> convertGroup(Class<?> cls);
}