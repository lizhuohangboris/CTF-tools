package org.hibernate.validator.internal.metadata.provider;

import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/MetaDataProvider.class */
public interface MetaDataProvider {
    AnnotationProcessingOptions getAnnotationProcessingOptions();

    <T> BeanConfiguration<? super T> getBeanConfiguration(Class<T> cls);
}