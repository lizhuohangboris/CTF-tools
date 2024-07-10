package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/ConstraintDef.class */
public abstract class ConstraintDef<C extends ConstraintDef<C, A>, A extends Annotation> extends AnnotationDef<C, A> {
    /* JADX INFO: Access modifiers changed from: protected */
    public ConstraintDef(Class<A> constraintType) {
        super(constraintType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConstraintDef(ConstraintDef<?, A> original) {
        super(original);
    }

    private C getThis() {
        return this;
    }

    public C message(String message) {
        addParameter(ConstraintHelper.MESSAGE, message);
        return getThis();
    }

    public C groups(Class<?>... groups) {
        addParameter(ConstraintHelper.GROUPS, groups);
        return getThis();
    }

    public C payload(Class<? extends Payload>... payload) {
        addParameter(ConstraintHelper.PAYLOAD, payload);
        return getThis();
    }
}