package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/GenericConstraintDef.class */
public class GenericConstraintDef<A extends Annotation> extends ConstraintDef<GenericConstraintDef<A>, A> {
    public GenericConstraintDef(Class<A> constraintType) {
        super(constraintType);
    }

    public GenericConstraintDef<A> param(String key, Object value) {
        addParameter(key, value);
        return this;
    }
}