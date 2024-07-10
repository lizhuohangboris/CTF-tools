package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ContainerElementTypeDescriptorImpl.class */
public class ContainerElementTypeDescriptorImpl extends ElementDescriptorImpl implements ContainerElementTypeDescriptor {
    private final Class<?> containerClass;
    private final Integer typeArgumentIndex;
    private final Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes;
    private final boolean cascaded;
    private final Set<GroupConversionDescriptor> groupConversions;

    public ContainerElementTypeDescriptorImpl(Type type, Class<?> containerClass, Integer typeArgumentIndex, Set<ConstraintDescriptorImpl<?>> constraints, Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes, boolean cascaded, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence, Set<GroupConversionDescriptor> groupConversions) {
        super(type, constraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.containerClass = containerClass;
        this.typeArgumentIndex = typeArgumentIndex;
        this.constrainedContainerElementTypes = CollectionHelper.toImmutableSet(constrainedContainerElementTypes);
        this.cascaded = cascaded;
        this.groupConversions = CollectionHelper.toImmutableSet(groupConversions);
    }

    @Override // javax.validation.metadata.ContainerElementTypeDescriptor
    public Class<?> getContainerClass() {
        return this.containerClass;
    }

    @Override // javax.validation.metadata.ContainerElementTypeDescriptor
    public Integer getTypeArgumentIndex() {
        return this.typeArgumentIndex;
    }

    @Override // javax.validation.metadata.ContainerDescriptor
    public Set<ContainerElementTypeDescriptor> getConstrainedContainerElementTypes() {
        return this.constrainedContainerElementTypes;
    }

    @Override // javax.validation.metadata.CascadableDescriptor
    public boolean isCascaded() {
        return this.cascaded;
    }

    @Override // javax.validation.metadata.CascadableDescriptor
    public Set<GroupConversionDescriptor> getGroupConversions() {
        return this.groupConversions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ContainerElementTypeDescriptorImpl{");
        sb.append("containerClass=").append(this.containerClass);
        sb.append(", typeArgumentIndex=").append(this.typeArgumentIndex);
        sb.append(", cascaded=").append(this.cascaded);
        sb.append('}');
        return sb.toString();
    }
}