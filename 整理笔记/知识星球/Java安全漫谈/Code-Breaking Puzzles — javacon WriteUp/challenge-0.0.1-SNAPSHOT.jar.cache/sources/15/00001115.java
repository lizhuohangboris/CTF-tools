package org.hibernate.validator.internal.metadata.descriptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Constraint;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import javax.validation.valueextraction.Unwrapping;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.annotation.ConstraintAnnotationDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttributes;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethods;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ConstraintDescriptorImpl.class */
public class ConstraintDescriptorImpl<T extends Annotation> implements ConstraintDescriptor<T>, Serializable {
    private static final long serialVersionUID = -2563102960314069246L;
    private static final int OVERRIDES_PARAMETER_DEFAULT_INDEX = -1;
    private final ConstraintAnnotationDescriptor<T> annotationDescriptor;
    private final List<Class<? extends ConstraintValidator<T, ?>>> constraintValidatorClasses;
    private final transient List<ConstraintValidatorDescriptor<T>> matchingConstraintValidatorDescriptors;
    private final Set<Class<?>> groups;
    private final Set<Class<? extends Payload>> payloads;
    private final Set<ConstraintDescriptorImpl<?>> composingConstraints;
    private final boolean isReportAsSingleInvalidConstraint;
    private final ElementType elementType;
    private final ConstraintOrigin definedOn;
    private final ConstraintType constraintType;
    private final ValidateUnwrappedValue valueUnwrapping;
    private final ConstraintTarget validationAppliesTo;
    private final CompositionType compositionType;
    private final int hashCode;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final List<String> NON_COMPOSING_CONSTRAINT_ANNOTATIONS = Arrays.asList(Documented.class.getName(), Retention.class.getName(), Target.class.getName(), Constraint.class.getName(), ReportAsSingleViolation.class.getName(), Repeatable.class.getName(), Deprecated.class.getName());

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ConstraintDescriptorImpl$ComposingConstraintAnnotationLocation.class */
    public enum ComposingConstraintAnnotationLocation {
        DIRECT,
        IN_CONTAINER
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ConstraintDescriptorImpl$ConstraintType.class */
    public enum ConstraintType {
        GENERIC,
        CROSS_PARAMETER
    }

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type, Class<?> implicitGroup, ConstraintOrigin definedOn, ConstraintType externalConstraintType) {
        this.annotationDescriptor = annotationDescriptor;
        this.elementType = type;
        this.definedOn = definedOn;
        this.isReportAsSingleInvalidConstraint = annotationDescriptor.getType().isAnnotationPresent(ReportAsSingleViolation.class);
        this.groups = buildGroupSet(annotationDescriptor, implicitGroup);
        this.payloads = buildPayloadSet(annotationDescriptor);
        this.valueUnwrapping = determineValueUnwrapping(this.payloads, member, annotationDescriptor.getType());
        this.validationAppliesTo = determineValidationAppliesTo(annotationDescriptor);
        this.constraintValidatorClasses = (List) constraintHelper.getAllValidatorDescriptors(annotationDescriptor.getType()).stream().map((v0) -> {
            return v0.getValidatorClass();
        }).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionHelper::toImmutableList));
        List<ConstraintValidatorDescriptor<T>> crossParameterValidatorDescriptors = CollectionHelper.toImmutableList(constraintHelper.findValidatorDescriptors(annotationDescriptor.getType(), ValidationTarget.PARAMETERS));
        List<ConstraintValidatorDescriptor<T>> genericValidatorDescriptors = CollectionHelper.toImmutableList(constraintHelper.findValidatorDescriptors(annotationDescriptor.getType(), ValidationTarget.ANNOTATED_ELEMENT));
        if (crossParameterValidatorDescriptors.size() > 1) {
            throw LOG.getMultipleCrossParameterValidatorClassesException(annotationDescriptor.getType());
        }
        this.constraintType = determineConstraintType(annotationDescriptor.getType(), member, type, !genericValidatorDescriptors.isEmpty(), !crossParameterValidatorDescriptors.isEmpty(), externalConstraintType);
        this.composingConstraints = parseComposingConstraints(constraintHelper, member, this.constraintType);
        this.compositionType = parseCompositionType(constraintHelper);
        validateComposingConstraintTypes();
        if (this.constraintType == ConstraintType.GENERIC) {
            this.matchingConstraintValidatorDescriptors = CollectionHelper.toImmutableList(genericValidatorDescriptors);
        } else {
            this.matchingConstraintValidatorDescriptors = CollectionHelper.toImmutableList(crossParameterValidatorDescriptors);
        }
        this.hashCode = annotationDescriptor.hashCode();
    }

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type) {
        this(constraintHelper, member, annotationDescriptor, type, null, ConstraintOrigin.DEFINED_LOCALLY, null);
    }

    public ConstraintDescriptorImpl(ConstraintHelper constraintHelper, Member member, ConstraintAnnotationDescriptor<T> annotationDescriptor, ElementType type, ConstraintType constraintType) {
        this(constraintHelper, member, annotationDescriptor, type, null, ConstraintOrigin.DEFINED_LOCALLY, constraintType);
    }

    public ConstraintAnnotationDescriptor<T> getAnnotationDescriptor() {
        return this.annotationDescriptor;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public T getAnnotation() {
        return this.annotationDescriptor.getAnnotation();
    }

    public Class<T> getAnnotationType() {
        return this.annotationDescriptor.getType();
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public String getMessageTemplate() {
        return this.annotationDescriptor.getMessage();
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public Set<Class<?>> getGroups() {
        return this.groups;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public Set<Class<? extends Payload>> getPayload() {
        return this.payloads;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public ConstraintTarget getValidationAppliesTo() {
        return this.validationAppliesTo;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public ValidateUnwrappedValue getValueUnwrapping() {
        return this.valueUnwrapping;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses() {
        return this.constraintValidatorClasses;
    }

    public List<ConstraintValidatorDescriptor<T>> getMatchingConstraintValidatorDescriptors() {
        return this.matchingConstraintValidatorDescriptors;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public Map<String, Object> getAttributes() {
        return this.annotationDescriptor.getAttributes();
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public Set<ConstraintDescriptor<?>> getComposingConstraints() {
        return this.composingConstraints;
    }

    public Set<ConstraintDescriptorImpl<?>> getComposingConstraintImpls() {
        return this.composingConstraints;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public boolean isReportAsSingleViolation() {
        return this.isReportAsSingleInvalidConstraint;
    }

    public ElementType getElementType() {
        return this.elementType;
    }

    public ConstraintOrigin getDefinedOn() {
        return this.definedOn;
    }

    public ConstraintType getConstraintType() {
        return this.constraintType;
    }

    @Override // javax.validation.metadata.ConstraintDescriptor
    public <U> U unwrap(Class<U> type) {
        throw LOG.getUnwrappingOfConstraintDescriptorNotSupportedYetException();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConstraintDescriptorImpl<?> that = (ConstraintDescriptorImpl) o;
        if (this.annotationDescriptor != null) {
            if (!this.annotationDescriptor.equals(that.annotationDescriptor)) {
                return false;
            }
            return true;
        } else if (that.annotationDescriptor != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintDescriptorImpl");
        sb.append("{annotation=").append(StringHelper.toShortString(this.annotationDescriptor.getType()));
        sb.append(", payloads=").append(this.payloads);
        sb.append(", hasComposingConstraints=").append(this.composingConstraints.isEmpty());
        sb.append(", isReportAsSingleInvalidConstraint=").append(this.isReportAsSingleInvalidConstraint);
        sb.append(", elementType=").append(this.elementType);
        sb.append(", definedOn=").append(this.definedOn);
        sb.append(", groups=").append(this.groups);
        sb.append(", attributes=").append(this.annotationDescriptor.getAttributes());
        sb.append(", constraintType=").append(this.constraintType);
        sb.append(", valueUnwrapping=").append(this.valueUnwrapping);
        sb.append('}');
        return sb.toString();
    }

    private ConstraintType determineConstraintType(Class<? extends Annotation> constraintAnnotationType, Member member, ElementType elementType, boolean hasGenericValidators, boolean hasCrossParameterValidator, ConstraintType externalConstraintType) {
        ConstraintTarget constraintTarget = this.validationAppliesTo;
        ConstraintType constraintType = null;
        boolean isExecutable = isExecutable(elementType);
        if (constraintTarget == ConstraintTarget.RETURN_VALUE) {
            if (!isExecutable) {
                throw LOG.getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(this.annotationDescriptor.getType(), ConstraintTarget.RETURN_VALUE);
            }
            constraintType = ConstraintType.GENERIC;
        } else if (constraintTarget == ConstraintTarget.PARAMETERS) {
            if (!isExecutable) {
                throw LOG.getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(this.annotationDescriptor.getType(), ConstraintTarget.PARAMETERS);
            }
            constraintType = ConstraintType.CROSS_PARAMETER;
        } else if (externalConstraintType != null) {
            constraintType = externalConstraintType;
        } else if (hasGenericValidators && !hasCrossParameterValidator) {
            constraintType = ConstraintType.GENERIC;
        } else if (!hasGenericValidators && hasCrossParameterValidator) {
            constraintType = ConstraintType.CROSS_PARAMETER;
        } else if (!isExecutable) {
            constraintType = ConstraintType.GENERIC;
        } else if (constraintAnnotationType.isAnnotationPresent(SupportedValidationTarget.class)) {
            SupportedValidationTarget supportedValidationTarget = (SupportedValidationTarget) constraintAnnotationType.getAnnotation(SupportedValidationTarget.class);
            if (supportedValidationTarget.value().length == 1) {
                constraintType = supportedValidationTarget.value()[0] == ValidationTarget.ANNOTATED_ELEMENT ? ConstraintType.GENERIC : ConstraintType.CROSS_PARAMETER;
            }
        } else {
            boolean hasParameters = hasParameters(member);
            boolean hasReturnValue = hasReturnValue(member);
            if (!hasParameters && hasReturnValue) {
                constraintType = ConstraintType.GENERIC;
            } else if (hasParameters && !hasReturnValue) {
                constraintType = ConstraintType.CROSS_PARAMETER;
            }
        }
        if (constraintType == null) {
            throw LOG.getImplicitConstraintTargetInAmbiguousConfigurationException(this.annotationDescriptor.getType());
        }
        if (constraintType == ConstraintType.CROSS_PARAMETER) {
            validateCrossParameterConstraintType(member, hasCrossParameterValidator);
        }
        return constraintType;
    }

    private static ValidateUnwrappedValue determineValueUnwrapping(Set<Class<? extends Payload>> payloads, Member member, Class<? extends Annotation> annotationType) {
        if (payloads.contains(Unwrapping.Unwrap.class)) {
            if (payloads.contains(Unwrapping.Skip.class)) {
                throw LOG.getInvalidUnwrappingConfigurationForConstraintException(member, annotationType);
            }
            return ValidateUnwrappedValue.UNWRAP;
        } else if (payloads.contains(Unwrapping.Skip.class)) {
            return ValidateUnwrappedValue.SKIP;
        } else {
            return ValidateUnwrappedValue.DEFAULT;
        }
    }

    private static ConstraintTarget determineValidationAppliesTo(ConstraintAnnotationDescriptor<?> annotationDescriptor) {
        return annotationDescriptor.getValidationAppliesTo();
    }

    private void validateCrossParameterConstraintType(Member member, boolean hasCrossParameterValidator) {
        if (!hasCrossParameterValidator) {
            throw LOG.getCrossParameterConstraintHasNoValidatorException(this.annotationDescriptor.getType());
        }
        if (member == null) {
            throw LOG.getCrossParameterConstraintOnClassException(this.annotationDescriptor.getType());
        }
        if (member instanceof Field) {
            throw LOG.getCrossParameterConstraintOnFieldException(this.annotationDescriptor.getType(), member);
        }
        if (!hasParameters(member)) {
            throw LOG.getCrossParameterConstraintOnMethodWithoutParametersException(this.annotationDescriptor.getType(), (Executable) member);
        }
    }

    private void validateComposingConstraintTypes() {
        for (ConstraintDescriptorImpl<?> composingConstraint : getComposingConstraintImpls()) {
            if (composingConstraint.constraintType != this.constraintType) {
                throw LOG.getComposedAndComposingConstraintsHaveDifferentTypesException(this.annotationDescriptor.getType(), composingConstraint.annotationDescriptor.getType(), this.constraintType, composingConstraint.constraintType);
            }
        }
    }

    private boolean hasParameters(Member member) {
        boolean hasParameters = false;
        if (member instanceof Constructor) {
            Constructor<?> constructor = (Constructor) member;
            hasParameters = constructor.getParameterTypes().length > 0;
        } else if (member instanceof Method) {
            Method method = (Method) member;
            hasParameters = method.getParameterTypes().length > 0;
        }
        return hasParameters;
    }

    private boolean hasReturnValue(Member member) {
        boolean hasReturnValue;
        if (member instanceof Constructor) {
            hasReturnValue = true;
        } else if (member instanceof Method) {
            Method method = (Method) member;
            hasReturnValue = method.getGenericReturnType() != Void.TYPE;
        } else {
            hasReturnValue = false;
        }
        return hasReturnValue;
    }

    private boolean isExecutable(ElementType elementType) {
        return elementType == ElementType.METHOD || elementType == ElementType.CONSTRUCTOR;
    }

    private static Set<Class<? extends Payload>> buildPayloadSet(ConstraintAnnotationDescriptor<?> annotationDescriptor) {
        Set<Class<? extends Payload>> payloadSet = CollectionHelper.newHashSet();
        Class<? extends Payload>[] payloadFromAnnotation = annotationDescriptor.getPayload();
        if (payloadFromAnnotation != null) {
            payloadSet.addAll(Arrays.asList(payloadFromAnnotation));
        }
        return CollectionHelper.toImmutableSet(payloadSet);
    }

    private static Set<Class<?>> buildGroupSet(ConstraintAnnotationDescriptor<?> annotationDescriptor, Class<?> implicitGroup) {
        Set<Class<?>> groupSet = CollectionHelper.newHashSet();
        Class<?>[] groupsFromAnnotation = annotationDescriptor.getGroups();
        if (groupsFromAnnotation.length == 0) {
            groupSet.add(Default.class);
        } else {
            groupSet.addAll(Arrays.asList(groupsFromAnnotation));
        }
        if (implicitGroup != null && groupSet.contains(Default.class)) {
            groupSet.add(implicitGroup);
        }
        return CollectionHelper.toImmutableSet(groupSet);
    }

    private Map<ClassIndexWrapper, Map<String, Object>> parseOverrideParameters() {
        Map<ClassIndexWrapper, Map<String, Object>> overrideParameters = CollectionHelper.newHashMap();
        Method[] methods = (Method[]) run(GetDeclaredMethods.action(this.annotationDescriptor.getType()));
        for (Method m : methods) {
            if (m.getAnnotation(OverridesAttribute.class) != null) {
                addOverrideAttributes(overrideParameters, m, (OverridesAttribute) m.getAnnotation(OverridesAttribute.class));
            } else if (m.getAnnotation(OverridesAttribute.List.class) != null) {
                addOverrideAttributes(overrideParameters, m, ((OverridesAttribute.List) m.getAnnotation(OverridesAttribute.List.class)).value());
            }
        }
        return overrideParameters;
    }

    private void addOverrideAttributes(Map<ClassIndexWrapper, Map<String, Object>> overrideParameters, Method m, OverridesAttribute... attributes) {
        Object value = this.annotationDescriptor.getAttribute(m.getName());
        for (OverridesAttribute overridesAttribute : attributes) {
            String overridesAttributeName = overridesAttribute.name().length() > 0 ? overridesAttribute.name() : m.getName();
            ensureAttributeIsOverridable(m, overridesAttribute, overridesAttributeName);
            ClassIndexWrapper wrapper = new ClassIndexWrapper(overridesAttribute.constraint(), overridesAttribute.constraintIndex());
            Map<String, Object> map = overrideParameters.get(wrapper);
            if (map == null) {
                map = CollectionHelper.newHashMap();
                overrideParameters.put(wrapper, map);
            }
            map.put(overridesAttributeName, value);
        }
    }

    private void ensureAttributeIsOverridable(Method m, OverridesAttribute overridesAttribute, String overridesAttributeName) {
        Method method = (Method) run(GetMethod.action(overridesAttribute.constraint(), overridesAttributeName));
        if (method == null) {
            throw LOG.getOverriddenConstraintAttributeNotFoundException(overridesAttributeName);
        }
        Class<?> returnTypeOfOverriddenConstraint = method.getReturnType();
        if (!returnTypeOfOverriddenConstraint.equals(m.getReturnType())) {
            throw LOG.getWrongAttributeTypeForOverriddenConstraintException(returnTypeOfOverriddenConstraint, m.getReturnType());
        }
    }

    private Set<ConstraintDescriptorImpl<?>> parseComposingConstraints(ConstraintHelper constraintHelper, Member member, ConstraintType constraintType) {
        Annotation[] declaredAnnotations;
        HashSet newHashSet = CollectionHelper.newHashSet();
        Map<ClassIndexWrapper, Map<String, Object>> overrideParameters = parseOverrideParameters();
        Map<Class<? extends Annotation>, ComposingConstraintAnnotationLocation> composingConstraintLocations = new HashMap<>();
        for (Annotation declaredAnnotation : this.annotationDescriptor.getType().getDeclaredAnnotations()) {
            Class<? extends Annotation> declaredAnnotationType = declaredAnnotation.annotationType();
            if (!NON_COMPOSING_CONSTRAINT_ANNOTATIONS.contains(declaredAnnotationType.getName())) {
                if (constraintHelper.isConstraintAnnotation(declaredAnnotationType)) {
                    if (composingConstraintLocations.containsKey(declaredAnnotationType) && !ComposingConstraintAnnotationLocation.DIRECT.equals(composingConstraintLocations.get(declaredAnnotationType))) {
                        throw LOG.getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(this.annotationDescriptor.getType(), declaredAnnotationType);
                    }
                    Object createComposingConstraintDescriptor = createComposingConstraintDescriptor(constraintHelper, member, overrideParameters, -1, declaredAnnotation, constraintType);
                    newHashSet.add(createComposingConstraintDescriptor);
                    composingConstraintLocations.put(declaredAnnotationType, ComposingConstraintAnnotationLocation.DIRECT);
                    LOG.debugf("Adding composing constraint: %s.", createComposingConstraintDescriptor);
                } else if (constraintHelper.isMultiValueConstraint(declaredAnnotationType)) {
                    List<Annotation> multiValueConstraints = constraintHelper.getConstraintsFromMultiValueConstraint(declaredAnnotation);
                    int index = 0;
                    for (Annotation constraintAnnotation : multiValueConstraints) {
                        if (composingConstraintLocations.containsKey(constraintAnnotation.annotationType()) && !ComposingConstraintAnnotationLocation.IN_CONTAINER.equals(composingConstraintLocations.get(constraintAnnotation.annotationType()))) {
                            throw LOG.getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(this.annotationDescriptor.getType(), constraintAnnotation.annotationType());
                        }
                        Object createComposingConstraintDescriptor2 = createComposingConstraintDescriptor(constraintHelper, member, overrideParameters, index, constraintAnnotation, constraintType);
                        newHashSet.add(createComposingConstraintDescriptor2);
                        composingConstraintLocations.put(constraintAnnotation.annotationType(), ComposingConstraintAnnotationLocation.IN_CONTAINER);
                        LOG.debugf("Adding composing constraint: %s.", createComposingConstraintDescriptor2);
                        index++;
                    }
                    continue;
                } else {
                    continue;
                }
            }
        }
        return CollectionHelper.toImmutableSet(newHashSet);
    }

    private CompositionType parseCompositionType(ConstraintHelper constraintHelper) {
        Annotation[] declaredAnnotations;
        for (Annotation declaredAnnotation : this.annotationDescriptor.getType().getDeclaredAnnotations()) {
            Class<? extends Annotation> declaredAnnotationType = declaredAnnotation.annotationType();
            if (!NON_COMPOSING_CONSTRAINT_ANNOTATIONS.contains(declaredAnnotationType.getName()) && constraintHelper.isConstraintComposition(declaredAnnotationType)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debugf("Adding Bool %s.", declaredAnnotationType.getName());
                }
                return ((ConstraintComposition) declaredAnnotation).value();
            }
        }
        return CompositionType.AND;
    }

    private <U extends Annotation> ConstraintDescriptorImpl<U> createComposingConstraintDescriptor(ConstraintHelper constraintHelper, Member member, Map<ClassIndexWrapper, Map<String, Object>> overrideParameters, int index, U constraintAnnotation, ConstraintType constraintType) {
        Class<? extends Annotation> annotationType = constraintAnnotation.annotationType();
        ConstraintAnnotationDescriptor.Builder<U> annotationDescriptorBuilder = new ConstraintAnnotationDescriptor.Builder<>(annotationType, (Map) run(GetAnnotationAttributes.action(constraintAnnotation)));
        Map<String, Object> overrides = overrideParameters.get(new ClassIndexWrapper(annotationType, index));
        if (overrides != null) {
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                annotationDescriptorBuilder.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        annotationDescriptorBuilder.setGroups((Class[]) this.groups.toArray(new Class[this.groups.size()]));
        annotationDescriptorBuilder.setPayload((Class[]) this.payloads.toArray(new Class[this.payloads.size()]));
        if (annotationDescriptorBuilder.hasAttribute(ConstraintHelper.VALIDATION_APPLIES_TO)) {
            ConstraintTarget validationAppliesTo = getValidationAppliesTo();
            if (validationAppliesTo == null) {
                if (constraintType == ConstraintType.CROSS_PARAMETER) {
                    validationAppliesTo = ConstraintTarget.PARAMETERS;
                } else {
                    validationAppliesTo = ConstraintTarget.IMPLICIT;
                }
            }
            annotationDescriptorBuilder.setAttribute(ConstraintHelper.VALIDATION_APPLIES_TO, validationAppliesTo);
        }
        return new ConstraintDescriptorImpl<>(constraintHelper, member, annotationDescriptorBuilder.build(), this.elementType, null, this.definedOn, constraintType);
    }

    private static <P> P run(PrivilegedAction<P> action) {
        return System.getSecurityManager() != null ? (P) AccessController.doPrivileged(action) : action.run();
    }

    public CompositionType getCompositionType() {
        return this.compositionType;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ConstraintDescriptorImpl$ClassIndexWrapper.class */
    public static class ClassIndexWrapper {
        final Class<?> clazz;
        final int index;

        ClassIndexWrapper(Class<?> clazz, int index) {
            this.clazz = clazz;
            this.index = index;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ClassIndexWrapper that = (ClassIndexWrapper) o;
            if (this.index != that.index) {
                return false;
            }
            return this.clazz.equals(that.clazz);
        }

        public int hashCode() {
            int result = this.clazz != null ? this.clazz.hashCode() : 0;
            return (31 * result) + this.index;
        }

        public String toString() {
            return "ClassIndexWrapper [clazz=" + StringHelper.toShortString((Type) this.clazz) + ", index=" + this.index + "]";
        }
    }
}