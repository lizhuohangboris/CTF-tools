package org.hibernate.validator.cfg.context;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/context/TypeTarget.class */
public interface TypeTarget {
    <C> TypeConstraintMappingContext<C> type(Class<C> cls);
}