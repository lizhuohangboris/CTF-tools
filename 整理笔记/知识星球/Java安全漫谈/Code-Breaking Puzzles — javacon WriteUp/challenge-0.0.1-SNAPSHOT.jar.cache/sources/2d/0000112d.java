package org.hibernate.validator.internal.metadata.provider;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.hibernate.validator.internal.engine.valueextraction.ArrayElement;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptionsImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;
import org.hibernate.validator.internal.metadata.raw.ConfigurationSource;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredConstructors;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredFields;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;
import org.hibernate.validator.internal.util.privilegedactions.GetMethods;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider.class */
public class AnnotationMetaDataProvider implements MetaDataProvider {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Annotation[] EMPTY_PARAMETER_ANNOTATIONS = new Annotation[0];
    private final ConstraintHelper constraintHelper;
    private final TypeResolutionHelper typeResolutionHelper;
    private final AnnotationProcessingOptions annotationProcessingOptions;
    private final ValueExtractorManager valueExtractorManager;
    private final BeanConfiguration<Object> objectBeanConfiguration = retrieveBeanConfiguration(Object.class);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider$TypeArgumentLocation.class */
    public interface TypeArgumentLocation {
        ConstraintLocation toConstraintLocation();
    }

    public AnnotationMetaDataProvider(ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, AnnotationProcessingOptions annotationProcessingOptions) {
        this.constraintHelper = constraintHelper;
        this.typeResolutionHelper = typeResolutionHelper;
        this.valueExtractorManager = valueExtractorManager;
        this.annotationProcessingOptions = annotationProcessingOptions;
    }

    @Override // org.hibernate.validator.internal.metadata.provider.MetaDataProvider
    public AnnotationProcessingOptions getAnnotationProcessingOptions() {
        return new AnnotationProcessingOptionsImpl();
    }

    @Override // org.hibernate.validator.internal.metadata.provider.MetaDataProvider
    public <T> BeanConfiguration<T> getBeanConfiguration(Class<T> beanClass) {
        if (Object.class.equals(beanClass)) {
            return (BeanConfiguration<T>) this.objectBeanConfiguration;
        }
        return retrieveBeanConfiguration(beanClass);
    }

    private <T> BeanConfiguration<T> retrieveBeanConfiguration(Class<T> beanClass) {
        Set<ConstrainedElement> constrainedElements = getFieldMetaData(beanClass);
        constrainedElements.addAll(getMethodMetaData(beanClass));
        constrainedElements.addAll(getConstructorMetaData(beanClass));
        Set<MetaConstraint<?>> classLevelConstraints = getClassLevelConstraints(beanClass);
        if (!classLevelConstraints.isEmpty()) {
            ConstrainedType classLevelMetaData = new ConstrainedType(ConfigurationSource.ANNOTATION, beanClass, classLevelConstraints);
            constrainedElements.add(classLevelMetaData);
        }
        return new BeanConfiguration<>(ConfigurationSource.ANNOTATION, beanClass, constrainedElements, getDefaultGroupSequence(beanClass), getDefaultGroupSequenceProvider(beanClass));
    }

    private List<Class<?>> getDefaultGroupSequence(Class<?> beanClass) {
        GroupSequence groupSequenceAnnotation = (GroupSequence) beanClass.getAnnotation(GroupSequence.class);
        if (groupSequenceAnnotation != null) {
            return Arrays.asList(groupSequenceAnnotation.value());
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> DefaultGroupSequenceProvider<? super T> getDefaultGroupSequenceProvider(Class<T> beanClass) {
        GroupSequenceProvider groupSequenceProviderAnnotation = (GroupSequenceProvider) beanClass.getAnnotation(GroupSequenceProvider.class);
        if (groupSequenceProviderAnnotation != null) {
            return newGroupSequenceProviderClassInstance(beanClass, groupSequenceProviderAnnotation.value());
        }
        return null;
    }

    private <T> DefaultGroupSequenceProvider<? super T> newGroupSequenceProviderClassInstance(Class<T> beanClass, Class<? extends DefaultGroupSequenceProvider<? super T>> providerClass) {
        Method[] providerMethods = (Method[]) run(GetMethods.action(providerClass));
        for (Method method : providerMethods) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if ("getValidationGroups".equals(method.getName()) && !method.isBridge() && paramTypes.length == 1 && paramTypes[0].isAssignableFrom(beanClass)) {
                return (DefaultGroupSequenceProvider) run(NewInstance.action(providerClass, "the default group sequence provider"));
            }
        }
        throw LOG.getWrongDefaultGroupSequenceProviderTypeException(beanClass);
    }

    private Set<MetaConstraint<?>> getClassLevelConstraints(Class<?> clazz) {
        if (this.annotationProcessingOptions.areClassLevelConstraintsIgnoredFor(clazz)) {
            return Collections.emptySet();
        }
        Set<MetaConstraint<?>> classLevelConstraints = CollectionHelper.newHashSet();
        List<ConstraintDescriptorImpl<?>> classMetaData = findClassLevelConstraints(clazz);
        ConstraintLocation location = ConstraintLocation.forClass(clazz);
        for (ConstraintDescriptorImpl<?> constraintDescription : classMetaData) {
            classLevelConstraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescription, location));
        }
        return classLevelConstraints;
    }

    private Set<ConstrainedElement> getFieldMetaData(Class<?> beanClass) {
        Field[] fieldArr;
        Set<ConstrainedElement> propertyMetaData = CollectionHelper.newHashSet();
        for (Field field : (Field[]) run(GetDeclaredFields.action(beanClass))) {
            if (!Modifier.isStatic(field.getModifiers()) && !this.annotationProcessingOptions.areMemberConstraintsIgnoredFor(field) && !field.isSynthetic()) {
                propertyMetaData.add(findPropertyMetaData(field));
            }
        }
        return propertyMetaData;
    }

    private ConstrainedField findPropertyMetaData(Field field) {
        Set<MetaConstraint<?>> constraints = convertToMetaConstraints(findConstraints(field, ElementType.FIELD), field);
        CascadingMetaDataBuilder cascadingMetaDataBuilder = findCascadingMetaData(field);
        Set<MetaConstraint<?>> typeArgumentsConstraints = findTypeAnnotationConstraints(field);
        return new ConstrainedField(ConfigurationSource.ANNOTATION, field, constraints, typeArgumentsConstraints, cascadingMetaDataBuilder);
    }

    private Set<MetaConstraint<?>> convertToMetaConstraints(List<ConstraintDescriptorImpl<?>> constraintDescriptors, Field field) {
        if (constraintDescriptors.isEmpty()) {
            return Collections.emptySet();
        }
        Set<MetaConstraint<?>> constraints = CollectionHelper.newHashSet();
        ConstraintLocation location = ConstraintLocation.forField(field);
        for (ConstraintDescriptorImpl<?> constraintDescription : constraintDescriptors) {
            constraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescription, location));
        }
        return constraints;
    }

    private Set<ConstrainedExecutable> getConstructorMetaData(Class<?> clazz) {
        Executable[] declaredConstructors = (Executable[]) run(GetDeclaredConstructors.action(clazz));
        return getMetaData(declaredConstructors);
    }

    private Set<ConstrainedExecutable> getMethodMetaData(Class<?> clazz) {
        Executable[] declaredMethods = (Executable[]) run(GetDeclaredMethods.action(clazz));
        return getMetaData(declaredMethods);
    }

    private Set<ConstrainedExecutable> getMetaData(Executable[] executableElements) {
        Set<ConstrainedExecutable> executableMetaData = CollectionHelper.newHashSet();
        for (Executable executable : executableElements) {
            if (!Modifier.isStatic(executable.getModifiers()) && !executable.isSynthetic()) {
                executableMetaData.add(findExecutableMetaData(executable));
            }
        }
        return executableMetaData;
    }

    private ConstrainedExecutable findExecutableMetaData(Executable executable) {
        Set<MetaConstraint<?>> crossParameterConstraints;
        Set<MetaConstraint<?>> typeArgumentsConstraints;
        Set<MetaConstraint<?>> returnValueConstraints;
        CascadingMetaDataBuilder cascadingMetaDataBuilder;
        List<ConstrainedParameter> parameterConstraints = getParameterMetaData(executable);
        Map<ConstraintDescriptorImpl.ConstraintType, List<ConstraintDescriptorImpl<?>>> executableConstraints = (Map) findConstraints(executable, ExecutableHelper.getElementType(executable)).stream().collect(Collectors.groupingBy((v0) -> {
            return v0.getConstraintType();
        }));
        if (this.annotationProcessingOptions.areCrossParameterConstraintsIgnoredFor(executable)) {
            crossParameterConstraints = Collections.emptySet();
        } else {
            crossParameterConstraints = convertToMetaConstraints(executableConstraints.get(ConstraintDescriptorImpl.ConstraintType.CROSS_PARAMETER), executable);
        }
        if (this.annotationProcessingOptions.areReturnValueConstraintsIgnoredFor(executable)) {
            returnValueConstraints = Collections.emptySet();
            typeArgumentsConstraints = Collections.emptySet();
            cascadingMetaDataBuilder = CascadingMetaDataBuilder.nonCascading();
        } else {
            AnnotatedType annotatedReturnType = executable.getAnnotatedReturnType();
            typeArgumentsConstraints = findTypeAnnotationConstraints(executable, annotatedReturnType);
            returnValueConstraints = convertToMetaConstraints(executableConstraints.get(ConstraintDescriptorImpl.ConstraintType.GENERIC), executable);
            cascadingMetaDataBuilder = findCascadingMetaData(executable, annotatedReturnType);
        }
        return new ConstrainedExecutable(ConfigurationSource.ANNOTATION, executable, parameterConstraints, crossParameterConstraints, returnValueConstraints, typeArgumentsConstraints, cascadingMetaDataBuilder);
    }

    private Set<MetaConstraint<?>> convertToMetaConstraints(List<ConstraintDescriptorImpl<?>> constraintsDescriptors, Executable executable) {
        if (constraintsDescriptors == null) {
            return Collections.emptySet();
        }
        Set<MetaConstraint<?>> constraints = CollectionHelper.newHashSet();
        ConstraintLocation returnValueLocation = ConstraintLocation.forReturnValue(executable);
        ConstraintLocation crossParameterLocation = ConstraintLocation.forCrossParameter(executable);
        for (ConstraintDescriptorImpl<?> constraintDescriptor : constraintsDescriptors) {
            ConstraintLocation location = constraintDescriptor.getConstraintType() == ConstraintDescriptorImpl.ConstraintType.GENERIC ? returnValueLocation : crossParameterLocation;
            constraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptor, location));
        }
        return constraints;
    }

    private List<ConstrainedParameter> getParameterMetaData(Executable executable) {
        Annotation[] parameterAnnotations;
        Annotation[] annotationArr;
        if (executable.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        Parameter[] parameters = executable.getParameters();
        List<ConstrainedParameter> metaData = new ArrayList<>(parameters.length);
        int i = 0;
        for (Parameter parameter : parameters) {
            try {
                parameterAnnotations = parameter.getAnnotations();
            } catch (ArrayIndexOutOfBoundsException ex) {
                LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
                parameterAnnotations = EMPTY_PARAMETER_ANNOTATIONS;
            }
            Set<MetaConstraint<?>> parameterConstraints = CollectionHelper.newHashSet();
            if (this.annotationProcessingOptions.areParameterConstraintsIgnoredFor(executable, i)) {
                Type type = ReflectionHelper.typeOf(executable, i);
                metaData.add(new ConstrainedParameter(ConfigurationSource.ANNOTATION, executable, type, i, parameterConstraints, Collections.emptySet(), CascadingMetaDataBuilder.nonCascading()));
            } else {
                ConstraintLocation location = ConstraintLocation.forParameter(executable, i);
                for (Annotation parameterAnnotation : parameterAnnotations) {
                    List<ConstraintDescriptorImpl<?>> constraints = findConstraintAnnotations(executable, parameterAnnotation, ElementType.PARAMETER);
                    for (ConstraintDescriptorImpl<?> constraintDescriptorImpl : constraints) {
                        parameterConstraints.add(MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraintDescriptorImpl, location));
                    }
                }
                AnnotatedType parameterAnnotatedType = parameter.getAnnotatedType();
                Set<MetaConstraint<?>> typeArgumentsConstraints = findTypeAnnotationConstraintsForExecutableParameter(executable, i, parameterAnnotatedType);
                CascadingMetaDataBuilder cascadingMetaData = findCascadingMetaData(executable, parameters, i, parameterAnnotatedType);
                metaData.add(new ConstrainedParameter(ConfigurationSource.ANNOTATION, executable, ReflectionHelper.typeOf(executable, i), i, parameterConstraints, typeArgumentsConstraints, cascadingMetaData));
            }
            i++;
        }
        return metaData;
    }

    private List<ConstraintDescriptorImpl<?>> findConstraints(Member member, ElementType type) {
        Annotation[] declaredAnnotations;
        List<ConstraintDescriptorImpl<?>> metaData = CollectionHelper.newArrayList();
        for (Annotation annotation : ((AccessibleObject) member).getDeclaredAnnotations()) {
            metaData.addAll(findConstraintAnnotations(member, annotation, type));
        }
        return metaData;
    }

    private List<ConstraintDescriptorImpl<?>> findClassLevelConstraints(Class<?> beanClass) {
        Annotation[] declaredAnnotations;
        List<ConstraintDescriptorImpl<?>> metaData = CollectionHelper.newArrayList();
        for (Annotation annotation : beanClass.getDeclaredAnnotations()) {
            metaData.addAll(findConstraintAnnotations(null, annotation, ElementType.TYPE));
        }
        return metaData;
    }

    protected <A extends Annotation> List<ConstraintDescriptorImpl<?>> findConstraintAnnotations(Member member, A annotation, ElementType type) {
        if (this.constraintHelper.isJdkAnnotation(annotation.annotationType())) {
            return Collections.emptyList();
        }
        List<Annotation> constraints = CollectionHelper.newArrayList();
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (this.constraintHelper.isConstraintAnnotation(annotationType)) {
            constraints.add(annotation);
        } else if (this.constraintHelper.isMultiValueConstraint(annotationType)) {
            constraints.addAll(this.constraintHelper.getConstraintsFromMultiValueConstraint(annotation));
        }
        return (List) constraints.stream().map(c -> {
            return buildConstraintDescriptor(member, c, type);
        }).collect(Collectors.toList());
    }

    private Map<Class<?>, Class<?>> getGroupConversions(AnnotatedElement annotatedElement) {
        return getGroupConversions((ConvertGroup) annotatedElement.getAnnotation(ConvertGroup.class), (ConvertGroup.List) annotatedElement.getAnnotation(ConvertGroup.List.class));
    }

    private Map<Class<?>, Class<?>> getGroupConversions(ConvertGroup groupConversion, ConvertGroup.List groupConversionList) {
        ConvertGroup[] value;
        Map<Class<?>, Class<?>> groupConversions = CollectionHelper.newHashMap();
        if (groupConversion != null) {
            groupConversions.put(groupConversion.from(), groupConversion.to());
        }
        if (groupConversionList != null) {
            for (ConvertGroup conversion : groupConversionList.value()) {
                if (groupConversions.containsKey(conversion.from())) {
                    throw LOG.getMultipleGroupConversionsForSameSourceException(conversion.from(), CollectionHelper.asSet(groupConversions.get(conversion.from()), conversion.to()));
                }
                groupConversions.put(conversion.from(), conversion.to());
            }
        }
        return groupConversions;
    }

    private <A extends Annotation> ConstraintDescriptorImpl<A> buildConstraintDescriptor(Member member, A annotation, ElementType type) {
        return new ConstraintDescriptorImpl<>(this.constraintHelper, member, new ConstraintAnnotationDescriptor(annotation), type);
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraints(Field field) {
        return findTypeArgumentsConstraints(field, new TypeArgumentFieldLocation(field), field.getAnnotatedType());
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraints(Executable executable, AnnotatedType annotatedReturnType) {
        return findTypeArgumentsConstraints(executable, new TypeArgumentReturnValueLocation(executable), annotatedReturnType);
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Executable executable, Parameter[] parameters, int i, AnnotatedType parameterAnnotatedType) {
        Parameter parameter = parameters[i];
        TypeVariable<?>[] typeParameters = parameter.getType().getTypeParameters();
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = getTypeParametersCascadingMetadata(parameterAnnotatedType, typeParameters);
        try {
            return getCascadingMetaData(ReflectionHelper.typeOf(parameter.getDeclaringExecutable(), i), parameter, containerElementTypesCascadingMetaData);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
            return CascadingMetaDataBuilder.nonCascading();
        }
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Field field) {
        TypeVariable<?>[] typeParameters = field.getType().getTypeParameters();
        AnnotatedType annotatedType = field.getAnnotatedType();
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = getTypeParametersCascadingMetadata(annotatedType, typeParameters);
        return getCascadingMetaData(ReflectionHelper.typeOf(field), field, containerElementTypesCascadingMetaData);
    }

    private CascadingMetaDataBuilder findCascadingMetaData(Executable executable, AnnotatedType annotatedReturnType) {
        TypeVariable<?>[] typeParameters;
        if (executable instanceof Method) {
            typeParameters = ((Method) executable).getReturnType().getTypeParameters();
        } else {
            typeParameters = ((Constructor) executable).getDeclaringClass().getTypeParameters();
        }
        Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData = getTypeParametersCascadingMetadata(annotatedReturnType, typeParameters);
        return getCascadingMetaData(ReflectionHelper.typeOf(executable), executable, containerElementTypesCascadingMetaData);
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetadata(AnnotatedType annotatedType, TypeVariable<?>[] typeParameters) {
        if (annotatedType instanceof AnnotatedArrayType) {
            return getTypeParametersCascadingMetaDataForArrayType((AnnotatedArrayType) annotatedType);
        }
        if (annotatedType instanceof AnnotatedParameterizedType) {
            return getTypeParametersCascadingMetaDataForParameterizedType((AnnotatedParameterizedType) annotatedType, typeParameters);
        }
        return Collections.emptyMap();
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForParameterizedType(AnnotatedParameterizedType annotatedParameterizedType, TypeVariable<?>[] typeParameters) {
        Map<TypeVariable<?>, CascadingMetaDataBuilder> typeParametersCascadingMetadata = CollectionHelper.newHashMap(typeParameters.length);
        AnnotatedType[] annotatedTypeArguments = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        int i = 0;
        for (AnnotatedType annotatedTypeArgument : annotatedTypeArguments) {
            Map<TypeVariable<?>, CascadingMetaDataBuilder> nestedTypeParametersCascadingMetadata = getTypeParametersCascadingMetaDataForAnnotatedType(annotatedTypeArgument);
            typeParametersCascadingMetadata.put(typeParameters[i], new CascadingMetaDataBuilder(annotatedParameterizedType.getType(), typeParameters[i], annotatedTypeArgument.isAnnotationPresent(Valid.class), nestedTypeParametersCascadingMetadata, getGroupConversions(annotatedTypeArgument)));
            i++;
        }
        return typeParametersCascadingMetadata;
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForArrayType(AnnotatedArrayType annotatedArrayType) {
        return Collections.emptyMap();
    }

    private Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaDataForAnnotatedType(AnnotatedType annotatedType) {
        if (annotatedType instanceof AnnotatedArrayType) {
            return getTypeParametersCascadingMetaDataForArrayType((AnnotatedArrayType) annotatedType);
        }
        if (annotatedType instanceof AnnotatedParameterizedType) {
            return getTypeParametersCascadingMetaDataForParameterizedType((AnnotatedParameterizedType) annotatedType, ReflectionHelper.getClassFromType(annotatedType.getType()).getTypeParameters());
        }
        return Collections.emptyMap();
    }

    protected Set<MetaConstraint<?>> findTypeAnnotationConstraintsForExecutableParameter(Executable executable, int i, AnnotatedType parameterAnnotatedType) {
        try {
            return findTypeArgumentsConstraints(executable, new TypeArgumentExecutableParameterLocation(executable, i), parameterAnnotatedType);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.warn(Messages.MESSAGES.constraintOnConstructorOfNonStaticInnerClass(), ex);
            return Collections.emptySet();
        }
    }

    private Set<MetaConstraint<?>> findTypeArgumentsConstraints(Member member, TypeArgumentLocation location, AnnotatedType annotatedType) {
        TypeVariable<?>[] typeParameters;
        if (!(annotatedType instanceof AnnotatedParameterizedType)) {
            return Collections.emptySet();
        }
        Set<MetaConstraint<?>> typeArgumentConstraints = new HashSet<>();
        if (annotatedType instanceof AnnotatedArrayType) {
            AnnotatedArrayType annotatedArrayType = (AnnotatedArrayType) annotatedType;
            Type validatedType = annotatedArrayType.getAnnotatedGenericComponentType().getType();
            TypeVariable<?> arrayElementTypeArgument = new ArrayElement(annotatedArrayType);
            typeArgumentConstraints.addAll(findTypeUseConstraints(member, annotatedArrayType, arrayElementTypeArgument, location, validatedType));
            typeArgumentConstraints.addAll(findTypeArgumentsConstraints(member, new NestedTypeArgumentLocation(location, arrayElementTypeArgument, validatedType), annotatedArrayType.getAnnotatedGenericComponentType()));
        } else if (annotatedType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
            int i = 0;
            for (TypeVariable<?> typeVariable : ReflectionHelper.getClassFromType(annotatedType.getType()).getTypeParameters()) {
                AnnotatedType annotatedTypeParameter = annotatedParameterizedType.getAnnotatedActualTypeArguments()[i];
                Type validatedType2 = annotatedTypeParameter.getType();
                typeArgumentConstraints.addAll(findTypeUseConstraints(member, annotatedTypeParameter, typeVariable, location, validatedType2));
                if (validatedType2 instanceof ParameterizedType) {
                    typeArgumentConstraints.addAll(findTypeArgumentsConstraints(member, new NestedTypeArgumentLocation(location, typeVariable, validatedType2), annotatedTypeParameter));
                }
                i++;
            }
        }
        return typeArgumentConstraints.isEmpty() ? Collections.emptySet() : typeArgumentConstraints;
    }

    private Set<MetaConstraint<?>> findTypeUseConstraints(Member member, AnnotatedType typeArgument, TypeVariable<?> typeVariable, TypeArgumentLocation location, Type type) {
        Set<MetaConstraint<?>> constraints = (Set) Arrays.stream(typeArgument.getAnnotations()).flatMap(a -> {
            return findConstraintAnnotations(member, a, ElementType.TYPE_USE).stream();
        }).map(d -> {
            return createTypeArgumentMetaConstraint(d, location, typeVariable, type);
        }).collect(Collectors.toSet());
        return constraints;
    }

    private <A extends Annotation> MetaConstraint<?> createTypeArgumentMetaConstraint(ConstraintDescriptorImpl<A> descriptor, TypeArgumentLocation location, TypeVariable<?> typeVariable, Type type) {
        ConstraintLocation constraintLocation = ConstraintLocation.forTypeArgument(location.toConstraintLocation(), typeVariable, type);
        return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, descriptor, constraintLocation);
    }

    private CascadingMetaDataBuilder getCascadingMetaData(Type type, AnnotatedElement annotatedElement, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData) {
        return CascadingMetaDataBuilder.annotatedObject(type, annotatedElement.isAnnotationPresent(Valid.class), containerElementTypesCascadingMetaData, getGroupConversions(annotatedElement));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider$TypeArgumentExecutableParameterLocation.class */
    public static class TypeArgumentExecutableParameterLocation implements TypeArgumentLocation {
        private final Executable executable;
        private final int index;

        private TypeArgumentExecutableParameterLocation(Executable executable, int index) {
            this.executable = executable;
            this.index = index;
        }

        @Override // org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider.TypeArgumentLocation
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forParameter(this.executable, this.index);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider$TypeArgumentFieldLocation.class */
    public static class TypeArgumentFieldLocation implements TypeArgumentLocation {
        private final Field field;

        private TypeArgumentFieldLocation(Field field) {
            this.field = field;
        }

        @Override // org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider.TypeArgumentLocation
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forField(this.field);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider$TypeArgumentReturnValueLocation.class */
    public static class TypeArgumentReturnValueLocation implements TypeArgumentLocation {
        private final Executable executable;

        private TypeArgumentReturnValueLocation(Executable executable) {
            this.executable = executable;
        }

        @Override // org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider.TypeArgumentLocation
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forReturnValue(this.executable);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/provider/AnnotationMetaDataProvider$NestedTypeArgumentLocation.class */
    public static class NestedTypeArgumentLocation implements TypeArgumentLocation {
        private final TypeArgumentLocation parentLocation;
        private final TypeVariable<?> typeParameter;
        private final Type typeOfAnnotatedElement;

        private NestedTypeArgumentLocation(TypeArgumentLocation parentLocation, TypeVariable<?> typeParameter, Type typeOfAnnotatedElement) {
            this.parentLocation = parentLocation;
            this.typeParameter = typeParameter;
            this.typeOfAnnotatedElement = typeOfAnnotatedElement;
        }

        @Override // org.hibernate.validator.internal.metadata.provider.AnnotationMetaDataProvider.TypeArgumentLocation
        public ConstraintLocation toConstraintLocation() {
            return ConstraintLocation.forTypeArgument(this.parentLocation.toConstraintLocation(), this.typeParameter, this.typeOfAnnotatedElement);
        }
    }
}