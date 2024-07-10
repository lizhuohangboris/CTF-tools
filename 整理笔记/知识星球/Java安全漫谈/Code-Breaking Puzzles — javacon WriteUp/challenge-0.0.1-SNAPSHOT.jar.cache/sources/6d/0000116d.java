package org.hibernate.validator.internal.util;

import com.fasterxml.classmate.TypeResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/TypeResolutionHelper.class */
public class TypeResolutionHelper {
    private final TypeResolver typeResolver = new TypeResolver();

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }
}