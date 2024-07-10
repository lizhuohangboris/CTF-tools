package org.hibernate.validator.internal.engine.resolver;

import java.lang.annotation.ElementType;
import javax.validation.Path;
import javax.validation.TraversableResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/TraverseAllTraversableResolver.class */
class TraverseAllTraversableResolver implements TraversableResolver {
    @Override // javax.validation.TraversableResolver
    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    @Override // javax.validation.TraversableResolver
    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }
}