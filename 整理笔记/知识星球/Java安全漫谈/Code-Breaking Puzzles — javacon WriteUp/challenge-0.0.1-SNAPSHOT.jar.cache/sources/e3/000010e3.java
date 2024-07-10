package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ElementKind;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.metadata.aggregated.PropertyMetaData;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.BeanDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ExecutableDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.classhierarchy.ClassHierarchyHelper;
import org.hibernate.validator.internal.util.classhierarchy.Filters;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/BeanMetaDataImpl.class */
public final class BeanMetaDataImpl<T> implements BeanMetaData<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final List<Class<?>> DEFAULT_GROUP_SEQUENCE = Collections.singletonList(Default.class);
    private final boolean hasConstraints;
    private final ValidationOrderGenerator validationOrderGenerator;
    private final Class<T> beanClass;
    private final Set<MetaConstraint<?>> allMetaConstraints;
    private final Set<MetaConstraint<?>> directMetaConstraints;
    private final Map<String, ExecutableMetaData> executableMetaDataMap;
    private final Set<String> unconstrainedExecutables;
    private final Map<String, PropertyMetaData> propertyMetaDataMap = CollectionHelper.newHashMap();
    private final Set<Cascadable> cascadedProperties;
    private final BeanDescriptor beanDescriptor;
    private final List<Class<?>> defaultGroupSequence;
    private final DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider;
    private final ValidationOrder validationOrder;
    private final List<Class<? super T>> classHierarchyWithoutInterfaces;

    public BeanMetaDataImpl(Class<T> beanClass, List<Class<?>> defaultGroupSequence, DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider, Set<ConstraintMetaData> constraintMetaDataSet, ValidationOrderGenerator validationOrderGenerator) {
        this.validationOrderGenerator = validationOrderGenerator;
        this.beanClass = beanClass;
        Set<PropertyMetaData> propertyMetaDataSet = CollectionHelper.newHashSet();
        Set<ExecutableMetaData> executableMetaDataSet = CollectionHelper.newHashSet();
        Set<String> tmpUnconstrainedExecutables = CollectionHelper.newHashSet();
        boolean hasConstraints = false;
        for (ConstraintMetaData constraintMetaData : constraintMetaDataSet) {
            boolean elementHasConstraints = constraintMetaData.isCascading() || constraintMetaData.isConstrained();
            hasConstraints |= elementHasConstraints;
            if (constraintMetaData.getKind() == ElementKind.PROPERTY) {
                propertyMetaDataSet.add((PropertyMetaData) constraintMetaData);
            } else {
                ExecutableMetaData executableMetaData = (ExecutableMetaData) constraintMetaData;
                if (elementHasConstraints) {
                    executableMetaDataSet.add(executableMetaData);
                } else {
                    tmpUnconstrainedExecutables.addAll(executableMetaData.getSignatures());
                }
            }
        }
        Set<Cascadable> cascadedProperties = CollectionHelper.newHashSet();
        Set<MetaConstraint<?>> allMetaConstraints = CollectionHelper.newHashSet();
        for (PropertyMetaData propertyMetaData : propertyMetaDataSet) {
            this.propertyMetaDataMap.put(propertyMetaData.getName(), propertyMetaData);
            cascadedProperties.addAll(propertyMetaData.getCascadables());
            allMetaConstraints.addAll(propertyMetaData.getAllConstraints());
        }
        this.hasConstraints = hasConstraints;
        this.cascadedProperties = CollectionHelper.toImmutableSet(cascadedProperties);
        this.allMetaConstraints = CollectionHelper.toImmutableSet(allMetaConstraints);
        this.classHierarchyWithoutInterfaces = CollectionHelper.toImmutableList(ClassHierarchyHelper.getHierarchy(beanClass, Filters.excludeInterfaces()));
        DefaultGroupSequenceContext<? super T> defaultGroupContext = getDefaultGroupSequenceData(beanClass, defaultGroupSequence, defaultGroupSequenceProvider, validationOrderGenerator);
        this.defaultGroupSequenceProvider = defaultGroupContext.defaultGroupSequenceProvider;
        this.defaultGroupSequence = CollectionHelper.toImmutableList(defaultGroupContext.defaultGroupSequence);
        this.validationOrder = defaultGroupContext.validationOrder;
        this.directMetaConstraints = getDirectConstraints();
        this.executableMetaDataMap = CollectionHelper.toImmutableMap(bySignature(executableMetaDataSet));
        this.unconstrainedExecutables = CollectionHelper.toImmutableSet(tmpUnconstrainedExecutables);
        boolean defaultGroupSequenceIsRedefined = defaultGroupSequenceIsRedefined();
        List<Class<?>> resolvedDefaultGroupSequence = getDefaultGroupSequence(null);
        Map<String, PropertyDescriptor> propertyDescriptors = getConstrainedPropertiesAsDescriptors(this.propertyMetaDataMap, defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence);
        Map<String, ExecutableDescriptorImpl> methodsDescriptors = getConstrainedMethodsAsDescriptors(this.executableMetaDataMap, defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence);
        Map<String, ConstructorDescriptor> constructorsDescriptors = getConstrainedConstructorsAsDescriptors(this.executableMetaDataMap, defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence);
        this.beanDescriptor = new BeanDescriptorImpl(beanClass, getClassLevelConstraintsAsDescriptors(allMetaConstraints), propertyDescriptors, methodsDescriptors, constructorsDescriptors, defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public Class<T> getBeanClass() {
        return this.beanClass;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public boolean hasConstraints() {
        return this.hasConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public BeanDescriptor getBeanDescriptor() {
        return this.beanDescriptor;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public Set<Cascadable> getCascadables() {
        return this.cascadedProperties;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public boolean hasCascadables() {
        return !this.cascadedProperties.isEmpty();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public PropertyMetaData getMetaDataFor(String propertyName) {
        PropertyMetaData propertyMetaData = this.propertyMetaDataMap.get(propertyName);
        if (propertyMetaData == null) {
            throw LOG.getPropertyNotDefinedByValidatedTypeException(this.beanClass, propertyName);
        }
        return propertyMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public Set<MetaConstraint<?>> getMetaConstraints() {
        return this.allMetaConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public Set<MetaConstraint<?>> getDirectMetaConstraints() {
        return this.directMetaConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public Optional<ExecutableMetaData> getMetaDataFor(Executable executable) {
        String signature = ExecutableHelper.getSignature(executable);
        if (this.unconstrainedExecutables.contains(signature)) {
            return Optional.empty();
        }
        ExecutableMetaData executableMetaData = this.executableMetaDataMap.get(ExecutableHelper.getSignature(executable));
        if (executableMetaData == null) {
            throw LOG.getMethodOrConstructorNotDefinedByValidatedTypeException(this.beanClass, executable);
        }
        return Optional.of(executableMetaData);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public List<Class<?>> getDefaultGroupSequence(T beanState) {
        if (hasDefaultGroupSequenceProvider()) {
            List<Class<?>> providerDefaultGroupSequence = this.defaultGroupSequenceProvider.getValidationGroups(beanState);
            return getValidDefaultGroupSequence(this.beanClass, providerDefaultGroupSequence);
        }
        return this.defaultGroupSequence;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public Iterator<Sequence> getDefaultValidationSequence(T beanState) {
        if (hasDefaultGroupSequenceProvider()) {
            List<Class<?>> providerDefaultGroupSequence = this.defaultGroupSequenceProvider.getValidationGroups(beanState);
            return this.validationOrderGenerator.getDefaultValidationOrder(this.beanClass, getValidDefaultGroupSequence(this.beanClass, providerDefaultGroupSequence)).getSequenceIterator();
        }
        return this.validationOrder.getSequenceIterator();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public boolean defaultGroupSequenceIsRedefined() {
        return this.defaultGroupSequence.size() > 1 || hasDefaultGroupSequenceProvider();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.BeanMetaData
    public List<Class<? super T>> getClassHierarchy() {
        return this.classHierarchyWithoutInterfaces;
    }

    private static Set<ConstraintDescriptorImpl<?>> getClassLevelConstraintsAsDescriptors(Set<MetaConstraint<?>> constraints) {
        return (Set) constraints.stream().filter(c -> {
            return c.getElementType() == ElementType.TYPE;
        }).map((v0) -> {
            return v0.getDescriptor();
        }).collect(Collectors.toSet());
    }

    private static Map<String, PropertyDescriptor> getConstrainedPropertiesAsDescriptors(Map<String, PropertyMetaData> propertyMetaDataMap, boolean defaultGroupSequenceIsRedefined, List<Class<?>> resolvedDefaultGroupSequence) {
        Map<String, PropertyDescriptor> theValue = CollectionHelper.newHashMap();
        for (Map.Entry<String, PropertyMetaData> entry : propertyMetaDataMap.entrySet()) {
            if (entry.getValue().isConstrained() && entry.getValue().getName() != null) {
                theValue.put(entry.getKey(), entry.getValue().asDescriptor(defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence));
            }
        }
        return theValue;
    }

    private static Map<String, ExecutableDescriptorImpl> getConstrainedMethodsAsDescriptors(Map<String, ExecutableMetaData> executableMetaDataMap, boolean defaultGroupSequenceIsRedefined, List<Class<?>> resolvedDefaultGroupSequence) {
        Map<String, ExecutableDescriptorImpl> constrainedMethodDescriptors = CollectionHelper.newHashMap();
        for (ExecutableMetaData executableMetaData : executableMetaDataMap.values()) {
            if (executableMetaData.getKind() == ElementKind.METHOD && executableMetaData.isConstrained()) {
                ExecutableDescriptorImpl descriptor = executableMetaData.asDescriptor(defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence);
                for (String signature : executableMetaData.getSignatures()) {
                    constrainedMethodDescriptors.put(signature, descriptor);
                }
            }
        }
        return constrainedMethodDescriptors;
    }

    private static Map<String, ConstructorDescriptor> getConstrainedConstructorsAsDescriptors(Map<String, ExecutableMetaData> executableMetaDataMap, boolean defaultGroupSequenceIsRedefined, List<Class<?>> resolvedDefaultGroupSequence) {
        Map<String, ConstructorDescriptor> constrainedMethodDescriptors = CollectionHelper.newHashMap();
        for (ExecutableMetaData executableMetaData : executableMetaDataMap.values()) {
            if (executableMetaData.getKind() == ElementKind.CONSTRUCTOR && executableMetaData.isConstrained()) {
                constrainedMethodDescriptors.put(executableMetaData.getSignatures().iterator().next(), executableMetaData.asDescriptor(defaultGroupSequenceIsRedefined, resolvedDefaultGroupSequence));
            }
        }
        return constrainedMethodDescriptors;
    }

    private static <T> DefaultGroupSequenceContext<T> getDefaultGroupSequenceData(Class<?> beanClass, List<Class<?>> defaultGroupSequence, DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider, ValidationOrderGenerator validationOrderGenerator) {
        if (defaultGroupSequence != null && defaultGroupSequenceProvider != null) {
            throw LOG.getInvalidDefaultGroupSequenceDefinitionException();
        }
        DefaultGroupSequenceContext<T> context = new DefaultGroupSequenceContext<>();
        if (defaultGroupSequenceProvider != null) {
            context.defaultGroupSequenceProvider = defaultGroupSequenceProvider;
            context.defaultGroupSequence = Collections.emptyList();
            context.validationOrder = null;
        } else if (defaultGroupSequence != null && !defaultGroupSequence.isEmpty()) {
            context.defaultGroupSequence = getValidDefaultGroupSequence(beanClass, defaultGroupSequence);
            context.validationOrder = validationOrderGenerator.getDefaultValidationOrder(beanClass, context.defaultGroupSequence);
        } else {
            context.defaultGroupSequence = DEFAULT_GROUP_SEQUENCE;
            context.validationOrder = ValidationOrder.DEFAULT_SEQUENCE;
        }
        return context;
    }

    private Set<MetaConstraint<?>> getDirectConstraints() {
        Set<MetaConstraint<?>> constraints = CollectionHelper.newHashSet();
        Set<Class<?>> classAndInterfaces = CollectionHelper.newHashSet();
        classAndInterfaces.add(this.beanClass);
        classAndInterfaces.addAll(ClassHierarchyHelper.getDirectlyImplementedInterfaces(this.beanClass));
        for (Class<?> clazz : classAndInterfaces) {
            for (MetaConstraint<?> metaConstraint : this.allMetaConstraints) {
                if (metaConstraint.getLocation().getDeclaringClass().equals(clazz)) {
                    constraints.add(metaConstraint);
                }
            }
        }
        return CollectionHelper.toImmutableSet(constraints);
    }

    private Map<String, ExecutableMetaData> bySignature(Set<ExecutableMetaData> executables) {
        Map<String, ExecutableMetaData> theValue = CollectionHelper.newHashMap();
        for (ExecutableMetaData executableMetaData : executables) {
            for (String signature : executableMetaData.getSignatures()) {
                theValue.put(signature, executableMetaData);
            }
        }
        return theValue;
    }

    private static List<Class<?>> getValidDefaultGroupSequence(Class<?> beanClass, List<Class<?>> groupSequence) {
        List<Class<?>> validDefaultGroupSequence = new ArrayList<>();
        boolean groupSequenceContainsDefault = false;
        if (groupSequence != null) {
            for (Class<?> group : groupSequence) {
                if (group.getName().equals(beanClass.getName())) {
                    validDefaultGroupSequence.add(Default.class);
                    groupSequenceContainsDefault = true;
                } else if (group.getName().equals(Default.class.getName())) {
                    throw LOG.getNoDefaultGroupInGroupSequenceException();
                } else {
                    validDefaultGroupSequence.add(group);
                }
            }
        }
        if (!groupSequenceContainsDefault) {
            throw LOG.getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException(beanClass);
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Members of the default group sequence for bean %s are: %s.", beanClass.getName(), validDefaultGroupSequence);
        }
        return validDefaultGroupSequence;
    }

    private boolean hasDefaultGroupSequenceProvider() {
        return this.defaultGroupSequenceProvider != null;
    }

    public String toString() {
        return "BeanMetaDataImpl{beanClass=" + this.beanClass.getSimpleName() + ", constraintCount=" + getMetaConstraints().size() + ", cascadedPropertiesCount=" + this.cascadedProperties.size() + ", defaultGroupSequence=" + getDefaultGroupSequence(null) + '}';
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/BeanMetaDataImpl$BeanMetaDataBuilder.class */
    public static class BeanMetaDataBuilder<T> {
        private final ConstraintHelper constraintHelper;
        private final ValidationOrderGenerator validationOrderGenerator;
        private final Class<T> beanClass;
        private final Set<BuilderDelegate> builders = CollectionHelper.newHashSet();
        private final ExecutableHelper executableHelper;
        private final TypeResolutionHelper typeResolutionHelper;
        private final ValueExtractorManager valueExtractorManager;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final MethodValidationConfiguration methodValidationConfiguration;
        private ConfigurationSource sequenceSource;
        private ConfigurationSource providerSource;
        private List<Class<?>> defaultGroupSequence;
        private DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider;

        private BeanMetaDataBuilder(ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider, ValidationOrderGenerator validationOrderGenerator, Class<T> beanClass, MethodValidationConfiguration methodValidationConfiguration) {
            this.beanClass = beanClass;
            this.constraintHelper = constraintHelper;
            this.validationOrderGenerator = validationOrderGenerator;
            this.executableHelper = executableHelper;
            this.typeResolutionHelper = typeResolutionHelper;
            this.valueExtractorManager = valueExtractorManager;
            this.parameterNameProvider = parameterNameProvider;
            this.methodValidationConfiguration = methodValidationConfiguration;
        }

        public static <T> BeanMetaDataBuilder<T> getInstance(ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider, ValidationOrderGenerator validationOrderGenerator, Class<T> beanClass, MethodValidationConfiguration methodValidationConfiguration) {
            return new BeanMetaDataBuilder<>(constraintHelper, executableHelper, typeResolutionHelper, valueExtractorManager, parameterNameProvider, validationOrderGenerator, beanClass, methodValidationConfiguration);
        }

        public void add(BeanConfiguration<? super T> configuration) {
            if (configuration.getBeanClass().equals(this.beanClass)) {
                if (configuration.getDefaultGroupSequence() != null && (this.sequenceSource == null || configuration.getSource().getPriority() >= this.sequenceSource.getPriority())) {
                    this.sequenceSource = configuration.getSource();
                    this.defaultGroupSequence = configuration.getDefaultGroupSequence();
                }
                if (configuration.getDefaultGroupSequenceProvider() != null && (this.providerSource == null || configuration.getSource().getPriority() >= this.providerSource.getPriority())) {
                    this.providerSource = configuration.getSource();
                    this.defaultGroupSequenceProvider = (DefaultGroupSequenceProvider<? super Object>) configuration.getDefaultGroupSequenceProvider();
                }
            }
            for (ConstrainedElement constrainedElement : configuration.getConstrainedElements()) {
                addMetaDataToBuilder(constrainedElement, this.builders);
            }
        }

        private void addMetaDataToBuilder(ConstrainedElement constrainableElement, Set<BuilderDelegate> builders) {
            for (BuilderDelegate builder : builders) {
                boolean foundBuilder = builder.add(constrainableElement);
                if (foundBuilder) {
                    return;
                }
            }
            builders.add(new BuilderDelegate(this.beanClass, constrainableElement, this.constraintHelper, this.executableHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider, this.methodValidationConfiguration));
        }

        public BeanMetaDataImpl<T> build() {
            Set<ConstraintMetaData> aggregatedElements = CollectionHelper.newHashSet();
            for (BuilderDelegate builder : this.builders) {
                aggregatedElements.addAll(builder.build());
            }
            return new BeanMetaDataImpl<>(this.beanClass, this.defaultGroupSequence, this.defaultGroupSequenceProvider, aggregatedElements, this.validationOrderGenerator);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/BeanMetaDataImpl$BuilderDelegate.class */
    public static class BuilderDelegate {
        private final Class<?> beanClass;
        private final ConstrainedElement constrainedElement;
        private final ConstraintHelper constraintHelper;
        private final ExecutableHelper executableHelper;
        private final TypeResolutionHelper typeResolutionHelper;
        private final ValueExtractorManager valueExtractorManager;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private MetaDataBuilder propertyBuilder;
        private ExecutableMetaData.Builder methodBuilder;
        private final MethodValidationConfiguration methodValidationConfiguration;
        private final int hashCode;

        public BuilderDelegate(Class<?> beanClass, ConstrainedElement constrainedElement, ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider, MethodValidationConfiguration methodValidationConfiguration) {
            this.beanClass = beanClass;
            this.constrainedElement = constrainedElement;
            this.constraintHelper = constraintHelper;
            this.executableHelper = executableHelper;
            this.typeResolutionHelper = typeResolutionHelper;
            this.valueExtractorManager = valueExtractorManager;
            this.parameterNameProvider = parameterNameProvider;
            this.methodValidationConfiguration = methodValidationConfiguration;
            switch (constrainedElement.getKind()) {
                case FIELD:
                    ConstrainedField constrainedField = (ConstrainedField) constrainedElement;
                    this.propertyBuilder = new PropertyMetaData.Builder(beanClass, constrainedField, constraintHelper, typeResolutionHelper, valueExtractorManager);
                    break;
                case CONSTRUCTOR:
                case METHOD:
                    ConstrainedExecutable constrainedExecutable = (ConstrainedExecutable) constrainedElement;
                    Member member = constrainedExecutable.getExecutable();
                    if (!Modifier.isPrivate(member.getModifiers()) || beanClass == member.getDeclaringClass()) {
                        this.methodBuilder = new ExecutableMetaData.Builder(beanClass, constrainedExecutable, constraintHelper, executableHelper, typeResolutionHelper, valueExtractorManager, parameterNameProvider, methodValidationConfiguration);
                    }
                    if (constrainedExecutable.isGetterMethod()) {
                        this.propertyBuilder = new PropertyMetaData.Builder(beanClass, constrainedExecutable, constraintHelper, typeResolutionHelper, valueExtractorManager);
                        break;
                    }
                    break;
                case TYPE:
                    ConstrainedType constrainedType = (ConstrainedType) constrainedElement;
                    this.propertyBuilder = new PropertyMetaData.Builder(beanClass, constrainedType, constraintHelper, typeResolutionHelper, valueExtractorManager);
                    break;
            }
            this.hashCode = buildHashCode();
        }

        public boolean add(ConstrainedElement constrainedElement) {
            boolean added = false;
            if (this.methodBuilder != null && this.methodBuilder.accepts(constrainedElement)) {
                this.methodBuilder.add(constrainedElement);
                added = true;
            }
            if (this.propertyBuilder != null && this.propertyBuilder.accepts(constrainedElement)) {
                this.propertyBuilder.add(constrainedElement);
                if (!added && constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD && this.methodBuilder == null) {
                    ConstrainedExecutable constrainedMethod = (ConstrainedExecutable) constrainedElement;
                    this.methodBuilder = new ExecutableMetaData.Builder(this.beanClass, constrainedMethod, this.constraintHelper, this.executableHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider, this.methodValidationConfiguration);
                }
                added = true;
            }
            return added;
        }

        public Set<ConstraintMetaData> build() {
            Set<ConstraintMetaData> metaDataSet = CollectionHelper.newHashSet();
            if (this.propertyBuilder != null) {
                metaDataSet.add(this.propertyBuilder.build());
            }
            if (this.methodBuilder != null) {
                metaDataSet.add(this.methodBuilder.build());
            }
            return metaDataSet;
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int buildHashCode() {
            int result = (31 * 1) + this.beanClass.hashCode();
            return (31 * result) + this.constrainedElement.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj) || getClass() != obj.getClass()) {
                return false;
            }
            BuilderDelegate other = (BuilderDelegate) obj;
            if (!this.beanClass.equals(other.beanClass) || !this.constrainedElement.equals(other.constrainedElement)) {
                return false;
            }
            return true;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/BeanMetaDataImpl$DefaultGroupSequenceContext.class */
    public static class DefaultGroupSequenceContext<T> {
        List<Class<?>> defaultGroupSequence;
        DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider;
        ValidationOrder validationOrder;

        private DefaultGroupSequenceContext() {
        }
    }
}