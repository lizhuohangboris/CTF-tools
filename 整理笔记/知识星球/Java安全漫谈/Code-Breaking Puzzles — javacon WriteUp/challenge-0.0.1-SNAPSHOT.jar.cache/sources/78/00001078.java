package org.hibernate.validator.internal.engine;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.ValidationContext;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValueContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.GroupWithInheritance;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.engine.resolver.TraversableResolvers;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorHelper;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ContainerCascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ParameterMetaData;
import org.hibernate.validator.internal.metadata.aggregated.PropertyMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ReturnValueMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.FieldConstraintLocation;
import org.hibernate.validator.internal.metadata.location.GetterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorImpl.class */
public class ValidatorImpl implements Validator, ExecutableValidator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Collection<Class<?>> DEFAULT_GROUPS = Collections.singletonList(Default.class);
    private final transient ValidationOrderGenerator validationOrderGenerator;
    private final ConstraintValidatorFactory constraintValidatorFactory;
    private final TraversableResolver traversableResolver;
    private final BeanMetaDataManager beanMetaDataManager;
    private final ConstraintValidatorManager constraintValidatorManager;
    private final ValueExtractorManager valueExtractorManager;
    private final ValidationContext.ValidatorScopedContext validatorScopedContext;
    private final HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;

    public ValidatorImpl(ConstraintValidatorFactory constraintValidatorFactory, BeanMetaDataManager beanMetaDataManager, ValueExtractorManager valueExtractorManager, ConstraintValidatorManager constraintValidatorManager, ValidationOrderGenerator validationOrderGenerator, ValidatorFactoryImpl.ValidatorFactoryScopedContext validatorFactoryScopedContext) {
        this.constraintValidatorFactory = constraintValidatorFactory;
        this.beanMetaDataManager = beanMetaDataManager;
        this.valueExtractorManager = valueExtractorManager;
        this.constraintValidatorManager = constraintValidatorManager;
        this.validationOrderGenerator = validationOrderGenerator;
        this.validatorScopedContext = new ValidationContext.ValidatorScopedContext(validatorFactoryScopedContext);
        this.traversableResolver = validatorFactoryScopedContext.getTraversableResolver();
        this.constraintValidatorInitializationContext = validatorFactoryScopedContext.getConstraintValidatorInitializationContext();
    }

    @Override // javax.validation.Validator
    public final <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Contracts.assertNotNull(object, Messages.MESSAGES.validatedObjectMustNotBeNull());
        sanityCheckGroups(groups);
        ValidationContext<T> validationContext = getValidationContextBuilder().forValidate(object);
        if (!validationContext.getRootBeanMetaData().hasConstraints()) {
            return Collections.emptySet();
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        ValueContext<?, Object> valueContext = ValueContext.getLocalExecutionContext(this.validatorScopedContext.getParameterNameProvider(), (Object) object, (BeanMetaData<?>) validationContext.getRootBeanMetaData(), PathImpl.createRootPath());
        return validateInContext(validationContext, valueContext, validationOrder);
    }

    @Override // javax.validation.Validator
    public final <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        Contracts.assertNotNull(object, Messages.MESSAGES.validatedObjectMustNotBeNull());
        sanityCheckPropertyPath(propertyName);
        sanityCheckGroups(groups);
        ValidationContext<T> validationContext = getValidationContextBuilder().forValidateProperty(object);
        if (!validationContext.getRootBeanMetaData().hasConstraints()) {
            return Collections.emptySet();
        }
        PathImpl propertyPath = PathImpl.createPathFromString(propertyName);
        ValueContext<?, Object> valueContext = getValueContextForPropertyValidation(validationContext, propertyPath);
        if (valueContext.getCurrentBean() == null) {
            throw LOG.getUnableToReachPropertyToValidateException(validationContext.getRootBean(), propertyPath);
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        return validateInContext(validationContext, valueContext, validationOrder);
    }

    @Override // javax.validation.Validator
    public final <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Contracts.assertNotNull(beanType, Messages.MESSAGES.beanTypeCannotBeNull());
        sanityCheckPropertyPath(propertyName);
        sanityCheckGroups(groups);
        ValidationContext<T> validationContext = getValidationContextBuilder().forValidateValue(beanType);
        if (!validationContext.getRootBeanMetaData().hasConstraints()) {
            return Collections.emptySet();
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        return validateValueInContext(validationContext, value, PathImpl.createPathFromString(propertyName), validationOrder);
    }

    @Override // javax.validation.executable.ExecutableValidator
    public <T> Set<ConstraintViolation<T>> validateParameters(T object, Method method, Object[] parameterValues, Class<?>... groups) {
        Contracts.assertNotNull(object, Messages.MESSAGES.validatedObjectMustNotBeNull());
        Contracts.assertNotNull(method, Messages.MESSAGES.validatedMethodMustNotBeNull());
        Contracts.assertNotNull(parameterValues, Messages.MESSAGES.validatedParameterArrayMustNotBeNull());
        return validateParameters((ValidatorImpl) object, (Executable) method, parameterValues, groups);
    }

    @Override // javax.validation.executable.ExecutableValidator
    public <T> Set<ConstraintViolation<T>> validateConstructorParameters(Constructor<? extends T> constructor, Object[] parameterValues, Class<?>... groups) {
        Contracts.assertNotNull(constructor, Messages.MESSAGES.validatedConstructorMustNotBeNull());
        Contracts.assertNotNull(parameterValues, Messages.MESSAGES.validatedParameterArrayMustNotBeNull());
        return validateParameters((ValidatorImpl) null, constructor, parameterValues, groups);
    }

    @Override // javax.validation.executable.ExecutableValidator
    public <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(Constructor<? extends T> constructor, T createdObject, Class<?>... groups) {
        Contracts.assertNotNull(constructor, Messages.MESSAGES.validatedConstructorMustNotBeNull());
        Contracts.assertNotNull(createdObject, Messages.MESSAGES.validatedConstructorCreatedInstanceMustNotBeNull());
        return validateReturnValue((ValidatorImpl) null, constructor, createdObject, groups);
    }

    @Override // javax.validation.executable.ExecutableValidator
    public <T> Set<ConstraintViolation<T>> validateReturnValue(T object, Method method, Object returnValue, Class<?>... groups) {
        Contracts.assertNotNull(object, Messages.MESSAGES.validatedObjectMustNotBeNull());
        Contracts.assertNotNull(method, Messages.MESSAGES.validatedMethodMustNotBeNull());
        return validateReturnValue((ValidatorImpl) object, (Executable) method, returnValue, groups);
    }

    private <T> Set<ConstraintViolation<T>> validateParameters(T object, Executable executable, Object[] parameterValues, Class<?>... groups) {
        sanityCheckGroups(groups);
        ValidationContext<T> validationContext = getValidationContextBuilder().forValidateParameters(this.validatorScopedContext.getParameterNameProvider(), object, executable, parameterValues);
        if (!validationContext.getRootBeanMetaData().hasConstraints()) {
            return Collections.emptySet();
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        validateParametersInContext(validationContext, parameterValues, validationOrder);
        return validationContext.getFailingConstraints();
    }

    private <T> Set<ConstraintViolation<T>> validateReturnValue(T object, Executable executable, Object returnValue, Class<?>... groups) {
        sanityCheckGroups(groups);
        ValidationContext<T> validationContext = getValidationContextBuilder().forValidateReturnValue(object, executable, returnValue);
        if (!validationContext.getRootBeanMetaData().hasConstraints()) {
            return Collections.emptySet();
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        validateReturnValueInContext(validationContext, object, returnValue, validationOrder);
        return validationContext.getFailingConstraints();
    }

    @Override // javax.validation.Validator
    public final BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return this.beanMetaDataManager.getBeanMetaData(clazz).getBeanDescriptor();
    }

    @Override // javax.validation.Validator
    public final <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(Validator.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    @Override // javax.validation.Validator
    public ExecutableValidator forExecutables() {
        return this;
    }

    private ValidationContext.ValidationContextBuilder getValidationContextBuilder() {
        return ValidationContext.getValidationContextBuilder(this.beanMetaDataManager, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, TraversableResolvers.wrapWithCachingForSingleValidation(this.traversableResolver, this.validatorScopedContext.isTraversableResolverResultCacheEnabled()), this.constraintValidatorInitializationContext);
    }

    private void sanityCheckPropertyPath(String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            throw LOG.getInvalidPropertyPathException();
        }
    }

    private void sanityCheckGroups(Class<?>[] groups) {
        Contracts.assertNotNull(groups, Messages.MESSAGES.groupMustNotBeNull());
        for (Class<?> clazz : groups) {
            if (clazz == null) {
                throw new IllegalArgumentException(Messages.MESSAGES.groupMustNotBeNull());
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ValidationOrder determineGroupValidationOrder(Class<?>[] groups) {
        Collection<Class<?>> resultGroups;
        if (groups.length == 0) {
            resultGroups = DEFAULT_GROUPS;
        } else {
            resultGroups = Arrays.asList(groups);
        }
        return this.validationOrderGenerator.getValidationOrder(resultGroups);
    }

    public <T, U> Set<ConstraintViolation<T>> validateInContext(ValidationContext<T> validationContext, ValueContext<U, Object> valueContext, ValidationOrder validationOrder) {
        if (valueContext.getCurrentBean() == null) {
            return Collections.emptySet();
        }
        BeanMetaData<U> beanMetaData = valueContext.getCurrentBeanMetaData();
        if (beanMetaData.defaultGroupSequenceIsRedefined()) {
            validationOrder.assertDefaultGroupSequenceIsExpandable(beanMetaData.getDefaultGroupSequence(valueContext.getCurrentBean()));
        }
        Iterator<Group> groupIterator = validationOrder.getGroupIterator();
        while (groupIterator.hasNext()) {
            Group group = groupIterator.next();
            valueContext.setCurrentGroup(group.getDefiningClass());
            validateConstraintsForCurrentGroup(validationContext, valueContext);
            if (shouldFailFast(validationContext)) {
                return validationContext.getFailingConstraints();
            }
        }
        Iterator<Group> groupIterator2 = validationOrder.getGroupIterator();
        while (groupIterator2.hasNext()) {
            Group group2 = groupIterator2.next();
            valueContext.setCurrentGroup(group2.getDefiningClass());
            validateCascadedConstraints(validationContext, valueContext);
            if (shouldFailFast(validationContext)) {
                return validationContext.getFailingConstraints();
            }
        }
        Iterator<Sequence> sequenceIterator = validationOrder.getSequenceIterator();
        while (sequenceIterator.hasNext()) {
            Sequence sequence = sequenceIterator.next();
            Iterator<GroupWithInheritance> it = sequence.iterator();
            while (it.hasNext()) {
                GroupWithInheritance groupOfGroups = it.next();
                int numberOfViolations = validationContext.getFailingConstraints().size();
                Iterator<Group> it2 = groupOfGroups.iterator();
                while (it2.hasNext()) {
                    Group group3 = it2.next();
                    valueContext.setCurrentGroup(group3.getDefiningClass());
                    validateConstraintsForCurrentGroup(validationContext, valueContext);
                    if (shouldFailFast(validationContext)) {
                        return validationContext.getFailingConstraints();
                    }
                    validateCascadedConstraints(validationContext, valueContext);
                    if (shouldFailFast(validationContext)) {
                        return validationContext.getFailingConstraints();
                    }
                }
                if (validationContext.getFailingConstraints().size() > numberOfViolations) {
                    break;
                }
            }
        }
        return validationContext.getFailingConstraints();
    }

    private void validateConstraintsForCurrentGroup(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext) {
        if (!valueContext.validatingDefault()) {
            validateConstraintsForNonDefaultGroup(validationContext, valueContext);
        } else {
            validateConstraintsForDefaultGroup(validationContext, valueContext);
        }
    }

    private <U> void validateConstraintsForDefaultGroup(ValidationContext<?> validationContext, ValueContext<U, Object> valueContext) {
        BeanMetaData<U> beanMetaData = valueContext.getCurrentBeanMetaData();
        Map<Class<?>, Class<?>> validatedInterfaces = new HashMap<>();
        for (Class<? super U> clazz : beanMetaData.getClassHierarchy()) {
            BeanMetaData<? super U> hostingBeanMetaData = this.beanMetaDataManager.getBeanMetaData(clazz);
            boolean defaultGroupSequenceIsRedefined = hostingBeanMetaData.defaultGroupSequenceIsRedefined();
            if (defaultGroupSequenceIsRedefined) {
                Iterator<Sequence> defaultGroupSequence = hostingBeanMetaData.getDefaultValidationSequence(valueContext.getCurrentBean());
                Set<MetaConstraint<?>> metaConstraints = hostingBeanMetaData.getMetaConstraints();
                while (defaultGroupSequence.hasNext()) {
                    Iterator<GroupWithInheritance> it = defaultGroupSequence.next().iterator();
                    while (it.hasNext()) {
                        GroupWithInheritance groupOfGroups = it.next();
                        boolean validationSuccessful = true;
                        Iterator<Group> it2 = groupOfGroups.iterator();
                        while (it2.hasNext()) {
                            Group defaultSequenceMember = it2.next();
                            validationSuccessful = validateConstraintsForSingleDefaultGroupElement(validationContext, valueContext, validatedInterfaces, clazz, metaConstraints, defaultSequenceMember);
                        }
                        if (!validationSuccessful) {
                            break;
                        }
                    }
                }
            } else {
                Set<MetaConstraint<?>> metaConstraints2 = hostingBeanMetaData.getDirectMetaConstraints();
                validateConstraintsForSingleDefaultGroupElement(validationContext, valueContext, validatedInterfaces, clazz, metaConstraints2, Group.DEFAULT_GROUP);
            }
            validationContext.markCurrentBeanAsProcessed(valueContext);
            if (defaultGroupSequenceIsRedefined) {
                return;
            }
        }
    }

    private <U> boolean validateConstraintsForSingleDefaultGroupElement(ValidationContext<?> validationContext, ValueContext<U, Object> valueContext, Map<Class<?>, Class<?>> validatedInterfaces, Class<? super U> clazz, Set<MetaConstraint<?>> metaConstraints, Group defaultSequenceMember) {
        boolean validationSuccessful = true;
        valueContext.setCurrentGroup(defaultSequenceMember.getDefiningClass());
        for (MetaConstraint<?> metaConstraint : metaConstraints) {
            Class<?> declaringClass = metaConstraint.getLocation().getDeclaringClass();
            if (declaringClass.isInterface()) {
                Class<?> validatedForClass = validatedInterfaces.get(declaringClass);
                if (validatedForClass == null || validatedForClass.equals(clazz)) {
                    validatedInterfaces.put(declaringClass, clazz);
                }
            }
            boolean tmp = validateMetaConstraint(validationContext, valueContext, valueContext.getCurrentBean(), metaConstraint);
            if (shouldFailFast(validationContext)) {
                return false;
            }
            validationSuccessful = validationSuccessful && tmp;
        }
        return validationSuccessful;
    }

    private void validateConstraintsForNonDefaultGroup(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext) {
        validateMetaConstraints(validationContext, valueContext, valueContext.getCurrentBean(), valueContext.getCurrentBeanMetaData().getMetaConstraints());
        validationContext.markCurrentBeanAsProcessed(valueContext);
    }

    private void validateMetaConstraints(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext, Object parent, Iterable<MetaConstraint<?>> constraints) {
        for (MetaConstraint<?> metaConstraint : constraints) {
            validateMetaConstraint(validationContext, valueContext, parent, metaConstraint);
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
    }

    private boolean validateMetaConstraint(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext, Object parent, MetaConstraint<?> metaConstraint) {
        ValueContext.ValueState<Object> originalValueState = valueContext.getCurrentValueState();
        valueContext.appendNode(metaConstraint.getLocation());
        boolean success = true;
        if (isValidationRequired(validationContext, valueContext, metaConstraint)) {
            if (parent != null) {
                valueContext.setCurrentValidatedValue(valueContext.getValue(parent, metaConstraint.getLocation()));
            }
            success = metaConstraint.validateConstraint(validationContext, valueContext);
            validationContext.markConstraintProcessed(valueContext.getCurrentBean(), valueContext.getPropertyPath(), metaConstraint);
        }
        valueContext.resetValueState(originalValueState);
        return success;
    }

    private void validateCascadedConstraints(ValidationContext<?> validationContext, ValueContext<?, Object> valueContext) {
        Validatable validatable = valueContext.getCurrentValidatable();
        ValueContext.ValueState<Object> originalValueState = valueContext.getCurrentValueState();
        for (Cascadable cascadable : validatable.getCascadables()) {
            valueContext.appendNode(cascadable);
            ElementType elementType = cascadable.getElementType();
            if (isCascadeRequired(validationContext, valueContext.getCurrentBean(), valueContext.getPropertyPath(), elementType)) {
                Object value = getCascadableValue(validationContext, valueContext.getCurrentBean(), cascadable);
                CascadingMetaData cascadingMetaData = cascadable.getCascadingMetaData();
                if (value != null) {
                    CascadingMetaData effectiveCascadingMetaData = cascadingMetaData.addRuntimeContainerSupport(this.valueExtractorManager, value.getClass());
                    if (effectiveCascadingMetaData.isCascading()) {
                        validateCascadedAnnotatedObjectForCurrentGroup(value, validationContext, valueContext, effectiveCascadingMetaData);
                    }
                    if (effectiveCascadingMetaData.isContainer()) {
                        ContainerCascadingMetaData containerCascadingMetaData = (ContainerCascadingMetaData) effectiveCascadingMetaData.as(ContainerCascadingMetaData.class);
                        if (containerCascadingMetaData.hasContainerElementsMarkedForCascading()) {
                            validateCascadedContainerElementsForCurrentGroup(value, validationContext, valueContext, containerCascadingMetaData.getContainerElementTypesCascadingMetaData());
                        }
                    }
                }
            }
            valueContext.resetValueState(originalValueState);
        }
    }

    private void validateCascadedAnnotatedObjectForCurrentGroup(Object value, ValidationContext<?> validationContext, ValueContext<?, Object> valueContext, CascadingMetaData cascadingMetaData) {
        if (validationContext.isBeanAlreadyValidated(value, valueContext.getCurrentGroup(), valueContext.getPropertyPath()) || shouldFailFast(validationContext)) {
            return;
        }
        Class<?> originalGroup = valueContext.getCurrentGroup();
        Class<?> currentGroup = cascadingMetaData.convertGroup(originalGroup);
        ValidationOrder validationOrder = this.validationOrderGenerator.getValidationOrder(currentGroup, currentGroup != originalGroup);
        ValueContext<?, Object> cascadedValueContext = buildNewLocalExecutionContext(valueContext, value);
        validateInContext(validationContext, cascadedValueContext, validationOrder);
    }

    private void validateCascadedContainerElementsForCurrentGroup(Object value, ValidationContext<?> validationContext, ValueContext<?, ?> valueContext, List<ContainerCascadingMetaData> containerElementTypesCascadingMetaData) {
        for (ContainerCascadingMetaData cascadingMetaData : containerElementTypesCascadingMetaData) {
            if (cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
                ValueExtractorDescriptor extractor = this.valueExtractorManager.getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(cascadingMetaData.getEnclosingType(), cascadingMetaData.getTypeParameter(), value.getClass(), cascadingMetaData.getValueExtractorCandidates());
                if (extractor == null) {
                    throw LOG.getNoValueExtractorFoundForTypeException(cascadingMetaData.getEnclosingType(), cascadingMetaData.getTypeParameter(), value.getClass());
                }
                CascadingValueReceiver receiver = new CascadingValueReceiver(validationContext, valueContext, cascadingMetaData);
                ValueExtractorHelper.extractValues(extractor, value, receiver);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidatorImpl$CascadingValueReceiver.class */
    public class CascadingValueReceiver implements ValueExtractor.ValueReceiver {
        private final ValidationContext<?> validationContext;
        private final ValueContext<?, ?> valueContext;
        private final ContainerCascadingMetaData cascadingMetaData;

        public CascadingValueReceiver(ValidationContext<?> validationContext, ValueContext<?, ?> valueContext, ContainerCascadingMetaData cascadingMetaData) {
            ValidatorImpl.this = r4;
            this.validationContext = validationContext;
            this.valueContext = valueContext;
            this.cascadingMetaData = cascadingMetaData;
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void value(String nodeName, Object value) {
            doValidate(value, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void iterableValue(String nodeName, Object value) {
            this.valueContext.markCurrentPropertyAsIterable();
            doValidate(value, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void indexedValue(String nodeName, int index, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetIndex(Integer.valueOf(index));
            doValidate(value, nodeName);
        }

        @Override // javax.validation.valueextraction.ValueExtractor.ValueReceiver
        public void keyedValue(String nodeName, Object key, Object value) {
            this.valueContext.markCurrentPropertyAsIterableAndSetKey(key);
            doValidate(value, nodeName);
        }

        private void doValidate(Object value, String nodeName) {
            if (value == null || this.validationContext.isBeanAlreadyValidated(value, this.valueContext.getCurrentGroup(), this.valueContext.getPropertyPath()) || ValidatorImpl.this.shouldFailFast(this.validationContext)) {
                return;
            }
            Class<?> originalGroup = this.valueContext.getCurrentGroup();
            Class<?> currentGroup = this.cascadingMetaData.convertGroup(originalGroup);
            ValidationOrder validationOrder = ValidatorImpl.this.validationOrderGenerator.getValidationOrder(currentGroup, currentGroup != originalGroup);
            ValueContext<?, Object> cascadedValueContext = ValidatorImpl.this.buildNewLocalExecutionContext(this.valueContext, value);
            if (this.cascadingMetaData.getDeclaredContainerClass() != null) {
                cascadedValueContext.setTypeParameter(this.cascadingMetaData.getDeclaredContainerClass(), this.cascadingMetaData.getDeclaredTypeParameterIndex());
            }
            if (this.cascadingMetaData.isCascading()) {
                ValidatorImpl.this.validateInContext(this.validationContext, cascadedValueContext, validationOrder);
            }
            if (this.cascadingMetaData.hasContainerElementsMarkedForCascading()) {
                ValueContext<?, Object> cascadedTypeArgumentValueContext = ValidatorImpl.this.buildNewLocalExecutionContext(this.valueContext, value);
                if (this.cascadingMetaData.getTypeParameter() != null) {
                    cascadedValueContext.setTypeParameter(this.cascadingMetaData.getDeclaredContainerClass(), this.cascadingMetaData.getDeclaredTypeParameterIndex());
                }
                if (nodeName != null) {
                    cascadedTypeArgumentValueContext.appendTypeParameterNode(nodeName);
                }
                ValidatorImpl.this.validateCascadedContainerElementsInContext(value, this.validationContext, cascadedTypeArgumentValueContext, this.cascadingMetaData, validationOrder);
            }
        }
    }

    public void validateCascadedContainerElementsInContext(Object value, ValidationContext<?> validationContext, ValueContext<?, ?> valueContext, ContainerCascadingMetaData cascadingMetaData, ValidationOrder validationOrder) {
        Iterator<Group> groupIterator = validationOrder.getGroupIterator();
        while (groupIterator.hasNext()) {
            Group group = groupIterator.next();
            valueContext.setCurrentGroup(group.getDefiningClass());
            validateCascadedContainerElementsForCurrentGroup(value, validationContext, valueContext, cascadingMetaData.getContainerElementTypesCascadingMetaData());
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
        Iterator<Sequence> sequenceIterator = validationOrder.getSequenceIterator();
        while (sequenceIterator.hasNext()) {
            Sequence sequence = sequenceIterator.next();
            Iterator<GroupWithInheritance> it = sequence.iterator();
            while (it.hasNext()) {
                GroupWithInheritance groupOfGroups = it.next();
                int numberOfViolations = validationContext.getFailingConstraints().size();
                Iterator<Group> it2 = groupOfGroups.iterator();
                while (it2.hasNext()) {
                    Group group2 = it2.next();
                    valueContext.setCurrentGroup(group2.getDefiningClass());
                    validateCascadedContainerElementsForCurrentGroup(value, validationContext, valueContext, cascadingMetaData.getContainerElementTypesCascadingMetaData());
                    if (shouldFailFast(validationContext)) {
                        return;
                    }
                }
                if (validationContext.getFailingConstraints().size() > numberOfViolations) {
                    break;
                }
            }
        }
    }

    public ValueContext<?, Object> buildNewLocalExecutionContext(ValueContext<?, ?> valueContext, Object value) {
        ValueContext<?, Object> newValueContext;
        if (value != null) {
            newValueContext = ValueContext.getLocalExecutionContext(this.validatorScopedContext.getParameterNameProvider(), value, this.beanMetaDataManager.getBeanMetaData(value.getClass()), valueContext.getPropertyPath());
            newValueContext.setCurrentValidatedValue(value);
        } else {
            newValueContext = ValueContext.getLocalExecutionContext(this.validatorScopedContext.getParameterNameProvider(), (Class) valueContext.getCurrentBeanType(), valueContext.getCurrentBeanMetaData(), valueContext.getPropertyPath());
        }
        return newValueContext;
    }

    private <T> Set<ConstraintViolation<T>> validateValueInContext(ValidationContext<T> validationContext, Object value, PathImpl propertyPath, ValidationOrder validationOrder) {
        ValueContext<?, Object> valueContext = getValueContextForValueValidation(validationContext, propertyPath);
        valueContext.setCurrentValidatedValue(value);
        BeanMetaData<?> beanMetaData = valueContext.getCurrentBeanMetaData();
        if (beanMetaData.defaultGroupSequenceIsRedefined()) {
            validationOrder.assertDefaultGroupSequenceIsExpandable(beanMetaData.getDefaultGroupSequence(null));
        }
        Iterator<Group> groupIterator = validationOrder.getGroupIterator();
        while (groupIterator.hasNext()) {
            Group group = groupIterator.next();
            valueContext.setCurrentGroup(group.getDefiningClass());
            validateConstraintsForCurrentGroup(validationContext, valueContext);
            if (shouldFailFast(validationContext)) {
                return validationContext.getFailingConstraints();
            }
        }
        Iterator<Sequence> sequenceIterator = validationOrder.getSequenceIterator();
        while (sequenceIterator.hasNext()) {
            Sequence sequence = sequenceIterator.next();
            Iterator<GroupWithInheritance> it = sequence.iterator();
            while (it.hasNext()) {
                GroupWithInheritance groupOfGroups = it.next();
                int numberOfConstraintViolationsBefore = validationContext.getFailingConstraints().size();
                Iterator<Group> it2 = groupOfGroups.iterator();
                while (it2.hasNext()) {
                    Group group2 = it2.next();
                    valueContext.setCurrentGroup(group2.getDefiningClass());
                    validateConstraintsForCurrentGroup(validationContext, valueContext);
                    if (shouldFailFast(validationContext)) {
                        return validationContext.getFailingConstraints();
                    }
                }
                if (validationContext.getFailingConstraints().size() > numberOfConstraintViolationsBefore) {
                    break;
                }
            }
        }
        return validationContext.getFailingConstraints();
    }

    private <T> void validateParametersInContext(ValidationContext<T> validationContext, Object[] parameterValues, ValidationOrder validationOrder) {
        BeanMetaData<T> beanMetaData = validationContext.getRootBeanMetaData();
        Optional<ExecutableMetaData> executableMetaDataOptional = validationContext.getExecutableMetaData();
        if (!executableMetaDataOptional.isPresent()) {
            return;
        }
        ExecutableMetaData executableMetaData = executableMetaDataOptional.get();
        if (parameterValues.length != executableMetaData.getParameterTypes().length) {
            throw LOG.getInvalidParameterCountForExecutableException(ExecutableHelper.getExecutableAsString(executableMetaData.getType().toString() + "#" + executableMetaData.getName(), executableMetaData.getParameterTypes()), executableMetaData.getParameterTypes().length, parameterValues.length);
        }
        if (beanMetaData.defaultGroupSequenceIsRedefined()) {
            validationOrder.assertDefaultGroupSequenceIsExpandable(beanMetaData.getDefaultGroupSequence(validationContext.getRootBean()));
        }
        Iterator<Group> groupIterator = validationOrder.getGroupIterator();
        while (groupIterator.hasNext()) {
            validateParametersForGroup(validationContext, executableMetaData, parameterValues, groupIterator.next());
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
        ValueContext<Object[], Object> cascadingValueContext = ValueContext.getLocalExecutionContext(this.beanMetaDataManager, this.validatorScopedContext.getParameterNameProvider(), parameterValues, executableMetaData.getValidatableParametersMetaData(), PathImpl.createPathForExecutable(executableMetaData));
        Iterator<Group> groupIterator2 = validationOrder.getGroupIterator();
        while (groupIterator2.hasNext()) {
            cascadingValueContext.setCurrentGroup(groupIterator2.next().getDefiningClass());
            validateCascadedConstraints(validationContext, cascadingValueContext);
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
        Iterator<Sequence> sequenceIterator = validationOrder.getSequenceIterator();
        while (sequenceIterator.hasNext()) {
            Sequence sequence = sequenceIterator.next();
            Iterator<GroupWithInheritance> it = sequence.iterator();
            while (it.hasNext()) {
                GroupWithInheritance groupOfGroups = it.next();
                int numberOfViolations = validationContext.getFailingConstraints().size();
                Iterator<Group> it2 = groupOfGroups.iterator();
                while (it2.hasNext()) {
                    Group group = it2.next();
                    validateParametersForGroup(validationContext, executableMetaData, parameterValues, group);
                    if (shouldFailFast(validationContext)) {
                        return;
                    }
                    cascadingValueContext.setCurrentGroup(group.getDefiningClass());
                    validateCascadedConstraints(validationContext, cascadingValueContext);
                    if (shouldFailFast(validationContext)) {
                        return;
                    }
                }
                if (validationContext.getFailingConstraints().size() > numberOfViolations) {
                    break;
                }
            }
        }
    }

    private <T> void validateParametersForGroup(ValidationContext<T> validationContext, ExecutableMetaData executableMetaData, Object[] parameterValues, Group group) {
        Contracts.assertNotNull(executableMetaData, "executableMetaData may not be null");
        if (group.isDefaultGroup()) {
            Iterator<Sequence> defaultGroupSequence = validationContext.getRootBeanMetaData().getDefaultValidationSequence(validationContext.getRootBean());
            while (defaultGroupSequence.hasNext()) {
                Sequence sequence = defaultGroupSequence.next();
                int numberOfViolations = validationContext.getFailingConstraints().size();
                Iterator<GroupWithInheritance> it = sequence.iterator();
                while (it.hasNext()) {
                    GroupWithInheritance expandedGroup = it.next();
                    Iterator<Group> it2 = expandedGroup.iterator();
                    while (it2.hasNext()) {
                        Group defaultGroupSequenceElement = it2.next();
                        validateParametersForSingleGroup(validationContext, parameterValues, executableMetaData, defaultGroupSequenceElement.getDefiningClass());
                        if (shouldFailFast(validationContext)) {
                            return;
                        }
                    }
                    if (validationContext.getFailingConstraints().size() > numberOfViolations) {
                        return;
                    }
                }
            }
            return;
        }
        validateParametersForSingleGroup(validationContext, parameterValues, executableMetaData, group.getDefiningClass());
    }

    private <T> void validateParametersForSingleGroup(ValidationContext<T> validationContext, Object[] parameterValues, ExecutableMetaData executableMetaData, Class<?> currentValidatedGroup) {
        if (!executableMetaData.getCrossParameterConstraints().isEmpty()) {
            ValueContext<T, Object> valueContext = getExecutableValueContext(validationContext.getRootBean(), executableMetaData, executableMetaData.getValidatableParametersMetaData(), currentValidatedGroup);
            validateMetaConstraints(validationContext, valueContext, parameterValues, executableMetaData.getCrossParameterConstraints());
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
        ValueContext<T, Object> valueContext2 = getExecutableValueContext(validationContext.getRootBean(), executableMetaData, executableMetaData.getValidatableParametersMetaData(), currentValidatedGroup);
        for (int i = 0; i < parameterValues.length; i++) {
            ParameterMetaData parameterMetaData = executableMetaData.getParameterMetaData(i);
            Object value = parameterValues[i];
            if (value != null) {
                Class<?> valueType = value.getClass();
                if ((parameterMetaData.getType() instanceof Class) && ((Class) parameterMetaData.getType()).isPrimitive()) {
                    valueType = ReflectionHelper.unBoxedType(valueType);
                }
                if (!TypeHelper.isAssignable(TypeHelper.getErasedType(parameterMetaData.getType()), valueType)) {
                    throw LOG.getParameterTypesDoNotMatchException(valueType, parameterMetaData.getType(), i, validationContext.getExecutable());
                }
            }
            validateMetaConstraints(validationContext, valueContext2, parameterValues, parameterMetaData);
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
    }

    private <T> ValueContext<T, Object> getExecutableValueContext(T object, ExecutableMetaData executableMetaData, Validatable validatable, Class<?> group) {
        ValueContext<T, Object> valueContext;
        if (object != null) {
            valueContext = ValueContext.getLocalExecutionContext(this.beanMetaDataManager, this.validatorScopedContext.getParameterNameProvider(), object, validatable, PathImpl.createPathForExecutable(executableMetaData));
        } else {
            valueContext = ValueContext.getLocalExecutionContext(this.beanMetaDataManager, this.validatorScopedContext.getParameterNameProvider(), (Class) null, validatable, PathImpl.createPathForExecutable(executableMetaData));
        }
        valueContext.setCurrentGroup(group);
        return valueContext;
    }

    private <V, T> void validateReturnValueInContext(ValidationContext<T> validationContext, T bean, V value, ValidationOrder validationOrder) {
        BeanMetaData<T> beanMetaData = validationContext.getRootBeanMetaData();
        Optional<ExecutableMetaData> executableMetaDataOptional = validationContext.getExecutableMetaData();
        if (!executableMetaDataOptional.isPresent()) {
            return;
        }
        ExecutableMetaData executableMetaData = executableMetaDataOptional.get();
        if (beanMetaData.defaultGroupSequenceIsRedefined()) {
            validationOrder.assertDefaultGroupSequenceIsExpandable(beanMetaData.getDefaultGroupSequence(bean));
        }
        Iterator<Group> groupIterator = validationOrder.getGroupIterator();
        while (groupIterator.hasNext()) {
            validateReturnValueForGroup(validationContext, executableMetaData, bean, value, groupIterator.next());
            if (shouldFailFast(validationContext)) {
                return;
            }
        }
        ValueContext<?, Object> valueContext = null;
        if (value != null) {
            valueContext = ValueContext.getLocalExecutionContext(this.beanMetaDataManager, this.validatorScopedContext.getParameterNameProvider(), value, executableMetaData.getReturnValueMetaData(), PathImpl.createPathForExecutable(executableMetaData));
            Iterator<Group> groupIterator2 = validationOrder.getGroupIterator();
            while (groupIterator2.hasNext()) {
                valueContext.setCurrentGroup(groupIterator2.next().getDefiningClass());
                validateCascadedConstraints(validationContext, valueContext);
                if (shouldFailFast(validationContext)) {
                    return;
                }
            }
        }
        Iterator<Sequence> sequenceIterator = validationOrder.getSequenceIterator();
        while (sequenceIterator.hasNext()) {
            Sequence sequence = sequenceIterator.next();
            Iterator<GroupWithInheritance> it = sequence.iterator();
            while (it.hasNext()) {
                GroupWithInheritance groupOfGroups = it.next();
                int numberOfFailingConstraintsBeforeGroup = validationContext.getFailingConstraints().size();
                Iterator<Group> it2 = groupOfGroups.iterator();
                while (it2.hasNext()) {
                    Group group = it2.next();
                    validateReturnValueForGroup(validationContext, executableMetaData, bean, value, group);
                    if (shouldFailFast(validationContext)) {
                        return;
                    }
                    if (value != null) {
                        valueContext.setCurrentGroup(group.getDefiningClass());
                        validateCascadedConstraints(validationContext, valueContext);
                        if (shouldFailFast(validationContext)) {
                            return;
                        }
                    }
                }
                if (validationContext.getFailingConstraints().size() > numberOfFailingConstraintsBeforeGroup) {
                    break;
                }
            }
        }
    }

    private <T> void validateReturnValueForGroup(ValidationContext<T> validationContext, ExecutableMetaData executableMetaData, T bean, Object value, Group group) {
        Contracts.assertNotNull(executableMetaData, "executableMetaData may not be null");
        if (group.isDefaultGroup()) {
            Iterator<Sequence> defaultGroupSequence = validationContext.getRootBeanMetaData().getDefaultValidationSequence(bean);
            while (defaultGroupSequence.hasNext()) {
                Sequence sequence = defaultGroupSequence.next();
                int numberOfViolations = validationContext.getFailingConstraints().size();
                Iterator<GroupWithInheritance> it = sequence.iterator();
                while (it.hasNext()) {
                    GroupWithInheritance expandedGroup = it.next();
                    Iterator<Group> it2 = expandedGroup.iterator();
                    while (it2.hasNext()) {
                        Group defaultGroupSequenceElement = it2.next();
                        validateReturnValueForSingleGroup(validationContext, executableMetaData, bean, value, defaultGroupSequenceElement.getDefiningClass());
                        if (shouldFailFast(validationContext)) {
                            return;
                        }
                    }
                    if (validationContext.getFailingConstraints().size() > numberOfViolations) {
                        return;
                    }
                }
            }
            return;
        }
        validateReturnValueForSingleGroup(validationContext, executableMetaData, bean, value, group.getDefiningClass());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> void validateReturnValueForSingleGroup(ValidationContext<T> validationContext, ExecutableMetaData executableMetaData, T bean, Object value, Class<?> oneGroup) {
        ValueContext<?, Object> valueContext = getExecutableValueContext(executableMetaData.getKind() == ElementKind.CONSTRUCTOR ? value : bean, executableMetaData, executableMetaData.getReturnValueMetaData(), oneGroup);
        ReturnValueMetaData returnValueMetaData = executableMetaData.getReturnValueMetaData();
        validateMetaConstraints(validationContext, valueContext, value, returnValueMetaData);
    }

    private <V> ValueContext<?, V> getValueContextForPropertyValidation(ValidationContext<?> validationContext, PathImpl propertyPath) {
        Class<?> clazz = validationContext.getRootBeanClass();
        BeanMetaData<?> beanMetaData = validationContext.getRootBeanMetaData();
        Object value = validationContext.getRootBean();
        PropertyMetaData propertyMetaData = null;
        Iterator<Path.Node> propertyPathIter = propertyPath.iterator();
        while (propertyPathIter.hasNext()) {
            NodeImpl propertyPathNode = (NodeImpl) propertyPathIter.next();
            propertyMetaData = getBeanPropertyMetaData(beanMetaData, propertyPathNode);
            if (propertyPathIter.hasNext()) {
                if (!propertyMetaData.isCascading()) {
                    throw LOG.getInvalidPropertyPathException(validationContext.getRootBeanClass(), propertyPath.asString());
                }
                value = getCascadableValue(validationContext, value, propertyMetaData.getCascadables().iterator().next());
                if (value == null) {
                    throw LOG.getUnableToReachPropertyToValidateException(validationContext.getRootBean(), propertyPath);
                }
                clazz = value.getClass();
                if (propertyPathNode.isIterable()) {
                    NodeImpl propertyPathNode2 = (NodeImpl) propertyPathIter.next();
                    if (propertyPathNode2.getIndex() != null) {
                        value = ReflectionHelper.getIndexedValue(value, propertyPathNode2.getIndex().intValue());
                    } else if (propertyPathNode2.getKey() != null) {
                        value = ReflectionHelper.getMappedValue(value, propertyPathNode2.getKey());
                    } else {
                        throw LOG.getPropertyPathMustProvideIndexOrMapKeyException();
                    }
                    if (value == null) {
                        throw LOG.getUnableToReachPropertyToValidateException(validationContext.getRootBean(), propertyPath);
                    }
                    clazz = value.getClass();
                    beanMetaData = this.beanMetaDataManager.getBeanMetaData(clazz);
                    propertyMetaData = getBeanPropertyMetaData(beanMetaData, propertyPathNode2);
                } else {
                    beanMetaData = this.beanMetaDataManager.getBeanMetaData(clazz);
                }
            }
        }
        if (propertyMetaData == null) {
            throw LOG.getInvalidPropertyPathException(clazz, propertyPath.asString());
        }
        validationContext.setValidatedProperty(propertyMetaData.getName());
        propertyPath.removeLeafNode();
        return ValueContext.getLocalExecutionContext(this.validatorScopedContext.getParameterNameProvider(), value, beanMetaData, propertyPath);
    }

    private <V> ValueContext<?, V> getValueContextForValueValidation(ValidationContext<?> validationContext, PathImpl propertyPath) {
        Class<?> clazz = validationContext.getRootBeanClass();
        BeanMetaData<?> beanMetaData = null;
        PropertyMetaData propertyMetaData = null;
        Iterator<Path.Node> propertyPathIter = propertyPath.iterator();
        while (propertyPathIter.hasNext()) {
            NodeImpl propertyPathNode = (NodeImpl) propertyPathIter.next();
            beanMetaData = this.beanMetaDataManager.getBeanMetaData(clazz);
            propertyMetaData = getBeanPropertyMetaData(beanMetaData, propertyPathNode);
            if (propertyPathIter.hasNext()) {
                if (propertyPathNode.isIterable()) {
                    NodeImpl propertyPathNode2 = (NodeImpl) propertyPathIter.next();
                    clazz = ReflectionHelper.getClassFromType(ReflectionHelper.getCollectionElementType(propertyMetaData.getType()));
                    beanMetaData = this.beanMetaDataManager.getBeanMetaData(clazz);
                    propertyMetaData = getBeanPropertyMetaData(beanMetaData, propertyPathNode2);
                } else {
                    clazz = ReflectionHelper.getClassFromType(propertyMetaData.getType());
                }
            }
        }
        if (propertyMetaData == null) {
            throw LOG.getInvalidPropertyPathException(clazz, propertyPath.asString());
        }
        validationContext.setValidatedProperty(propertyMetaData.getName());
        propertyPath.removeLeafNode();
        return ValueContext.getLocalExecutionContext(this.validatorScopedContext.getParameterNameProvider(), (Class) clazz, beanMetaData, propertyPath);
    }

    private boolean isValidationRequired(ValidationContext<?> validationContext, ValueContext<?, ?> valueContext, MetaConstraint<?> metaConstraint) {
        if ((validationContext.getValidatedProperty() != null && !Objects.equals(validationContext.getValidatedProperty(), getPropertyName(metaConstraint.getLocation()))) || validationContext.hasMetaConstraintBeenProcessed(valueContext.getCurrentBean(), valueContext.getPropertyPath(), metaConstraint) || !metaConstraint.getGroupList().contains(valueContext.getCurrentGroup())) {
            return false;
        }
        return isReachable(validationContext, valueContext.getCurrentBean(), valueContext.getPropertyPath(), metaConstraint.getElementType());
    }

    private boolean isReachable(ValidationContext<?> validationContext, Object traversableObject, PathImpl path, ElementType type) {
        if (needToCallTraversableResolver(path, type)) {
            return true;
        }
        Path pathToObject = path.getPathWithoutLeafNode();
        try {
            return validationContext.getTraversableResolver().isReachable(traversableObject, path.getLeafNode(), validationContext.getRootBeanClass(), pathToObject, type);
        } catch (RuntimeException e) {
            throw LOG.getErrorDuringCallOfTraversableResolverIsReachableException(e);
        }
    }

    private boolean needToCallTraversableResolver(PathImpl path, ElementType type) {
        return isClassLevelConstraint(type) || isCrossParameterValidation(path) || isParameterValidation(path) || isReturnValueValidation(path);
    }

    private boolean isCascadeRequired(ValidationContext<?> validationContext, Object traversableObject, PathImpl path, ElementType type) {
        if (needToCallTraversableResolver(path, type)) {
            return true;
        }
        boolean isReachable = isReachable(validationContext, traversableObject, path, type);
        if (!isReachable) {
            return false;
        }
        Path pathToObject = path.getPathWithoutLeafNode();
        try {
            return validationContext.getTraversableResolver().isCascadable(traversableObject, path.getLeafNode(), validationContext.getRootBeanClass(), pathToObject, type);
        } catch (RuntimeException e) {
            throw LOG.getErrorDuringCallOfTraversableResolverIsCascadableException(e);
        }
    }

    private boolean isClassLevelConstraint(ElementType type) {
        return ElementType.TYPE.equals(type);
    }

    private boolean isCrossParameterValidation(PathImpl path) {
        return path.getLeafNode().getKind() == ElementKind.CROSS_PARAMETER;
    }

    private boolean isParameterValidation(PathImpl path) {
        return path.getLeafNode().getKind() == ElementKind.PARAMETER;
    }

    private boolean isReturnValueValidation(PathImpl path) {
        return path.getLeafNode().getKind() == ElementKind.RETURN_VALUE;
    }

    public boolean shouldFailFast(ValidationContext<?> validationContext) {
        return validationContext.isFailFastModeEnabled() && !validationContext.getFailingConstraints().isEmpty();
    }

    private PropertyMetaData getBeanPropertyMetaData(BeanMetaData<?> beanMetaData, Path.Node propertyNode) {
        if (!ElementKind.PROPERTY.equals(propertyNode.getKind())) {
            throw LOG.getInvalidPropertyPathException(beanMetaData.getBeanClass(), propertyNode.getName());
        }
        return beanMetaData.getMetaDataFor(propertyNode.getName());
    }

    private Object getCascadableValue(ValidationContext<?> validationContext, Object object, Cascadable cascadable) {
        return cascadable.getValue(object);
    }

    private String getPropertyName(ConstraintLocation location) {
        if (location instanceof TypeArgumentConstraintLocation) {
            location = ((TypeArgumentConstraintLocation) location).getOuterDelegate();
        }
        if (location instanceof FieldConstraintLocation) {
            return ((FieldConstraintLocation) location).getPropertyName();
        }
        if (location instanceof GetterConstraintLocation) {
            return ((GetterConstraintLocation) location).getPropertyName();
        }
        return null;
    }
}