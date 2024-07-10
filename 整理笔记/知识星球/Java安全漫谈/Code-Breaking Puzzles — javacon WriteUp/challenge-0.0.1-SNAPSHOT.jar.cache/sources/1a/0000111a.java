package org.hibernate.validator.internal.metadata.descriptor;

import java.util.List;
import java.util.Set;
import javax.validation.metadata.CrossParameterDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/CrossParameterDescriptorImpl.class */
public class CrossParameterDescriptorImpl extends ElementDescriptorImpl implements CrossParameterDescriptor {
    public CrossParameterDescriptorImpl(Set<ConstraintDescriptorImpl<?>> constraintDescriptors, boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        super(Object[].class, constraintDescriptors, defaultGroupSequenceRedefined, defaultGroupSequence);
    }
}