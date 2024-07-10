package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.TypeVariable;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/NonContainerCascadingMetaData.class */
public class NonContainerCascadingMetaData implements CascadingMetaData {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final NonContainerCascadingMetaData NON_CASCADING = new NonContainerCascadingMetaData(false, GroupConversionHelper.EMPTY);
    private static final NonContainerCascadingMetaData CASCADING_WITHOUT_GROUP_CONVERSIONS = new NonContainerCascadingMetaData(true, GroupConversionHelper.EMPTY);
    private final boolean cascading;
    private GroupConversionHelper groupConversionHelper;

    public static NonContainerCascadingMetaData of(CascadingMetaDataBuilder cascadingMetaDataBuilder, Object context) {
        if (!cascadingMetaDataBuilder.isCascading()) {
            return NON_CASCADING;
        }
        if (cascadingMetaDataBuilder.getGroupConversions().isEmpty()) {
            return CASCADING_WITHOUT_GROUP_CONVERSIONS;
        }
        return new NonContainerCascadingMetaData(cascadingMetaDataBuilder);
    }

    private NonContainerCascadingMetaData(CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        this(cascadingMetaDataBuilder.isCascading(), GroupConversionHelper.of(cascadingMetaDataBuilder.getGroupConversions()));
    }

    private NonContainerCascadingMetaData(boolean cascading, GroupConversionHelper groupConversionHelper) {
        this.cascading = cascading;
        this.groupConversionHelper = groupConversionHelper;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public TypeVariable<?> getTypeParameter() {
        return AnnotatedObject.INSTANCE;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isCascading() {
        return this.cascading;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData
    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements() {
        return this.cascading;
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
        return this;
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
        sb.append("cascading=").append(this.cascading).append(", ");
        sb.append("groupConversions=").append(this.groupConversionHelper).append(", ");
        sb.append("]");
        return sb.toString();
    }
}