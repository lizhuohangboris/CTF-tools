package org.hibernate.validator.internal.cfg.context;

import org.hibernate.validator.cfg.context.GroupConversionTargetContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/GroupConversionTargetContextImpl.class */
public class GroupConversionTargetContextImpl<C> implements GroupConversionTargetContext<C> {
    private final C cascadableContext;
    private final Class<?> from;
    private final CascadableConstraintMappingContextImplBase<?> target;

    public GroupConversionTargetContextImpl(Class<?> from, C cascadableContext, CascadableConstraintMappingContextImplBase<?> target) {
        this.from = from;
        this.cascadableContext = cascadableContext;
        this.target = target;
    }

    @Override // org.hibernate.validator.cfg.context.GroupConversionTargetContext
    public C to(Class<?> to) {
        this.target.addGroupConversion(this.from, to);
        return this.cascadableContext;
    }
}