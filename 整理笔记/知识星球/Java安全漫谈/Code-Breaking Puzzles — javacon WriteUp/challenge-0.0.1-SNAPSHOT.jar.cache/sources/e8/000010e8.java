package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.TypeVariable;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/CascadingMetaData.class */
public interface CascadingMetaData {
    TypeVariable<?> getTypeParameter();

    boolean isCascading();

    boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements();

    Class<?> convertGroup(Class<?> cls);

    Set<GroupConversionDescriptor> getGroupConversionDescriptors();

    boolean isContainer();

    <T extends CascadingMetaData> T as(Class<T> cls);

    CascadingMetaData addRuntimeContainerSupport(ValueExtractorManager valueExtractorManager, Class<?> cls);
}