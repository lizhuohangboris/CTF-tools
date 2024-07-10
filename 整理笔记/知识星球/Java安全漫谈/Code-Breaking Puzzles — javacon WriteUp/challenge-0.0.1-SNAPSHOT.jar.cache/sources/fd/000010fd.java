package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.ReturnValueDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ReturnValueDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ReturnValueMetaData.class */
public class ReturnValueMetaData extends AbstractConstraintMetaData implements Validatable, Cascadable {
    private static final String RETURN_VALUE_NODE_NAME = null;
    private final List<Cascadable> cascadables;
    private final CascadingMetaData cascadingMetaData;

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public /* bridge */ /* synthetic */ ElementDescriptor asDescriptor(boolean z, List list) {
        return asDescriptor(z, (List<Class<?>>) list);
    }

    public ReturnValueMetaData(Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, CascadingMetaData cascadingMetaData) {
        super(RETURN_VALUE_NODE_NAME, type, constraints, containerElementsConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), !constraints.isEmpty() || containerElementsConstraints.isEmpty() || cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements());
        this.cascadables = isCascading() ? Collections.singletonList(this) : Collections.emptyList();
        this.cascadingMetaData = cascadingMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public Iterable<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public boolean hasCascadables() {
        return !this.cascadables.isEmpty();
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public ElementType getElementType() {
        return ElementType.METHOD;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ReturnValueDescriptor asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ReturnValueDescriptorImpl(getType(), asDescriptors(getDirectConstraints()), asContainerElementTypeDescriptors(getContainerElementsConstraints(), this.cascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), this.cascadingMetaData.isCascading(), defaultGroupSequenceRedefined, defaultGroupSequence, this.cascadingMetaData.getGroupConversionDescriptors());
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public Object getValue(Object parent) {
        return parent;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public Type getCascadableType() {
        return getType();
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public void appendTo(PathImpl path) {
        path.addReturnValueNode();
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Cascadable
    public CascadingMetaData getCascadingMetaData() {
        return this.cascadingMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ElementKind getKind() {
        return ElementKind.RETURN_VALUE;
    }
}