package org.hibernate.validator.internal.metadata.raw;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConstrainedExecutable.class */
public class ConstrainedExecutable extends AbstractConstrainedElement {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Executable executable;
    private final List<ConstrainedParameter> parameterMetaData;
    private final boolean hasParameterConstraints;
    private final Set<MetaConstraint<?>> crossParameterConstraints;
    private final boolean isGetterMethod;

    public ConstrainedExecutable(ConfigurationSource source, Executable executable, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        this(source, executable, Collections.emptyList(), Collections.emptySet(), returnValueConstraints, typeArgumentConstraints, cascadingMetaDataBuilder);
    }

    public ConstrainedExecutable(ConfigurationSource source, Executable executable, List<ConstrainedParameter> parameterMetaData, Set<MetaConstraint<?>> crossParameterConstraints, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        super(source, executable instanceof Constructor ? ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR : ConstrainedElement.ConstrainedElementKind.METHOD, returnValueConstraints, typeArgumentConstraints, cascadingMetaDataBuilder);
        this.executable = executable;
        if (parameterMetaData.size() != executable.getParameterTypes().length) {
            throw LOG.getInvalidLengthOfParameterMetaDataListException(executable, executable.getParameterTypes().length, parameterMetaData.size());
        }
        this.crossParameterConstraints = CollectionHelper.toImmutableSet(crossParameterConstraints);
        this.parameterMetaData = CollectionHelper.toImmutableList(parameterMetaData);
        this.hasParameterConstraints = hasParameterConstraints(parameterMetaData) || !crossParameterConstraints.isEmpty();
        this.isGetterMethod = ReflectionHelper.isGetterMethod(executable);
    }

    public ConstrainedParameter getParameterMetaData(int parameterIndex) {
        if (parameterIndex < 0 || parameterIndex > this.parameterMetaData.size() - 1) {
            throw LOG.getInvalidExecutableParameterIndexException(this.executable, parameterIndex);
        }
        return this.parameterMetaData.get(parameterIndex);
    }

    public List<ConstrainedParameter> getAllParameterMetaData() {
        return this.parameterMetaData;
    }

    public Set<MetaConstraint<?>> getCrossParameterConstraints() {
        return this.crossParameterConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement, org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public boolean isConstrained() {
        return super.isConstrained() || this.hasParameterConstraints;
    }

    public boolean hasParameterConstraints() {
        return this.hasParameterConstraints;
    }

    public boolean isGetterMethod() {
        return this.isGetterMethod;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public String toString() {
        return "ConstrainedExecutable [executable=" + StringHelper.toShortString(this.executable) + ", parameterMetaData=" + this.parameterMetaData + ", hasParameterConstraints=" + this.hasParameterConstraints + "]";
    }

    private boolean hasParameterConstraints(List<ConstrainedParameter> parameterMetaData) {
        for (ConstrainedParameter oneParameter : parameterMetaData) {
            if (oneParameter.isConstrained()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEquallyParameterConstrained(ConstrainedExecutable other) {
        if (!getDescriptors(this.crossParameterConstraints).equals(getDescriptors(other.crossParameterConstraints))) {
            return false;
        }
        int i = 0;
        for (ConstrainedParameter parameter : this.parameterMetaData) {
            ConstrainedParameter otherParameter = other.getParameterMetaData(i);
            if (!parameter.getCascadingMetaDataBuilder().equals(otherParameter.getCascadingMetaDataBuilder()) || !getDescriptors(parameter.getConstraints()).equals(getDescriptors(otherParameter.getConstraints()))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public ConstrainedExecutable merge(ConstrainedExecutable other) {
        ConfigurationSource mergedSource = ConfigurationSource.max(this.source, other.source);
        List<ConstrainedParameter> mergedParameterMetaData = CollectionHelper.newArrayList(this.parameterMetaData.size());
        int i = 0;
        for (ConstrainedParameter parameter : this.parameterMetaData) {
            mergedParameterMetaData.add(parameter.merge(other.getParameterMetaData(i)));
            i++;
        }
        Set<MetaConstraint<?>> mergedCrossParameterConstraints = CollectionHelper.newHashSet((Collection) this.crossParameterConstraints);
        mergedCrossParameterConstraints.addAll(other.crossParameterConstraints);
        Set<MetaConstraint<?>> mergedReturnValueConstraints = CollectionHelper.newHashSet((Collection) this.constraints);
        mergedReturnValueConstraints.addAll(other.constraints);
        Set<MetaConstraint<?>> mergedTypeArgumentConstraints = new HashSet<>(this.typeArgumentConstraints);
        mergedTypeArgumentConstraints.addAll(other.typeArgumentConstraints);
        CascadingMetaDataBuilder mergedCascadingMetaDataBuilder = this.cascadingMetaDataBuilder.merge(other.cascadingMetaDataBuilder);
        return new ConstrainedExecutable(mergedSource, this.executable, mergedParameterMetaData, mergedCrossParameterConstraints, mergedReturnValueConstraints, mergedTypeArgumentConstraints, mergedCascadingMetaDataBuilder);
    }

    private Set<ConstraintDescriptor<?>> getDescriptors(Iterable<MetaConstraint<?>> constraints) {
        Set<ConstraintDescriptor<?>> descriptors = CollectionHelper.newHashSet();
        for (MetaConstraint<?> constraint : constraints) {
            descriptors.add(constraint.getDescriptor());
        }
        return descriptors;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + (this.executable == null ? 0 : this.executable.hashCode());
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ConstrainedExecutable other = (ConstrainedExecutable) obj;
        if (this.executable == null) {
            if (other.executable != null) {
                return false;
            }
            return true;
        } else if (!this.executable.equals(other.executable)) {
            return false;
        } else {
            return true;
        }
    }
}