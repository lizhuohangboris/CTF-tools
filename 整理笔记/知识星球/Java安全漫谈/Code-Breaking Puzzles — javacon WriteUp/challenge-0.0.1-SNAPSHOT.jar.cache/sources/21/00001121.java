package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ContainerElementTypeDescriptor;
import javax.validation.metadata.GroupConversionDescriptor;
import javax.validation.metadata.ReturnValueDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ReturnValueDescriptorImpl.class */
public class ReturnValueDescriptorImpl extends ElementDescriptorImpl implements ReturnValueDescriptor {
    private final Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes;
    private final boolean cascaded;
    private final Set<GroupConversionDescriptor> groupConversions;

    public ReturnValueDescriptorImpl(Type returnType, Set<ConstraintDescriptorImpl<?>> returnValueConstraints, Set<ContainerElementTypeDescriptor> constrainedContainerElementTypes, boolean cascaded, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence, Set<GroupConversionDescriptor> groupConversions) {
        super(returnType, returnValueConstraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.constrainedContainerElementTypes = CollectionHelper.toImmutableSet(constrainedContainerElementTypes);
        this.cascaded = cascaded;
        this.groupConversions = CollectionHelper.toImmutableSet(groupConversions);
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
        sb.append("ReturnValueDescriptorImpl");
        sb.append("{cascaded=").append(this.cascaded);
        sb.append('}');
        return sb.toString();
    }
}