package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.cfg.context.ContainerElementConstraintMappingContext;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/cfg/context/PropertyConstraintMappingContextImpl.class */
public final class PropertyConstraintMappingContextImpl extends CascadableConstraintMappingContextImplBase<PropertyConstraintMappingContext> implements PropertyConstraintMappingContext {
    private final TypeConstraintMappingContextImpl<?> typeContext;
    private final Member member;
    private final ConstraintLocation location;

    @Override // org.hibernate.validator.cfg.context.Constrainable
    public /* bridge */ /* synthetic */ PropertyConstraintMappingContext constraint(ConstraintDef constraintDef) {
        return constraint((ConstraintDef<?, ?>) constraintDef);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertyConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Member member) {
        super(typeContext.getConstraintMapping(), ReflectionHelper.typeOf(member));
        this.typeContext = typeContext;
        this.member = member;
        if (member instanceof Field) {
            this.location = ConstraintLocation.forField((Field) member);
        } else {
            this.location = ConstraintLocation.forGetter((Method) member);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.cfg.context.CascadableConstraintMappingContextImplBase
    /* renamed from: getThis */
    public PropertyConstraintMappingContext getThis2() {
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.cfg.context.Constrainable
    public PropertyConstraintMappingContext constraint(ConstraintDef<?, ?> definition) {
        if (this.member instanceof Field) {
            super.addConstraint(ConfiguredConstraint.forProperty(definition, this.member));
        } else {
            super.addConstraint(ConfiguredConstraint.forExecutable(definition, (Method) this.member));
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.cfg.context.AnnotationProcessingOptions
    public PropertyConstraintMappingContext ignoreAnnotations() {
        return ignoreAnnotations(true);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.cfg.context.AnnotationIgnoreOptions
    public PropertyConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.member, Boolean.valueOf(ignoreAnnotations));
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

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType() {
        return super.containerElement(this, this.typeContext, this.location);
    }

    @Override // org.hibernate.validator.cfg.context.ContainerElementTarget
    public ContainerElementConstraintMappingContext containerElementType(int index, int... nestedIndexes) {
        return super.containerElement(this, this.typeContext, this.location, index, nestedIndexes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstrainedElement build(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
        if (this.member instanceof Field) {
            return new ConstrainedField(ConfigurationSource.API, (Field) this.member, getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getCascadingMetaDataBuilder());
        }
        return new ConstrainedExecutable(ConfigurationSource.API, (Executable) this.member, getConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getTypeArgumentConstraints(constraintHelper, typeResolutionHelper, valueExtractorManager), getCascadingMetaDataBuilder());
    }

    @Override // org.hibernate.validator.internal.cfg.context.ConstraintMappingContextImplBase
    protected ConstraintDescriptorImpl.ConstraintType getConstraintType() {
        return ConstraintDescriptorImpl.ConstraintType.GENERIC;
    }
}