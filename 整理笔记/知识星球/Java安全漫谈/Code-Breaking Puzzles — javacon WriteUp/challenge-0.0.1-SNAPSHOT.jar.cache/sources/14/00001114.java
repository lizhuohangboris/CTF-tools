package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.MethodType;
import javax.validation.metadata.PropertyDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/BeanDescriptorImpl.class */
public class BeanDescriptorImpl extends ElementDescriptorImpl implements BeanDescriptor {
    private final Map<String, PropertyDescriptor> constrainedProperties;
    private final Map<String, ExecutableDescriptorImpl> constrainedMethods;
    private final Map<String, ConstructorDescriptor> constrainedConstructors;

    public BeanDescriptorImpl(Type beanClass, Set<ConstraintDescriptorImpl<?>> classLevelConstraints, Map<String, PropertyDescriptor> constrainedProperties, Map<String, ExecutableDescriptorImpl> constrainedMethods, Map<String, ConstructorDescriptor> constrainedConstructors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        super(beanClass, classLevelConstraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.constrainedProperties = CollectionHelper.toImmutableMap(constrainedProperties);
        this.constrainedMethods = CollectionHelper.toImmutableMap(constrainedMethods);
        this.constrainedConstructors = CollectionHelper.toImmutableMap(constrainedConstructors);
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public final boolean isBeanConstrained() {
        return hasConstraints() || !this.constrainedProperties.isEmpty();
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public final PropertyDescriptor getConstraintsForProperty(String propertyName) {
        Contracts.assertNotNull(propertyName, "The property name cannot be null");
        return this.constrainedProperties.get(propertyName);
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public final Set<PropertyDescriptor> getConstrainedProperties() {
        return CollectionHelper.newHashSet((Collection) this.constrainedProperties.values());
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public ConstructorDescriptor getConstraintsForConstructor(Class<?>... parameterTypes) {
        return this.constrainedConstructors.get(ExecutableHelper.getSignature(getElementClass().getSimpleName(), parameterTypes));
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public Set<ConstructorDescriptor> getConstrainedConstructors() {
        return CollectionHelper.newHashSet((Collection) this.constrainedConstructors.values());
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public Set<MethodDescriptor> getConstrainedMethods(MethodType methodType, MethodType... methodTypes) {
        boolean includeGetters = MethodType.GETTER.equals(methodType);
        boolean includeNonGetters = MethodType.NON_GETTER.equals(methodType);
        if (methodTypes != null) {
            for (MethodType type : methodTypes) {
                if (MethodType.GETTER.equals(type)) {
                    includeGetters = true;
                }
                if (MethodType.NON_GETTER.equals(type)) {
                    includeNonGetters = true;
                }
            }
        }
        Set<MethodDescriptor> matchingMethodDescriptors = CollectionHelper.newHashSet();
        for (ExecutableDescriptorImpl constrainedMethod : this.constrainedMethods.values()) {
            boolean addToSet = false;
            if ((constrainedMethod.isGetter() && includeGetters) || (!constrainedMethod.isGetter() && includeNonGetters)) {
                addToSet = true;
            }
            if (addToSet) {
                matchingMethodDescriptors.add(constrainedMethod);
            }
        }
        return matchingMethodDescriptors;
    }

    @Override // javax.validation.metadata.BeanDescriptor
    public MethodDescriptor getConstraintsForMethod(String methodName, Class<?>... parameterTypes) {
        Contracts.assertNotNull(methodName, Messages.MESSAGES.methodNameMustNotBeNull());
        return this.constrainedMethods.get(ExecutableHelper.getSignature(methodName, parameterTypes));
    }

    public String toString() {
        return "BeanDescriptorImpl{class='" + getElementClass().getSimpleName() + "'}";
    }
}