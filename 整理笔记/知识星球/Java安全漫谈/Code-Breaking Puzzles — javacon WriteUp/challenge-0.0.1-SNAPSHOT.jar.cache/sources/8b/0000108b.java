package org.hibernate.validator.internal.engine.constraintvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.constraints.Null;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ConstraintValidatorManager.class */
public class ConstraintValidatorManager {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    static ConstraintValidator<?, ?> DUMMY_CONSTRAINT_VALIDATOR = new ConstraintValidator<Null, Object>() { // from class: org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager.1
        @Override // javax.validation.ConstraintValidator
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            return false;
        }
    };
    private final ConstraintValidatorFactory defaultConstraintValidatorFactory;
    private final HibernateConstraintValidatorInitializationContext defaultConstraintValidatorInitializationContext;
    private volatile ConstraintValidatorFactory mostRecentlyUsedNonDefaultConstraintValidatorFactory;
    private volatile HibernateConstraintValidatorInitializationContext mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext;
    private final Object mostRecentlyUsedNonDefaultConstraintValidatorFactoryAndInitializationContextMutex = new Object();
    private final ConcurrentHashMap<CacheKey, ConstraintValidator<?, ?>> constraintValidatorCache = new ConcurrentHashMap<>();

    public ConstraintValidatorManager(ConstraintValidatorFactory defaultConstraintValidatorFactory, HibernateConstraintValidatorInitializationContext defaultConstraintValidatorInitializationContext) {
        this.defaultConstraintValidatorFactory = defaultConstraintValidatorFactory;
        this.defaultConstraintValidatorInitializationContext = defaultConstraintValidatorInitializationContext;
    }

    public <A extends Annotation> ConstraintValidator<A, ?> getInitializedValidator(Type validatedValueType, ConstraintDescriptorImpl<A> descriptor, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext initializationContext) {
        Contracts.assertNotNull(validatedValueType);
        Contracts.assertNotNull(descriptor);
        Contracts.assertNotNull(constraintValidatorFactory);
        Contracts.assertNotNull(initializationContext);
        CacheKey key = new CacheKey(descriptor.getAnnotationDescriptor(), validatedValueType, constraintValidatorFactory, initializationContext);
        ConstraintValidator<?, ?> constraintValidator = this.constraintValidatorCache.get(key);
        if (constraintValidator == null) {
            ConstraintValidator<A, ?> constraintValidator2 = createAndInitializeValidator(validatedValueType, descriptor, constraintValidatorFactory, initializationContext);
            constraintValidator = cacheValidator(key, constraintValidator2);
        } else {
            LOG.tracef("Constraint validator %s found in cache.", constraintValidator);
        }
        if (DUMMY_CONSTRAINT_VALIDATOR == constraintValidator) {
            return null;
        }
        return constraintValidator;
    }

    private <A extends Annotation> ConstraintValidator<A, ?> cacheValidator(CacheKey key, ConstraintValidator<A, ?> constraintValidator) {
        if ((key.getConstraintValidatorFactory() != this.defaultConstraintValidatorFactory && key.getConstraintValidatorFactory() != this.mostRecentlyUsedNonDefaultConstraintValidatorFactory) || (key.getConstraintValidatorInitializationContext() != this.defaultConstraintValidatorInitializationContext && key.getConstraintValidatorInitializationContext() != this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext)) {
            synchronized (this.mostRecentlyUsedNonDefaultConstraintValidatorFactoryAndInitializationContextMutex) {
                if (key.constraintValidatorFactory != this.mostRecentlyUsedNonDefaultConstraintValidatorFactory || key.constraintValidatorInitializationContext != this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext) {
                    clearEntries(this.mostRecentlyUsedNonDefaultConstraintValidatorFactory, this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext);
                    this.mostRecentlyUsedNonDefaultConstraintValidatorFactory = key.getConstraintValidatorFactory();
                    this.mostRecentlyUsedNonDefaultConstraintValidatorInitializationContext = key.getConstraintValidatorInitializationContext();
                }
            }
        }
        ConstraintValidator<A, ?> cached = (ConstraintValidator<A, ?>) this.constraintValidatorCache.putIfAbsent(key, constraintValidator);
        return cached != null ? cached : constraintValidator;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <A extends Annotation> ConstraintValidator<A, ?> createAndInitializeValidator(Type validatedValueType, ConstraintDescriptorImpl<A> descriptor, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext initializationContext) {
        ConstraintValidator newInstance;
        ConstraintValidatorDescriptor<A> validatorDescriptor = findMatchingValidatorDescriptor(descriptor, validatedValueType);
        if (validatorDescriptor == null) {
            newInstance = DUMMY_CONSTRAINT_VALIDATOR;
        } else {
            newInstance = validatorDescriptor.newInstance(constraintValidatorFactory);
            initializeValidator(descriptor, newInstance, initializationContext);
        }
        return newInstance;
    }

    private void clearEntries(ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
        Iterator<Map.Entry<CacheKey, ConstraintValidator<?, ?>>> cacheEntries = this.constraintValidatorCache.entrySet().iterator();
        while (cacheEntries.hasNext()) {
            Map.Entry<CacheKey, ConstraintValidator<?, ?>> cacheEntry = cacheEntries.next();
            if (cacheEntry.getKey().getConstraintValidatorFactory() == constraintValidatorFactory && cacheEntry.getKey().getConstraintValidatorInitializationContext() == constraintValidatorInitializationContext) {
                constraintValidatorFactory.releaseInstance(cacheEntry.getValue());
                cacheEntries.remove();
            }
        }
    }

    public void clear() {
        for (Map.Entry<CacheKey, ConstraintValidator<?, ?>> entry : this.constraintValidatorCache.entrySet()) {
            entry.getKey().getConstraintValidatorFactory().releaseInstance(entry.getValue());
        }
        this.constraintValidatorCache.clear();
    }

    public ConstraintValidatorFactory getDefaultConstraintValidatorFactory() {
        return this.defaultConstraintValidatorFactory;
    }

    public HibernateConstraintValidatorInitializationContext getDefaultConstraintValidatorInitializationContext() {
        return this.defaultConstraintValidatorInitializationContext;
    }

    public int numberOfCachedConstraintValidatorInstances() {
        return this.constraintValidatorCache.size();
    }

    private <A extends Annotation> ConstraintValidatorDescriptor<A> findMatchingValidatorDescriptor(ConstraintDescriptorImpl<A> descriptor, Type validatedValueType) {
        Map<Type, ConstraintValidatorDescriptor<A>> availableValidatorDescriptors = TypeHelper.getValidatorTypes(descriptor.getAnnotationType(), descriptor.getMatchingConstraintValidatorDescriptors());
        List<Type> discoveredSuitableTypes = findSuitableValidatorTypes(validatedValueType, availableValidatorDescriptors.keySet());
        resolveAssignableTypes(discoveredSuitableTypes);
        if (discoveredSuitableTypes.size() == 0) {
            return null;
        }
        if (discoveredSuitableTypes.size() > 1) {
            throw LOG.getMoreThanOneValidatorFoundForTypeException(validatedValueType, discoveredSuitableTypes);
        }
        Type suitableType = discoveredSuitableTypes.get(0);
        return availableValidatorDescriptors.get(suitableType);
    }

    private <A extends Annotation> List<Type> findSuitableValidatorTypes(Type type, Iterable<Type> availableValidatorTypes) {
        List<Type> determinedSuitableTypes = CollectionHelper.newArrayList();
        for (Type validatorType : availableValidatorTypes) {
            if (TypeHelper.isAssignable(validatorType, type) && !determinedSuitableTypes.contains(validatorType)) {
                determinedSuitableTypes.add(validatorType);
            }
        }
        return determinedSuitableTypes;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <A extends Annotation> void initializeValidator(ConstraintDescriptor<A> descriptor, ConstraintValidator<A, ?> constraintValidator, HibernateConstraintValidatorInitializationContext initializationContext) {
        try {
            if (constraintValidator instanceof HibernateConstraintValidator) {
                ((HibernateConstraintValidator) constraintValidator).initialize(descriptor, initializationContext);
            }
            constraintValidator.initialize(descriptor.getAnnotation());
        } catch (RuntimeException e) {
            if (e instanceof ConstraintDeclarationException) {
                throw e;
            }
            throw LOG.getUnableToInitializeConstraintValidatorException(constraintValidator.getClass(), e);
        }
    }

    private void resolveAssignableTypes(List<Type> assignableTypes) {
        if (assignableTypes.size() == 0 || assignableTypes.size() == 1) {
            return;
        }
        List<Type> typesToRemove = new ArrayList<>();
        do {
            typesToRemove.clear();
            Type type = assignableTypes.get(0);
            for (int i = 1; i < assignableTypes.size(); i++) {
                if (TypeHelper.isAssignable(type, assignableTypes.get(i))) {
                    typesToRemove.add(type);
                } else if (TypeHelper.isAssignable(assignableTypes.get(i), type)) {
                    typesToRemove.add(assignableTypes.get(i));
                }
            }
            assignableTypes.removeAll(typesToRemove);
        } while (typesToRemove.size() > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintvalidation/ConstraintValidatorManager$CacheKey.class */
    public static final class CacheKey {
        private ConstraintAnnotationDescriptor<?> annotationDescriptor;
        private Type validatedType;
        private ConstraintValidatorFactory constraintValidatorFactory;
        private HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
        private int hashCode;

        private CacheKey(ConstraintAnnotationDescriptor<?> annotationDescriptor, Type validatorType, ConstraintValidatorFactory constraintValidatorFactory, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
            this.annotationDescriptor = annotationDescriptor;
            this.validatedType = validatorType;
            this.constraintValidatorFactory = constraintValidatorFactory;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
            this.hashCode = createHashCode();
        }

        public ConstraintValidatorFactory getConstraintValidatorFactory() {
            return this.constraintValidatorFactory;
        }

        public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
            return this.constraintValidatorInitializationContext;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            CacheKey other = (CacheKey) o;
            if (!this.annotationDescriptor.equals(other.annotationDescriptor) || !this.validatedType.equals(other.validatedType) || !this.constraintValidatorFactory.equals(other.constraintValidatorFactory) || !this.constraintValidatorInitializationContext.equals(other.constraintValidatorInitializationContext)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = this.annotationDescriptor.hashCode();
            return (31 * ((31 * ((31 * result) + this.validatedType.hashCode())) + this.constraintValidatorFactory.hashCode())) + this.constraintValidatorInitializationContext.hashCode();
        }
    }
}