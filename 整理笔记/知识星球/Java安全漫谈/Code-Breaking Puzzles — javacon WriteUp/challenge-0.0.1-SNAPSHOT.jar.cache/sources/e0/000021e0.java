package org.springframework.jmx.export.metadata;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/metadata/JmxAttributeSource.class */
public interface JmxAttributeSource {
    @Nullable
    ManagedResource getManagedResource(Class<?> cls) throws InvalidMetadataException;

    @Nullable
    ManagedAttribute getManagedAttribute(Method method) throws InvalidMetadataException;

    @Nullable
    ManagedMetric getManagedMetric(Method method) throws InvalidMetadataException;

    @Nullable
    ManagedOperation getManagedOperation(Method method) throws InvalidMetadataException;

    ManagedOperationParameter[] getManagedOperationParameters(Method method) throws InvalidMetadataException;

    ManagedNotification[] getManagedNotifications(Class<?> cls) throws InvalidMetadataException;
}