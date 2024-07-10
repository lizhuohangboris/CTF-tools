package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ContainerCascadingMetaData.class */
public class ContainerCascadingMetaData implements CascadingMetaData {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Type enclosingType;
    private final TypeVariable<?> typeParameter;
    private final Class<?> declaredContainerClass;
    private final TypeVariable<?> declaredTypeParameter;
    private final Integer declaredTypeParameterIndex;
    private final List<ContainerCascadingMetaData> containerElementTypesCascadingMetaData;
    private final boolean cascading;
    private GroupConversionHelper groupConversionHelper;
    private final boolean hasContainerElementsMarkedForCascading;
    private final Set<ValueExtractorDescriptor> valueExtractorCandidates;

    public static ContainerCascadingMetaData of(ValueExtractorManager valueExtractorManager, CascadingMetaDataBuilder cascadingMetaDataBuilder, Object context) {
        return new ContainerCascadingMetaData(valueExtractorManager, cascadingMetaDataBuilder);
    }

    private ContainerCascadingMetaData(ValueExtractorManager valueExtractorManager, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        this(valueExtractorManager, cascadingMetaDataBuilder.getEnclosingType(), cascadingMetaDataBuilder.getTypeParameter(), cascadingMetaDataBuilder.getDeclaredContainerClass(), cascadingMetaDataBuilder.getDeclaredTypeParameter(), (List) cascadingMetaDataBuilder.getContainerElementTypesCascadingMetaData().entrySet().stream().map(entry -> {
            return new ContainerCascadingMetaData(valueExtractorManager, (CascadingMetaDataBuilder) entry.getValue());
        }).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList)), cascadingMetaDataBuilder.isCascading(), GroupConversionHelper.of(cascadingMetaDataBuilder.getGroupConversions()), cascadingMetaDataBuilder.isMarkedForCascadingOnAnnotatedObjectOrContainerElements());
    }

    private ContainerCascadingMetaData(ValueExtractorManager valueExtractorManager, Type enclosingType, TypeVariable<?> typeParameter, Class<?> declaredContainerClass, TypeVariable<?> declaredTypeParameter, List<ContainerCascadingMetaData> containerElementTypesCascadingMetaData, boolean cascading, GroupConversionHelper groupConversionHelper, boolean markedForCascadingOnContainerElements) {
        this.enclosingType = enclosingType;
        this.typeParameter = typeParameter;
        this.declaredContainerClass = declaredContainerClass;
        this.declaredTypeParameter = declaredTypeParameter;
        this.declaredTypeParameterIndex = TypeVariables.getTypeParameterIndex(declaredTypeParameter);
        this.containerElementTypesCascadingMetaData = containerElementTypesCascadingMetaData;
        this.cascading = cascading;
        this.groupConversionHelper = groupConversionHelper;
        this.hasContainerElementsMarkedForCascading = markedForCascadingOnContainerElements;
        if (TypeVariables.isAnnotatedObject(this.typeParameter) || !markedForCascadingOnContainerElements) {
            this.valueExtractorCandidates = Collections.emptySet();
            return;
        }
        this.valueExtractorCandidates = CollectionHelper.toImmutableSet(valueExtractorManager.getResolver().getValueExtractorCandidatesForCascadedValidation(this.enclosingType, this.typeParameter));
        if (this.valueExtractorCandidates.size() == 0) {
            throw LOG.getNoValueExtractorFoundForTypeException(this.declaredContainerClass, this.declaredTypeParameter);
        }
    }

    public ContainerCascadingMetaData(Type enclosingType, List<ContainerCascadingMetaData> containerElementTypesCascadingMetaData, GroupConversionHelper groupConversionHelper, Set<ValueExtractorDescriptor> valueExtractorCandidates) {
        this.enclosingType = enclosingType;
        this.typeParameter = AnnotatedObject.INSTANCE;
        this.declaredContainerClass = null;
        this.declaredTypeParameter = null;
        this.declaredTypeParameterIndex = null;
        this.containerElementTypesCascadingMetaData = containerElementTypesCascadingMetaData;
        this.cascading = true;
        this.groupConversionHelper = groupConversionHelper;
        this.hasContainerElementsMarkedForCascading = true;
        this.valueExtractorCandidates = valueExtractorCandidates;
    }

    public ContainerCascadingMetaData(Type enclosingType, TypeVariable<?> typeParameter, Class<?> declaredContainerClass, TypeVariable<?> declaredTypeParameter, GroupConversionHelper groupConversionHelper) {
        this.enclosingType = enclosingType;
        this.typeParameter = typeParameter;
        this.declaredContainerClass = declaredContainerClass;
        this.declaredTypeParameter = declaredTypeParameter;
        this.declaredTypeParameterIndex = TypeVariables.getTypeParameterIndex(declaredTypeParameter);
        this.containerElementTypesCascadingMetaData = Collections.emptyList();
        this.cascading = true;
        this.groupConversionHelper = groupConversionHelper;
        this.hasContainerElementsMarkedForCascading = false;
        this.valueExtractorCandidates = Collections.emptySet();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isContainer() {
        return true;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public TypeVariable<?> getTypeParameter() {
        return this.typeParameter;
    }

    public Type getEnclosingType() {
        return this.enclosingType;
    }

    public Class<?> getDeclaredContainerClass() {
        return this.declaredContainerClass;
    }

    public TypeVariable<?> getDeclaredTypeParameter() {
        return this.declaredTypeParameter;
    }

    public Integer getDeclaredTypeParameterIndex() {
        return this.declaredTypeParameterIndex;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isCascading() {
        return this.cascading;
    }

    public boolean hasContainerElementsMarkedForCascading() {
        return this.hasContainerElementsMarkedForCascading;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements() {
        return this.cascading || this.hasContainerElementsMarkedForCascading;
    }

    public List<ContainerCascadingMetaData> getContainerElementTypesCascadingMetaData() {
        return this.containerElementTypesCascadingMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public Class<?> convertGroup(Class<?> originalGroup) {
        return this.groupConversionHelper.convertGroup(originalGroup);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public Set<GroupConversionDescriptor> getGroupConversionDescriptors() {
        return this.groupConversionHelper.asDescriptors();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public <T extends CascadingMetaData> T as(Class<T> clazz) {
        if (clazz.isAssignableFrom(getClass())) {
            return this;
        }
        throw LOG.getUnableToCastException(this, clazz);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public CascadingMetaData addRuntimeContainerSupport(ValueExtractorManager valueExtractorManager, Class<?> valueClass) {
        return this;
    }

    public Set<ValueExtractorDescriptor> getValueExtractorCandidates() {
        return this.valueExtractorCandidates;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("enclosingType=").append(StringHelper.toShortString(this.enclosingType)).append(", ");
        sb.append("typeParameter=").append(this.typeParameter).append(", ");
        sb.append("cascading=").append(this.cascading).append(", ");
        sb.append("groupConversions=").append(this.groupConversionHelper).append(", ");
        sb.append("containerElementTypesCascadingMetaData=").append(this.containerElementTypesCascadingMetaData);
        sb.append("]");
        return sb.toString();
    }
}