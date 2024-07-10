package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ParameterDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ParameterMetaData.class */
public class ParameterMetaData extends AbstractConstraintMetaData implements Cascadable {
    private final int index;
    private final CascadingMetaData cascadingMetaData;

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public /* bridge */ /* synthetic */ ElementDescriptor asDescriptor(boolean z, List list) {
        return asDescriptor(z, (List<Class<?>>) list);
    }

    private ParameterMetaData(int index, String name, Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData) {
        super(name, type, constraints, containerElementsConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), (constraints.isEmpty() && containerElementsConstraints.isEmpty() && !cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) ? false : true);
        this.index = index;
        this.cascadingMetaData = cascadingMetaData;
    }

    public int getIndex() {
        return this.index;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public ElementType getElementType() {
        return ElementType.PARAMETER;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ParameterDescriptor asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ParameterDescriptorImpl(getType(), this.index, getName(), asDescriptors(getDirectConstraints()), asContainerElementTypeDescriptors(getContainerElementsConstraints(), this.cascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), this.cascadingMetaData.isCascading(), defaultGroupSequenceRedefined, defaultGroupSequence, this.cascadingMetaData.getGroupConversionDescriptors());
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public Object getValue(Object parent) {
        return ((Object[]) parent)[getIndex()];
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public Type getCascadableType() {
        return getType();
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public void appendTo(PathImpl path) {
        path.addParameterNode(getName(), getIndex());
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public CascadingMetaData getCascadingMetaData() {
        return this.cascadingMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ElementKind getKind() {
        return ElementKind.PARAMETER;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ParameterMetaData$Builder.class */
    public static class Builder extends MetaDataBuilder {
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final Type parameterType;
        private final int parameterIndex;
        private Executable executableForNameRetrieval;
        private CascadingMetaDataBuilder cascadingMetaDataBuilder;

        public Builder(Class<?> beanClass, ConstrainedParameter constrainedParameter, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.parameterNameProvider = parameterNameProvider;
            this.parameterType = constrainedParameter.getType();
            this.parameterIndex = constrainedParameter.getIndex();
            add(constrainedParameter);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public boolean accepts(ConstrainedElement constrainedElement) {
            return constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.PARAMETER && ((ConstrainedParameter) constrainedElement).getIndex() == this.parameterIndex;
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public void add(ConstrainedElement constrainedElement) {
            super.add(constrainedElement);
            ConstrainedParameter newConstrainedParameter = (ConstrainedParameter) constrainedElement;
            if (this.cascadingMetaDataBuilder == null) {
                this.cascadingMetaDataBuilder = newConstrainedParameter.getCascadingMetaDataBuilder();
            } else {
                this.cascadingMetaDataBuilder = this.cascadingMetaDataBuilder.merge(newConstrainedParameter.getCascadingMetaDataBuilder());
            }
            if (this.executableForNameRetrieval == null || newConstrainedParameter.getExecutable().getDeclaringClass().isAssignableFrom(this.executableForNameRetrieval.getDeclaringClass())) {
                this.executableForNameRetrieval = newConstrainedParameter.getExecutable();
            }
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public ParameterMetaData build() {
            return new ParameterMetaData(this.parameterIndex, this.parameterNameProvider.getParameterNames(this.executableForNameRetrieval).get(this.parameterIndex), this.parameterType, adaptOriginsAndImplicitGroups(getDirectConstraints()), adaptOriginsAndImplicitGroups(getContainerElementConstraints()), this.cascadingMetaDataBuilder.build(this.valueExtractorManager, this.executableForNameRetrieval));
        }
    }
}