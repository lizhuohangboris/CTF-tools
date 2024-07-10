package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.GroupConversionTargetContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterConstraintMappingContext;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
import org.hibernate.validator.cfg.context.ReturnValueTarget;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/ContainerElementConstraintMappingContextImpl.class */
public class ContainerElementConstraintMappingContextImpl extends CascadableConstraintMappingContextImplBase<ContainerElementConstraintMappingContext> implements ContainerElementConstraintMappingContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final TypeConstraintMappingContextImpl<?> typeContext;
    private final ContainerElementTarget parentContainerElementTarget;
    private final ConstraintLocation parentLocation;
    private final Type configuredType;
    private final int index;
    private final TypeVariable<?> typeParameter;
    protected final Map<Integer, ContainerElementConstraintMappingContextImpl> nestedContainerElementContexts;
    private final Set<ConfiguredConstraint<?>> constraints;

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public /* bridge */ /* synthetic */ boolean isCascading() {
        return super.isCascading();
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public /* bridge */ /* synthetic */ ContainerElementConstraintMappingContext containerElement(ContainerElementTarget containerElementTarget, TypeConstraintMappingContextImpl typeConstraintMappingContextImpl, ConstraintLocation constraintLocation, int i, int[] iArr) {
        return super.containerElement(containerElementTarget, typeConstraintMappingContextImpl, constraintLocation, i, iArr);
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public /* bridge */ /* synthetic */ ContainerElementConstraintMappingContext containerElement(ContainerElementTarget containerElementTarget, TypeConstraintMappingContextImpl typeConstraintMappingContextImpl, ConstraintLocation constraintLocation) {
        return super.containerElement(containerElementTarget, typeConstraintMappingContextImpl, constraintLocation);
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase, org.hibernate.validator.cfg.context.Cascadable
    public /* bridge */ /* synthetic */ GroupConversionTargetContext<ContainerElementConstraintMappingContext> convertGroup(Class cls) {
        return super.convertGroup(cls);
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public /* bridge */ /* synthetic */ void addGroupConversion(Class cls, Class cls2) {
        super.addGroupConversion(cls, cls2);
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintContextImplBase, org.hibernate.validator.cfg.context.ConstraintDefinitionTarget
    public /* bridge */ /* synthetic */ ConstraintDefinitionContext constraintDefinition(Class cls) {
        return super.constraintDefinition(cls);
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintContextImplBase, org.hibernate.validator.cfg.context.TypeTarget
    public /* bridge */ /* synthetic */ TypeConstraintMappingContext type(Class cls) {
        return super.type(cls);
    }

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public /* bridge */ /* synthetic */ ContainerElementConstraintMappingContext constraint(ConstraintDef constraintDef) {
        return constraint((ConstraintDef<?, ?>) constraintDef);
    }

    public ContainerElementConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, ContainerElementTarget parentContainerElementTarget, ConstraintLocation parentLocation, int index) {
        super(typeContext.getConstraintMapping(), parentLocation.getTypeForValidatorResolution());
        this.typeContext = typeContext;
        this.parentContainerElementTarget = parentContainerElementTarget;
        this.parentLocation = parentLocation;
        this.configuredType = parentLocation.getTypeForValidatorResolution();
        if (TypeHelper.isArray(this.configuredType)) {
            throw LOG.getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(this.configuredType);
        }
        if (this.configuredType instanceof ParameterizedType) {
            TypeVariable<?>[] typeParameters = ReflectionHelper.getClassFromType(this.configuredType).getTypeParameters();
            if (index > typeParameters.length - 1) {
                throw LOG.getInvalidTypeArgumentIndexException(this.configuredType, index);
            }
            this.typeParameter = typeParameters[index];
        } else {
            this.typeParameter = new ArrayElement(this.configuredType);
        }
        this.index = index;
        this.constraints = new HashSet();
        this.nestedContainerElementContexts = new HashMap();
    }

    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    public ContainerElementConstraintMappingContext getThis() {
        return this;
    }

    @Override // org.hibernate.validator.cfg.context.PropertyTarget
    public PropertyConstraintMappingContext property(String property, ElementType elementType) {
        return this.typeContext.property(property, elementType);
    }

    @Override // org.hibernate.validator.cfg.context.ConstructorTarget
    public ConstructorConstraintMappingContext constructor(Class<?>... parameterTypes) {
        return this.typeContext.constructor(parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.MethodTarget
    public MethodConstraintMappingContext method(String name, Class<?>... parameterTypes) {
        return this.typeContext.method(name, parameterTypes);
    }

    @Override // org.hibernate.validator.cfg.context.ParameterTarget
    public ParameterConstraintMappingContext parameter(int index) {
        if (this.parentContainerElementTarget instanceof ParameterTarget) {
            return ((ParameterTarget) this.parentContainerElementTarget).parameter(index);
        }
        throw LOG.getParameterIsNotAValidCallException();
    }

    @Override // org.hibernate.validator.cfg.context.ReturnValueTarget
    public ReturnValueConstraintMappingContext returnValue() {
        if (this.parentContainerElementTarget instanceof ReturnValueTarget) {
            return ((ReturnValueTarget) this.parentContainerElementTarget).returnValue();
        }
        throw LOG.getReturnValueIsNotAValidCallException();
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType() {
        return this.parentContainerElementTarget.containerElementType(0, new int[0]);
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType(int index, int... nestedIndexes) {
        return this.parentContainerElementTarget.containerElementType(index, nestedIndexes);
    }

    public ContainerElementConstraintMappingContext nestedContainerElement(int[] nestedIndexes) {
        if (!(this.configuredType instanceof ParameterizedType) && !TypeHelper.isArray(this.configuredType)) {
            throw LOG.getTypeIsNotAParameterizedNorArrayTypeException(this.configuredType);
        }
        ContainerElementConstraintMappingContextImpl nestedContext = this.nestedContainerElementContexts.get(Integer.valueOf(nestedIndexes[0]));
        if (nestedContext == null) {
            nestedContext = new ContainerElementConstraintMappingContextImpl(this.typeContext, this.parentContainerElementTarget, ConstraintLocation.forTypeArgument(this.parentLocation, this.typeParameter, getContainerElementType()), nestedIndexes[0]);
            this.nestedContainerElementContexts.put(Integer.valueOf(nestedIndexes[0]), nestedContext);
        }
        if (nestedIndexes.length > 1) {
            return nestedContext.nestedContainerElement(Arrays.copyOfRange(nestedIndexes, 1, nestedIndexes.length));
        }
        return nestedContext;
    }

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public ContainerElementConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        this.constraints.add(ConfiguredConstraint.forTypeArgument(definition, this.parentLocation, this.typeParameter, getContainerElementType()));
        return this;
    }

    private Type getContainerElementType() {
        if (this.configuredType instanceof ParameterizedType) {
            return ((ParameterizedType) this.configuredType).getActualTypeArguments()[this.index];
        }
        return TypeHelper.getComponentType(this.configuredType);
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }

    public CascadingMetaDataBuilder getContainerElementCascadingMetaDataBuilder() {
        return new CascadingMetaDataBuilder(this.parentLocation.getTypeForValidatorResolution(), this.typeParameter, this.isCascading, (Map) this.nestedContainerElementContexts.values().stream().map((v0) -> {
            return v0.getContainerElementCascadingMetaDataBuilder();
        }).collect(Collectors.toMap((v0) -> {
            return v0.getTypeParameter();
        }, Function.identity())), this.groupConversions);
    }

    public Set<MetaConstraint<?>> build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        return (Set) Stream.concat(this.constraints.stream().map(c -> {
            return asMetaConstraint(c, constraintHelper, typeResolutionHelper, valueExtractorManager);
        }), this.nestedContainerElementContexts.values().stream().map(c2 -> {
            return c2.build(constraintHelper, typeResolutionHelper, valueExtractorManager);
        }).flatMap((v0) -> {
            return v0.stream();
        })).collect(Collectors.toSet());
    }

    private <A extends Annotation> MetaConstraint<A> asMetaConstraint(ConfiguredConstraint<A> config, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        ConstraintDescriptorImpl<A> constraintDescriptor = new ConstraintDescriptorImpl<>(constraintHelper, config.getLocation().getMember(), config.createAnnotationDescriptor(), config.getElementType(), getConstraintType());
        return MetaConstraints.create(typeResolutionHelper, valueExtractorManager, constraintDescriptor, config.getLocation());
    }

    public String toString() {
        return "TypeArgumentConstraintMappingContextImpl [configuredType=" + StringHelper.toShortString(this.configuredType) + ", typeParameter=" + this.typeParameter + "]";
    }
}