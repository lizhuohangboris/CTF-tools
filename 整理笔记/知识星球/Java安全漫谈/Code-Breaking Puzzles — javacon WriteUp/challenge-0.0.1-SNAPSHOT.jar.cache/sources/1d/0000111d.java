package org.hibernate.validator.internal.metadata.descriptor;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ConstructorDescriptor;
import javax.validation.metadata.CrossParameterDescriptor;
import javax.validation.metadata.MethodDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import javax.validation.metadata.ReturnValueDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/descriptor/ExecutableDescriptorImpl.class */
public class ExecutableDescriptorImpl extends ElementDescriptorImpl implements ConstructorDescriptor, MethodDescriptor {
    private final String name;
    private final List<ParameterDescriptor> parameters;
    private final CrossParameterDescriptor crossParameterDescriptor;
    private final ReturnValueDescriptor returnValueDescriptor;
    private final boolean isGetter;

    public ExecutableDescriptorImpl(Type returnType, String name, Set<ConstraintDescriptorImpl<?>> crossParameterConstraints, ReturnValueDescriptor returnValueDescriptor, List<ParameterDescriptor> parameters, boolean defaultGroupSequenceRedefined, boolean isGetter, List<Class<?>> defaultGroupSequence) {
        super(returnType, Collections.emptySet(), defaultGroupSequenceRedefined, defaultGroupSequence);
        this.name = name;
        this.parameters = CollectionHelper.toImmutableList(parameters);
        this.returnValueDescriptor = returnValueDescriptor;
        this.crossParameterDescriptor = new CrossParameterDescriptorImpl(crossParameterConstraints, defaultGroupSequenceRedefined, defaultGroupSequence);
        this.isGetter = isGetter;
    }

    @Override // javax.validation.metadata.ExecutableDescriptor
    public String getName() {
        return this.name;
    }

    @Override // javax.validation.metadata.ExecutableDescriptor
    public List<ParameterDescriptor> getParameterDescriptors() {
        return this.parameters;
    }

    @Override // javax.validation.metadata.ExecutableDescriptor
    public ReturnValueDescriptor getReturnValueDescriptor() {
        return this.returnValueDescriptor;
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0021  */
    @Override // javax.validation.metadata.ExecutableDescriptor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean hasConstrainedParameters() {
        /*
            r2 = this;
            r0 = r2
            javax.validation.metadata.CrossParameterDescriptor r0 = r0.crossParameterDescriptor
            boolean r0 = r0.hasConstraints()
            if (r0 == 0) goto Le
            r0 = 1
            return r0
        Le:
            r0 = r2
            java.util.List<javax.validation.metadata.ParameterDescriptor> r0 = r0.parameters
            java.util.Iterator r0 = r0.iterator()
            r3 = r0
        L18:
            r0 = r3
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L42
            r0 = r3
            java.lang.Object r0 = r0.next()
            javax.validation.metadata.ParameterDescriptor r0 = (javax.validation.metadata.ParameterDescriptor) r0
            r4 = r0
            r0 = r4
            boolean r0 = r0.hasConstraints()
            if (r0 != 0) goto L3d
            r0 = r4
            boolean r0 = r0.isCascaded()
            if (r0 == 0) goto L3f
        L3d:
            r0 = 1
            return r0
        L3f:
            goto L18
        L42:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.hibernate.validator.internal.metadata.descriptor.ExecutableDescriptorImpl.hasConstrainedParameters():boolean");
    }

    @Override // javax.validation.metadata.ExecutableDescriptor
    public boolean hasConstrainedReturnValue() {
        return this.returnValueDescriptor != null && (this.returnValueDescriptor.hasConstraints() || this.returnValueDescriptor.isCascaded());
    }

    @Override // javax.validation.metadata.ExecutableDescriptor
    public CrossParameterDescriptor getCrossParameterDescriptor() {
        return this.crossParameterDescriptor;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExecutableDescriptorImpl");
        sb.append("{name='").append(this.name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public boolean isGetter() {
        return this.isGetter;
    }
}