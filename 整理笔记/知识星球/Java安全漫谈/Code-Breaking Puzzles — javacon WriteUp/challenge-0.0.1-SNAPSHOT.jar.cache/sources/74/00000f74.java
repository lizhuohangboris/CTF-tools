package org.hibernate.validator.internal.cfg.context;

import java.lang.reflect.Method;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/MethodConstraintMappingContextImpl.class */
public class MethodConstraintMappingContextImpl extends ExecutableConstraintMappingContextImpl implements MethodConstraintMappingContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MethodConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Method method) {
        super(typeContext, method);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public MethodConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.typeContext.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.executable, Boolean.valueOf(ignoreAnnotations));
        return this;
    }
}