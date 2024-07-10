package org.hibernate.validator.internal.engine.resolver;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import javax.validation.Path;
import javax.validation.TraversableResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/CachingJPATraversableResolverForSingleValidation.class */
class CachingJPATraversableResolverForSingleValidation implements TraversableResolver {
    private final TraversableResolver delegate;
    private final HashMap<TraversableHolder, Boolean> traversables = new HashMap<>();

    public CachingJPATraversableResolverForSingleValidation(TraversableResolver delegate) {
        this.delegate = delegate;
    }

    @Override // javax.validation.TraversableResolver
    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        if (traversableObject == null) {
            return true;
        }
        return this.traversables.computeIfAbsent(new TraversableHolder(traversableObject, traversableProperty), th -> {
            return Boolean.valueOf(this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
        }).booleanValue();
    }

    @Override // javax.validation.TraversableResolver
    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/CachingJPATraversableResolverForSingleValidation$TraversableHolder.class */
    private static class TraversableHolder extends AbstractTraversableHolder {
        private TraversableHolder(Object traversableObject, Path.Node traversableProperty) {
            super(traversableObject, traversableProperty);
        }
    }
}