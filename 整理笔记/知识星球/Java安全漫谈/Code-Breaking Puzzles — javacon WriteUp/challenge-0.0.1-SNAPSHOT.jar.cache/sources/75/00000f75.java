package org.hibernate.validator.internal.cfg.context;

import java.lang.reflect.Type;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ParameterConstraintMappingContextImpl.class */
public final class ParameterConstraintMappingContextImpl extends CascadableConstraintMappingContextImplBase<ParameterConstraintMappingContext> implements ParameterConstraintMappingContext {
    private final ExecutableConstraintMappingContextImpl executableContext;
    private final int parameterIndex;

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public /* bridge */ /* synthetic */ ParameterConstraintMappingContext constraint(ConstraintDef constraintDef) {
        return constraint((ConstraintDef<?, ?>) constraintDef);
    }

    public ParameterConstraintMappingContextImpl(ExecutableConstraintMappingContextImpl executableContext, int parameterIndex) {
        super(executableContext.getTypeContext().getConstraintMapping(), executableContext.executable.getGenericParameterTypes()[parameterIndex]);
        this.executableContext = executableContext;
        this.parameterIndex = parameterIndex;
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public ParameterConstraintMappingContext getThis() {
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public ParameterConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        super.addConstraint(ConfiguredConstraint.forParameter(definition, this.executableContext.getExecutable(), this.parameterIndex));
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public ParameterConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnParameter(this.executableContext.getExecutable(), this.parameterIndex, Boolean.valueOf(ignoreAnnotations));
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

    @Override // org.hibernate.validator.cfg.context.ReturnValueTarget
    public ReturnValueConstraintMappingContext returnValue() {
        return this.executableContext.returnValue();
    }

    @Override // org.hibernate.validator.cfg.context.ConstructorTarget
    public ConstructorConstraintMappingContext constructor(Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().constructor(parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.MethodTarget
    public MethodConstraintMappingContext method(String name, Class<?>... parameterTypes) {
        return this.executableContext.getTypeContext().method(name, parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType() {
        return super.containerElement(this, this.executableContext.getTypeContext(), ConstraintLocation.forParameter(this.executableContext.getExecutable(), this.parameterIndex));
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType(int index, int... nestedIndexes) {
        return super.containerElement(this, this.executableContext.getTypeContext(), ConstraintLocation.forParameter(this.executableContext.getExecutable(), this.parameterIndex), index, nestedIndexes);
    }

    public ConstrainedParameter build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        Type parameterType = ReflectionHelper.typeOf(this.executableContext.getExecutable(), this.parameterIndex);
        return new ConstrainedParameter(ConfigurationSource.API, this.executableContext.getExecutable(), parameterType, this.parameterIndex, getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getCascadingMetaDataBuilder());
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }
}