package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Executable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.metadata.BeanDescriptor;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.facets.Validatable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/BeanMetaData.class */
public interface BeanMetaData<T> extends Validatable {
    Class<T> getBeanClass();

    boolean hasConstraints();

    BeanDescriptor getBeanDescriptor();

    PropertyMetaData getMetaDataFor(String str);

    List<Class<?>> getDefaultGroupSequence(T t);

    Iterator<Sequence> getDefaultValidationSequence(T t);

    boolean defaultGroupSequenceIsRedefined();

    Set<MetaConstraint<?>> getMetaConstraints();

    Set<MetaConstraint<?>> getDirectMetaConstraints();

    Optional<ExecutableMetaData> getMetaDataFor(Executable executable) throws IllegalArgumentException;

    List<Class<? super T>> getClassHierarchy();
}