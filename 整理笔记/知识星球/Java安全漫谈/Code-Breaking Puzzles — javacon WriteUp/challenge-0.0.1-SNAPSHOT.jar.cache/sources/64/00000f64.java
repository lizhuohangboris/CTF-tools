package org.hibernate.validator.engine;

import javax.validation.ConstraintViolation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/engine/HibernateConstraintViolation.class */
public interface HibernateConstraintViolation<T> extends ConstraintViolation<T> {
    <C> C getDynamicPayload(Class<C> cls);
}