package org.hibernate.validator.spi.group;

import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/group/DefaultGroupSequenceProvider.class */
public interface DefaultGroupSequenceProvider<T> {
    List<Class<?>> getValidationGroups(T t);
}