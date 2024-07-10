package org.hibernate.validator.internal.cfg.context;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ReflectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ReturnValueConstraintMappingContextImpl.class */
public final class ReturnValueConstraintMappingContextImpl extends CascadableConstraintMappingContextImplBase<ReturnValueConstraintMappingContext> implements ReturnValueConstraintMappingContext {
    private final ExecutableConstraintMappingContextImpl executableContext;

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public /* bridge */ /* synthetic */ ReturnValueConstraintMappingContext constraint(ConstraintDef constraintDef) {
        return constraint((ConstraintDef<?, ?>) constraintDef);
    }

    public ReturnValueConstraintMappingContextImpl(ExecutableConstraintMappingContextImpl executableContext) {
        super(executableContext.getTypeContext().getConstraintMapping(), ReflectionHelper.typeOf(executableContext.getExecutable()));
        this.executableContext = executableContext;
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public ReturnValueConstraintMappingContext getThis() {
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public ReturnValueConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        super.addConstraint(ConfiguredConstraint.forExecutable(definition, this.executableContext.getExecutable()));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public ReturnValueConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsForReturnValue(this.executableContext.getExecutable(), Boolean.valueOf(ignoreAnnotations));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.ParameterTarget
    public ParameterConstraintMappingContext parameter(int index) {
        return this.executableContext.parameter(index);
    }

    @Override // org.hibernate.validator.cfg.context.CrossParameterTarget
    public CrossParameterConstraintMappingContext crossParameter() {
        return this.executableContext.crossParameter();
    }

    @Override // org.hibernate.validator.cfg.context.MethodTarget
    public MethodConstraintMappingContext method(String name, Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().method(name, parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ConstructorTarget
    public ConstructorConstraintMappingContext constructor(Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().constructor(parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType() {
        return super.containerElement(this, this.executableContext.getTypeContext(), ConstraintLocation.forReturnValue(this.executableContext.getExecutable()));
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType(int index, int... nestedIndexes) {
        return super.containerElement(this, this.executableContext.getTypeContext(), ConstraintLocation.forReturnValue(this.executableContext.getExecutable()), index, nestedIndexes);
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }
}