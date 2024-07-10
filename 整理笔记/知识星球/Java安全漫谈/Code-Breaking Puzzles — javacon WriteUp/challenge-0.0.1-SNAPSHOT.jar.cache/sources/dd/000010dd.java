package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.TypeVariableBindings;
import org.hibernate.validator.internal.util.TypeVariables;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorResolver.class */
public class ValueExtractorResolver {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Object NON_CONTAINER_VALUE = new Object();
    private final Set<ValueExtractorDescriptor> registeredValueExtractors;
    private final ConcurrentHashMap<ValueExtractorCacheKey, Set<ValueExtractorDescriptor>> possibleValueExtractorsByRuntimeTypeAndTypeParameter = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Set<ValueExtractorDescriptor>> possibleValueExtractorsByRuntimeType = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Object> nonContainerTypes = new ConcurrentHashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueExtractorResolver(Set<ValueExtractorDescriptor> valueExtractors) {
        this.registeredValueExtractors = CollectionHelper.toImmutableSet(valueExtractors);
    }

    public Set<ValueExtractorDescriptor> getMaximallySpecificValueExtractors(Class<?> declaredType) {
        return getRuntimeCompliantValueExtractors(declaredType, this.registeredValueExtractors);
    }

    public ValueExtractorDescriptor getMaximallySpecificAndContainerElementCompliantValueExtractor(Class<?> declaredType, TypeVariable<?> typeParameter) {
        return getUniqueValueExtractorOrThrowException(declaredType, getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, declaredType, this.registeredValueExtractors));
    }

    public ValueExtractorDescriptor getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(Type declaredType, TypeVariable<?> typeParameter, Class<?> runtimeType, Collection<ValueExtractorDescriptor> valueExtractorCandidates) {
        Contracts.assertNotEmpty(valueExtractorCandidates, "Value extractor candidates cannot be empty");
        if (valueExtractorCandidates.size() == 1) {
            return valueExtractorCandidates.iterator().next();
        }
        return getUniqueValueExtractorOrThrowException(runtimeType, getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, runtimeType, valueExtractorCandidates));
    }

    public ValueExtractorDescriptor getMaximallySpecificValueExtractorForAllContainerElements(Class<?> runtimeType, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        if (TypeHelper.isAssignable(Map.class, runtimeType)) {
            return MapValueExtractor.DESCRIPTOR;
        }
        return getUniqueValueExtractorOrThrowException(runtimeType, getRuntimeCompliantValueExtractors(runtimeType, potentialValueExtractorDescriptors));
    }

    public Set<ValueExtractorDescriptor> getValueExtractorCandidatesForCascadedValidation(Type declaredType, TypeVariable<?> typeParameter) {
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = new HashSet<>();
        valueExtractorDescriptors.addAll(getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(declaredType, typeParameter, TypeHelper.getErasedReferenceType(declaredType), this.registeredValueExtractors));
        valueExtractorDescriptors.addAll(getPotentiallyRuntimeTypeCompliantAndContainerElementCompliantValueExtractors(declaredType, typeParameter));
        return CollectionHelper.toImmutableSet(valueExtractorDescriptors);
    }

    public Set<ValueExtractorDescriptor> getValueExtractorCandidatesForContainerDetectionOfGlobalCascadedValidation(Type enclosingType) {
        boolean mapAssignable = TypeHelper.isAssignable(Map.class, enclosingType);
        Class<?> enclosingClass = ReflectionHelper.getClassFromType(enclosingType);
        return (Set) getRuntimeCompliantValueExtractors(enclosingClass, this.registeredValueExtractors).stream().filter(ved -> {
            return (mapAssignable && ved.equals(MapKeyExtractor.DESCRIPTOR)) ? false : true;
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
    }

    public Set<ValueExtractorDescriptor> getPotentialValueExtractorCandidatesForCascadedValidation(Type declaredType) {
        return (Set) this.registeredValueExtractors.stream().filter(e -> {
            return TypeHelper.isAssignable(declaredType, e.getContainerType());
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
    }

    public void clear() {
        this.nonContainerTypes.clear();
        this.possibleValueExtractorsByRuntimeType.clear();
        this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.clear();
    }

    private Set<ValueExtractorDescriptor> getPotentiallyRuntimeTypeCompliantAndContainerElementCompliantValueExtractors(Type declaredType, TypeVariable<?> typeParameter) {
        TypeVariable<?> typeParameterBoundToExtractorType;
        boolean isInternal = TypeVariables.isInternal(typeParameter);
        Type erasedDeclaredType = TypeHelper.getErasedReferenceType(declaredType);
        Set<ValueExtractorDescriptor> typeCompatibleExtractors = (Set) this.registeredValueExtractors.stream().filter(e -> {
            return TypeHelper.isAssignable(erasedDeclaredType, e.getContainerType());
        }).collect(Collectors.toSet());
        Set<ValueExtractorDescriptor> containerElementCompliantExtractors = new HashSet<>();
        for (ValueExtractorDescriptor extractorDescriptor : typeCompatibleExtractors) {
            if (!isInternal) {
                Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings = TypeVariableBindings.getTypeVariableBindings(extractorDescriptor.getContainerType());
                Map<TypeVariable<?>, TypeVariable<?>> bindingsForExtractorType = allBindings.get(erasedDeclaredType);
                typeParameterBoundToExtractorType = bind(extractorDescriptor.getExtractedTypeParameter(), bindingsForExtractorType);
            } else {
                typeParameterBoundToExtractorType = typeParameter;
            }
            if (Objects.equals(typeParameter, typeParameterBoundToExtractorType)) {
                containerElementCompliantExtractors.add(extractorDescriptor);
            }
        }
        return containerElementCompliantExtractors;
    }

    private ValueExtractorDescriptor getUniqueValueExtractorOrThrowException(Class<?> runtimeType, Set<ValueExtractorDescriptor> maximallySpecificContainerElementCompliantValueExtractors) {
        if (maximallySpecificContainerElementCompliantValueExtractors.size() == 1) {
            return maximallySpecificContainerElementCompliantValueExtractors.iterator().next();
        }
        if (maximallySpecificContainerElementCompliantValueExtractors.isEmpty()) {
            return null;
        }
        throw LOG.getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(runtimeType, ValueExtractorHelper.toValueExtractorClasses(maximallySpecificContainerElementCompliantValueExtractors));
    }

    private Set<ValueExtractorDescriptor> getMaximallySpecificValueExtractors(Set<ValueExtractorDescriptor> possibleValueExtractors) {
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = CollectionHelper.newHashSet(possibleValueExtractors.size());
        for (ValueExtractorDescriptor descriptor : possibleValueExtractors) {
            if (valueExtractorDescriptors.isEmpty()) {
                valueExtractorDescriptors.add(descriptor);
            } else {
                Iterator<ValueExtractorDescriptor> candidatesIterator = valueExtractorDescriptors.iterator();
                boolean isNewRoot = true;
                while (candidatesIterator.hasNext()) {
                    ValueExtractorDescriptor candidate = candidatesIterator.next();
                    if (!candidate.getContainerType().equals(descriptor.getContainerType())) {
                        if (TypeHelper.isAssignable(candidate.getContainerType(), descriptor.getContainerType())) {
                            candidatesIterator.remove();
                        } else if (TypeHelper.isAssignable(descriptor.getContainerType(), candidate.getContainerType())) {
                            isNewRoot = false;
                        }
                    }
                }
                if (isNewRoot) {
                    valueExtractorDescriptors.add(descriptor);
                }
            }
        }
        return valueExtractorDescriptors;
    }

    private Set<ValueExtractorDescriptor> getRuntimeCompliantValueExtractors(Class<?> runtimeType, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        if (this.nonContainerTypes.contains(runtimeType)) {
            return Collections.emptySet();
        }
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = this.possibleValueExtractorsByRuntimeType.get(runtimeType);
        if (valueExtractorDescriptors == null) {
            Set<ValueExtractorDescriptor> possibleValueExtractors = (Set) potentialValueExtractorDescriptors.stream().filter(e -> {
                return TypeHelper.isAssignable(e.getContainerType(), runtimeType);
            }).collect(Collectors.toSet());
            valueExtractorDescriptors = getMaximallySpecificValueExtractors(possibleValueExtractors);
        }
        if (valueExtractorDescriptors.isEmpty()) {
            this.nonContainerTypes.put(runtimeType, NON_CONTAINER_VALUE);
            return valueExtractorDescriptors;
        }
        Set<ValueExtractorDescriptor> extractorDescriptorsToCache = CollectionHelper.toImmutableSet(valueExtractorDescriptors);
        this.possibleValueExtractorsByRuntimeType.put(runtimeType, extractorDescriptorsToCache);
        return extractorDescriptorsToCache;
    }

    private Set<ValueExtractorDescriptor> getRuntimeAndContainerElementCompliantValueExtractorsFromPossibleCandidates(Type declaredType, TypeVariable<?> typeParameter, Class<?> runtimeType, Collection<ValueExtractorDescriptor> valueExtractorCandidates) {
        if (this.nonContainerTypes.contains(runtimeType)) {
            return Collections.emptySet();
        }
        ValueExtractorCacheKey cacheKey = new ValueExtractorCacheKey(runtimeType, typeParameter);
        Set<ValueExtractorDescriptor> valueExtractorDescriptors = this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.get(cacheKey);
        if (valueExtractorDescriptors == null) {
            boolean isInternal = TypeVariables.isInternal(typeParameter);
            Class<?> erasedDeclaredType = TypeHelper.getErasedReferenceType(declaredType);
            Set<ValueExtractorDescriptor> possibleValueExtractors = (Set) valueExtractorCandidates.stream().filter(e -> {
                return TypeHelper.isAssignable(e.getContainerType(), runtimeType);
            }).filter(extractorDescriptor -> {
                return checkValueExtractorTypeCompatibility(typeParameter, isInternal, erasedDeclaredType, extractorDescriptor);
            }).collect(Collectors.toSet());
            valueExtractorDescriptors = getMaximallySpecificValueExtractors(possibleValueExtractors);
            if (valueExtractorDescriptors.isEmpty()) {
                this.nonContainerTypes.put(runtimeType, NON_CONTAINER_VALUE);
            } else {
                Set<ValueExtractorDescriptor> extractorDescriptorsToCache = CollectionHelper.toImmutableSet(valueExtractorDescriptors);
                this.possibleValueExtractorsByRuntimeTypeAndTypeParameter.put(cacheKey, extractorDescriptorsToCache);
                return extractorDescriptorsToCache;
            }
        }
        return valueExtractorDescriptors;
    }

    private boolean checkValueExtractorTypeCompatibility(TypeVariable<?> typeParameter, boolean isInternal, Class<?> erasedDeclaredType, ValueExtractorDescriptor extractorDescriptor) {
        if (TypeHelper.isAssignable(extractorDescriptor.getContainerType(), erasedDeclaredType)) {
            return validateValueExtractorCompatibility(isInternal, erasedDeclaredType, extractorDescriptor.getContainerType(), typeParameter, extractorDescriptor.getExtractedTypeParameter());
        }
        return validateValueExtractorCompatibility(isInternal, extractorDescriptor.getContainerType(), erasedDeclaredType, extractorDescriptor.getExtractedTypeParameter(), typeParameter);
    }

    private boolean validateValueExtractorCompatibility(boolean isInternal, Class<?> typeForBinding, Class<?> typeToBind, TypeVariable<?> typeParameterForBinding, TypeVariable<?> typeParameterToCompare) {
        TypeVariable<?> typeParameterBoundToExtractorType;
        if (!isInternal) {
            Map<Class<?>, Map<TypeVariable<?>, TypeVariable<?>>> allBindings = TypeVariableBindings.getTypeVariableBindings(typeForBinding);
            Map<TypeVariable<?>, TypeVariable<?>> bindingsForExtractorType = allBindings.get(typeToBind);
            typeParameterBoundToExtractorType = bind(typeParameterForBinding, bindingsForExtractorType);
        } else {
            typeParameterBoundToExtractorType = typeParameterForBinding;
        }
        return Objects.equals(typeParameterToCompare, typeParameterBoundToExtractorType);
    }

    private TypeVariable<?> bind(TypeVariable<?> typeParameter, Map<TypeVariable<?>, TypeVariable<?>> bindings) {
        if (bindings != null) {
            return bindings.get(typeParameter);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorResolver$ValueExtractorCacheKey.class */
    public static class ValueExtractorCacheKey {
        private Class<?> type;
        private TypeVariable<?> typeParameter;
        private int hashCode = buildHashCode();

        ValueExtractorCacheKey(Class<?> type, TypeVariable<?> typeParameter) {
            this.type = type;
            this.typeParameter = typeParameter;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            ValueExtractorCacheKey that = (ValueExtractorCacheKey) o;
            return Objects.equals(this.type, that.type) && Objects.equals(this.typeParameter, that.typeParameter);
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int buildHashCode() {
            int result = this.type.hashCode();
            return (31 * result) + (this.typeParameter != null ? this.typeParameter.hashCode() : 0);
        }
    }
}