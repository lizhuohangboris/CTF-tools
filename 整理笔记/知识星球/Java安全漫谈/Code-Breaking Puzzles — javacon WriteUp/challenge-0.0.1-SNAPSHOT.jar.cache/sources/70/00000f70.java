package org.hibernate.validator.internal.cfg.context;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/CrossParameterConstraintMappingContextImpl.class */
public final class CrossParameterConstraintMappingContextImpl extends ConstraintMappingContextImplBase implements CrossParameterConstraintMappingContext {
    private final ExecutableConstraintMappingContextImpl executableContext;

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public /* bridge */ /* synthetic */ CrossParameterConstraintMappingContext constraint(ConstraintDef constraintDef) {
        return constraint((ConstraintDef<?, ?>) constraintDef);
    }

    public CrossParameterConstraintMappingContextImpl(ExecutableConstraintMappingContextImpl executableContext) {
        super(executableContext.getTypeContext().getConstraintMapping());
        this.executableContext = executableContext;
    }

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public CrossParameterConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        super.addConstraint(ConfiguredConstraint.forCrossParameter(definition, this.executableContext.getExecutable()));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public CrossParameterConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsForCrossParameterConstraint(this.executableContext.getExecutable(), Boolean.valueOf(ignoreAnnotations));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.ParameterTarget
    public ParameterConstraintMappingContext parameter(int index) {
        return this.executableContext.parameter(index);
    }

    @Override // org.hibernate.validator.cfg.context.MethodTarget
    public MethodConstraintMappingContext method(String name, Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().method(name, parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ConstructorTarget
    public ConstructorConstraintMappingContext constructor(Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().constructor(parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ReturnValueTarget
    public ReturnValueConstraintMappingContext returnValue() {
        return this.executableContext.returnValue();
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER;
    }
}