package org.hibernate.validator.internal.cfg.context;

import java.lang.reflect.Constructor;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ConstructorConstraintMappingContextImpl.class */
public class ConstructorConstraintMappingContextImpl extends ExecutableConstraintMappingContextImpl implements ConstructorConstraintMappingContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> ConstructorConstraintMappingContextImpl(TypeConstraintMappingContextImpl<T> typeContext, Constructor<T> constructor) {
        super(typeContext, constructor);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public ConstructorConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.typeContext.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.executable, Boolean.valueOf(ignoreAnnotations));
        return this;
    }
}