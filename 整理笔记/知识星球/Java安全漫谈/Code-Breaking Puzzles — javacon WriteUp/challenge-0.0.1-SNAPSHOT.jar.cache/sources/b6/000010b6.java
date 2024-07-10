package org.hibernate.validator.internal.engine.resolver;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Path;
import javax.validation.TraversableResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/CachingTraversableResolverForSingleValidation.class */
class CachingTraversableResolverForSingleValidation implements TraversableResolver {
    private final TraversableResolver delegate;
    private final Map<TraversableHolder, TraversableHolder> traversables = new HashMap();

    public CachingTraversableResolverForSingleValidation(TraversableResolver delegate) {
        this.delegate = delegate;
    }

    @Override // javax.validation.TraversableResolver
    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        TraversableHolder currentLH = new TraversableHolder(traversableObject, traversableProperty);
        TraversableHolder cachedLH = this.traversables.get(currentLH);
        if (cachedLH == null) {
            currentLH.isReachable = Boolean.valueOf(this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
            this.traversables.put(currentLH, currentLH);
            cachedLH = currentLH;
        } else if (cachedLH.isReachable == null) {
            cachedLH.isReachable = Boolean.valueOf(this.delegate.isReachable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
        }
        return cachedLH.isReachable.booleanValue();
    }

    @Override // javax.validation.TraversableResolver
    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        TraversableHolder currentLH = new TraversableHolder(traversableObject, traversableProperty);
        TraversableHolder cachedLH = this.traversables.get(currentLH);
        if (cachedLH == null) {
            currentLH.isCascadable = Boolean.valueOf(this.delegate.isCascadable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
            this.traversables.put(currentLH, currentLH);
            cachedLH = currentLH;
        } else if (cachedLH.isCascadable == null) {
            cachedLH.isCascadable = Boolean.valueOf(this.delegate.isCascadable(traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType));
        }
        return cachedLH.isCascadable.booleanValue();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/resolver/CachingTraversableResolverForSingleValidation$TraversableHolder.class */
    private static final class TraversableHolder extends AbstractTraversableHolder {
        private Boolean isReachable;
        private Boolean isCascadable;

        private TraversableHolder(Object traversableObject, Path.Node traversableProperty) {
            super(traversableObject, traversableProperty);
        }
    }
}