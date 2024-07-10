package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.descriptor.ContainerElementTypeDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.TypeVariables;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/AbstractConstraintMetaData.class */
public abstract class AbstractConstraintMetaData implements ConstraintMetaData {
    private final String name;
    private final Type type;
    private final Set<MetaConstraint<?>> directConstraints;
    private final Set<MetaConstraint<?>> containerElementsConstraints;
    private final Set<MetaConstraint<?>> allConstraints;
    private final boolean isCascading;
    private final boolean isConstrained;

    public AbstractConstraintMetaData(String name, Type type, Set<MetaConstraint<?>> directConstraints, Set<MetaConstraint<?>> containerElementsConstraints, boolean isCascading, boolean isConstrained) {
        this.name = name;
        this.type = type;
        this.directConstraints = CollectionHelper.toImmutableSet(directConstraints);
        this.containerElementsConstraints = CollectionHelper.toImmutableSet(containerElementsConstraints);
        this.allConstraints = (Set) Stream.concat(directConstraints.stream(), containerElementsConstraints.stream()).collect(Collectors.collectingAndThen(Collectors.toSet(), CollectionHelper::toImmutableSet));
        this.isCascading = isCascading;
        this.isConstrained = isConstrained;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public String getName() {
        return this.name;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public Type getType() {
        return this.type;
    }

    @Override // java.lang.Iterable
    public Iterator<MetaConstraint<?>> iterator() {
        return this.allConstraints.iterator();
    }

    public Set<MetaConstraint<?>> getAllConstraints() {
        return this.allConstraints;
    }

    public Set<MetaConstraint<?>> getDirectConstraints() {
        return this.directConstraints;
    }

    public Set<MetaConstraint<?>> getContainerElementsConstraints() {
        return this.containerElementsConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public final boolean isCascading() {
        return this.isCascading;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public boolean isConstrained() {
        return this.isConstrained;
    }

    public String toString() {
        return "AbstractConstraintMetaData [name=" + this.name + ", type=" + this.type + ", directConstraints=" + this.directConstraints + ", containerElementsConstraints=" + this.containerElementsConstraints + ", isCascading=" + this.isCascading + ", isConstrained=" + this.isConstrained + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractConstraintMetaData other = (AbstractConstraintMetaData) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
            return true;
        } else if (!this.name.equals(other.name)) {
            return false;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<ConstraintDescriptorImpl<?>> asDescriptors(Set<MetaConstraint<?>> constraints) {
        Set<ConstraintDescriptorImpl<?>> theValue = CollectionHelper.newHashSet();
        for (MetaConstraint<?> oneConstraint : constraints) {
            theValue.add(oneConstraint.getDescriptor());
        }
        return theValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<ContainerElementTypeDescriptor> asContainerElementTypeDescriptors(Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return asContainerElementTypeDescriptors(this.type, ContainerElementMetaDataTree.of(cascadingMetaData, containerElementsConstraints), defaultGroupSequenceRedefined, defaultGroupSequence);
    }

    private Set<ContainerElementTypeDescriptor> asContainerElementTypeDescriptors(Type type, ContainerElementMetaDataTree containerElementMetaDataTree, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        Set<ContainerElementTypeDescriptor> containerElementTypeDescriptors = new HashSet<>();
        for (Map.Entry<TypeVariable<?>, ContainerElementMetaDataTree> entry : containerElementMetaDataTree.nodes.entrySet()) {
            TypeVariable<?> childTypeParameter = entry.getKey();
            ContainerElementMetaDataTree childContainerElementMetaDataTree = entry.getValue();
            Set<ContainerElementTypeDescriptor> childrenDescriptors = asContainerElementTypeDescriptors(childContainerElementMetaDataTree.elementType, childContainerElementMetaDataTree, defaultGroupSequenceRedefined, defaultGroupSequence);
            containerElementTypeDescriptors.add(new ContainerElementTypeDescriptorImpl(childContainerElementMetaDataTree.elementType, childContainerElementMetaDataTree.containerClass, TypeVariables.getTypeParameterIndex(childTypeParameter), asDescriptors(childContainerElementMetaDataTree.constraints), childrenDescriptors, childContainerElementMetaDataTree.cascading, defaultGroupSequenceRedefined, defaultGroupSequence, childContainerElementMetaDataTree.groupConversionDescriptors));
        }
        return containerElementTypeDescriptors;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/AbstractConstraintMetaData$ContainerElementMetaDataTree.class */
    public static class ContainerElementMetaDataTree {
        private Class<?> containerClass;
        private final Map<TypeVariable<?>, ContainerElementMetaDataTree> nodes = new HashMap();
        private Type elementType = null;
        private final Set<MetaConstraint<?>> constraints = new HashSet();
        private boolean cascading = false;
        private Set<GroupConversionDescriptor> groupConversionDescriptors = new HashSet();

        private ContainerElementMetaDataTree() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ContainerElementMetaDataTree of(CascadingMetaData cascadingMetaData, Set<MetaConstraint<?>> containerElementsConstraints) {
            ContainerElementMetaDataTree containerElementMetaConstraintTree = new ContainerElementMetaDataTree();
            for (MetaConstraint<?> constraint : containerElementsConstraints) {
                ConstraintLocation currentLocation = constraint.getLocation();
                List<TypeVariable<?>> constraintPath = new ArrayList<>();
                while (currentLocation instanceof TypeArgumentConstraintLocation) {
                    TypeArgumentConstraintLocation typeArgumentConstraintLocation = (TypeArgumentConstraintLocation) currentLocation;
                    constraintPath.add(typeArgumentConstraintLocation.getTypeParameter());
                    currentLocation = typeArgumentConstraintLocation.getDelegate();
                }
                Collections.reverse(constraintPath);
                containerElementMetaConstraintTree.addConstraint(constraintPath, constraint);
            }
            if (cascadingMetaData != null && cascadingMetaData.isContainer() && cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
                containerElementMetaConstraintTree.addCascadingMetaData(new ArrayList(), (ContainerCascadingMetaData) cascadingMetaData.as(ContainerCascadingMetaData.class));
            }
            return containerElementMetaConstraintTree;
        }

        private void addConstraint(List<TypeVariable<?>> path, MetaConstraint<?> constraint) {
            ContainerElementMetaDataTree tree = this;
            for (TypeVariable<?> typeArgument : path) {
                tree = tree.nodes.computeIfAbsent(typeArgument, ta -> {
                    return new ContainerElementMetaDataTree();
                });
            }
            TypeArgumentConstraintLocation constraintLocation = (TypeArgumentConstraintLocation) constraint.getLocation();
            tree.elementType = constraintLocation.getTypeForValidatorResolution();
            tree.containerClass = ((TypeArgumentConstraintLocation) constraint.getLocation()).getContainerClass();
            tree.constraints.add(constraint);
        }

        private void addCascadingMetaData(List<TypeVariable<?>> path, ContainerCascadingMetaData cascadingMetaData) {
            for (ContainerCascadingMetaData nestedCascadingMetaData : cascadingMetaData.getContainerElementTypesCascadingMetaData()) {
                List<TypeVariable<?>> nestedPath = new ArrayList<>(path);
                nestedPath.add(nestedCascadingMetaData.getTypeParameter());
                ContainerElementMetaDataTree tree = this;
                for (TypeVariable<?> typeArgument : nestedPath) {
                    tree = tree.nodes.computeIfAbsent(typeArgument, ta -> {
                        return new ContainerElementMetaDataTree();
                    });
                }
                tree.elementType = TypeVariables.getContainerElementType(nestedCascadingMetaData.getEnclosingType(), nestedCascadingMetaData.getTypeParameter());
                tree.containerClass = nestedCascadingMetaData.getDeclaredContainerClass();
                tree.cascading = nestedCascadingMetaData.isCascading();
                tree.groupConversionDescriptors = nestedCascadingMetaData.getGroupConversionDescriptors();
                if (nestedCascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements()) {
                    addCascadingMetaData(nestedPath, nestedCascadingMetaData);
                }
            }
        }
    }
}