package org.hibernate.validator.internal.metadata.descriptor;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.Scope;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;
import org.hibernate.validator.internal.metadata.core.ConstraintOrigin;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ElementDescriptorImpl.class */
public abstract class ElementDescriptorImpl implements ElementDescriptor, Serializable {
    private final Class<?> type;
    private final Set<ConstraintDescriptorImpl<?>> constraintDescriptors;
    private final boolean defaultGroupSequenceRedefined;
    private final List<Class<?>> defaultGroupSequence;

    public ElementDescriptorImpl(Type type, Set<ConstraintDescriptorImpl<?>> constraintDescriptors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        this.type = (Class) TypeHelper.getErasedType(type);
        this.constraintDescriptors = CollectionHelper.toImmutableSet(constraintDescriptors);
        this.defaultGroupSequenceRedefined = defaultGroupSequenceRedefined;
        this.defaultGroupSequence = CollectionHelper.toImmutableList(defaultGroupSequence);
    }

    @Override // javax.validation.metadata.ElementDescriptor
    public final boolean hasConstraints() {
        return this.constraintDescriptors.size() != 0;
    }

    @Override // javax.validation.metadata.ElementDescriptor
    public final Class<?> getElementClass() {
        return this.type;
    }

    @Override // javax.validation.metadata.ElementDescriptor
    public final Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return findConstraints().getConstraintDescriptors();
    }

    @Override // javax.validation.metadata.ElementDescriptor
    public final ElementDescriptor.ConstraintFinder findConstraints() {
        return new ConstraintFinderImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ElementDescriptorImpl$ConstraintFinderImpl.class */
    public class ConstraintFinderImpl implements ElementDescriptor.ConstraintFinder {
        private final EnumSet<ElementType> elementTypes = EnumSet.of(ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER);
        private final EnumSet<ConstraintOrigin> definedInSet = EnumSet.allOf(ConstraintOrigin.class);
        private List<Class<?>> groups = Collections.emptyList();

        ConstraintFinderImpl() {
        }

        @Override // javax.validation.metadata.ElementDescriptor.ConstraintFinder
        public ElementDescriptor.ConstraintFinder unorderedAndMatchingGroups(Class<?>... classes) {
            this.groups = CollectionHelper.newArrayList();
            for (Class<?> clazz : classes) {
                if (Default.class.equals(clazz) && ElementDescriptorImpl.this.defaultGroupSequenceRedefined) {
                    this.groups.addAll(ElementDescriptorImpl.this.defaultGroupSequence);
                } else {
                    this.groups.add(clazz);
                }
            }
            return this;
        }

        @Override // javax.validation.metadata.ElementDescriptor.ConstraintFinder
        public ElementDescriptor.ConstraintFinder lookingAt(Scope visibility) {
            if (visibility.equals(Scope.LOCAL_ELEMENT)) {
                this.definedInSet.remove(ConstraintOrigin.DEFINED_IN_HIERARCHY);
            }
            return this;
        }

        @Override // javax.validation.metadata.ElementDescriptor.ConstraintFinder
        public ElementDescriptor.ConstraintFinder declaredOn(ElementType... elementTypes) {
            this.elementTypes.clear();
            this.elementTypes.addAll(Arrays.asList(elementTypes));
            return this;
        }

        @Override // javax.validation.metadata.ElementDescriptor.ConstraintFinder
        public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
            Set<ConstraintDescriptor<?>> matchingDescriptors = new HashSet<>();
            findMatchingDescriptors(matchingDescriptors);
            return CollectionHelper.toImmutableSet(matchingDescriptors);
        }

        @Override // javax.validation.metadata.ElementDescriptor.ConstraintFinder
        public boolean hasConstraints() {
            return getConstraintDescriptors().size() != 0;
        }

        private void addMatchingDescriptorsForGroup(Class<?> group, Set<ConstraintDescriptor<?>> matchingDescriptors) {
            for (ConstraintDescriptorImpl<?> descriptor : ElementDescriptorImpl.this.constraintDescriptors) {
                if (this.definedInSet.contains(descriptor.getDefinedOn()) && this.elementTypes.contains(descriptor.getElementType()) && descriptor.getGroups().contains(group)) {
                    matchingDescriptors.add(descriptor);
                }
            }
        }

        private void findMatchingDescriptors(Set<ConstraintDescriptor<?>> matchingDescriptors) {
            if (this.groups.isEmpty()) {
                for (ConstraintDescriptorImpl<?> descriptor : ElementDescriptorImpl.this.constraintDescriptors) {
                    if (this.definedInSet.contains(descriptor.getDefinedOn()) && this.elementTypes.contains(descriptor.getElementType())) {
                        matchingDescriptors.add(descriptor);
                    }
                }
                return;
            }
            ValidationOrder validationOrder = new ValidationOrderGenerator().getValidationOrder(this.groups);
            Iterator<Group> groupIterator = validationOrder.getGroupIterator();
            while (groupIterator.hasNext()) {
                Group g = groupIterator.next();
                addMatchingDescriptorsForGroup(g.getDefiningClass(), matchingDescriptors);
            }
        }
    }
}