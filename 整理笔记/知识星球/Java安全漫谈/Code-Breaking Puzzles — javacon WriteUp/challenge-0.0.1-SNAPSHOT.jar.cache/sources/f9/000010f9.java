package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/PotentiallyContainerCascadingMetaData.class */
public class PotentiallyContainerCascadingMetaData implements CascadingMetaData {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final GroupConversionHelper groupConversionHelper;
    private final Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors;

    public static PotentiallyContainerCascadingMetaData of(CascadingMetaDataBuilder cascadingMetaDataBuilder, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors, Object context) {
        return new PotentiallyContainerCascadingMetaData(cascadingMetaDataBuilder, potentialValueExtractorDescriptors);
    }

    private PotentiallyContainerCascadingMetaData(CascadingMetaDataBuilder cascadingMetaDataBuilder, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        this(potentialValueExtractorDescriptors, GroupConversionHelper.of(cascadingMetaDataBuilder.getGroupConversions()));
    }

    private PotentiallyContainerCascadingMetaData(Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors, GroupConversionHelper groupConversionHelper) {
        this.potentialValueExtractorDescriptors = potentialValueExtractorDescriptors;
        this.groupConversionHelper = groupConversionHelper;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public TypeVariable<?> getTypeParameter() {
        return AnnotatedObject.INSTANCE;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isCascading() {
        return true;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements() {
        return true;
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
    public boolean isContainer() {
        return false;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public CascadingMetaData addRuntimeContainerSupport(ValueExtractorManager valueExtractorManager, Class<?> valueClass) {
        ValueExtractorDescriptor compliantValueExtractor = valueExtractorManager.getResolver().getMaximallySpecificValueExtractorForAllContainerElements(valueClass, this.potentialValueExtractorDescriptors);
        if (compliantValueExtractor == null) {
            return this;
        }
        return new ContainerCascadingMetaData(valueClass, Collections.singletonList(new ContainerCascadingMetaData(compliantValueExtractor.getContainerType(), compliantValueExtractor.getExtractedTypeParameter(), compliantValueExtractor.getContainerType(), compliantValueExtractor.getExtractedTypeParameter(), this.groupConversionHelper.isEmpty() ? GroupConversionHelper.EMPTY : this.groupConversionHelper)), this.groupConversionHelper, Collections.singleton(compliantValueExtractor));
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public <T extends CascadingMetaData> T as(Class<T> clazz) {
        if (clazz.isAssignableFrom(getClass())) {
            return this;
        }
        throw LOG.getUnableToCastException(this, clazz);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("groupConversions=").append(this.groupConversionHelper).append(", ");
        sb.append("]");
        return sb.toString();
    }
}