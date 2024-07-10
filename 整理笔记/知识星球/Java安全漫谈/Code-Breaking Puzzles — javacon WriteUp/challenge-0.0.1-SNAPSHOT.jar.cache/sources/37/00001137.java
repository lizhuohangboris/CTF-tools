package org.hibernate.validator.internal.metadata.raw;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/AbstractConstrainedElement.class */
public abstract class AbstractConstrainedElement implements ConstrainedElement {
    private final ConstrainedElement.ConstrainedElementKind kind;
    protected final ConfigurationSource source;
    protected final Set<MetaConstraint<?>> constraints;
    protected final CascadingMetaDataBuilder cascadingMetaDataBuilder;
    protected final Set<MetaConstraint<?>> typeArgumentConstraints;

    public AbstractConstrainedElement(ConfigurationSource source, ConstrainedElement.ConstrainedElementKind kind, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        this.kind = kind;
        this.source = source;
        this.constraints = constraints != null ? CollectionHelper.toImmutableSet(constraints) : Collections.emptySet();
        this.typeArgumentConstraints = typeArgumentConstraints != null ? CollectionHelper.toImmutableSet(typeArgumentConstraints) : Collections.emptySet();
        this.cascadingMetaDataBuilder = cascadingMetaDataBuilder;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public ConstrainedElement.ConstrainedElementKind getKind() {
        return this.kind;
    }

    @Override // java.lang.Iterable
    public Iterator<MetaConstraint<?>> iterator() {
        return this.constraints.iterator();
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public Set<MetaConstraint<?>> getConstraints() {
        return this.constraints;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public Set<MetaConstraint<?>> getTypeArgumentConstraints() {
        return this.typeArgumentConstraints;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public CascadingMetaDataBuilder getCascadingMetaDataBuilder() {
        return this.cascadingMetaDataBuilder;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public boolean isConstrained() {
        return this.cascadingMetaDataBuilder.isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || this.cascadingMetaDataBuilder.hasGroupConversionsOnAnnotatedObjectOrContainerElements() || !this.constraints.isEmpty() || !this.typeArgumentConstraints.isEmpty();
    }

    @Override // org.hibernate.validator.internal.metadata.raw.ConstrainedElement
    public ConfigurationSource getSource() {
        return this.source;
    }

    public String toString() {
        return "AbstractConstrainedElement [kind=" + this.kind + ", source=" + this.source + ", constraints=" + this.constraints + ", cascadingMetaDataBuilder=" + this.cascadingMetaDataBuilder + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.source == null ? 0 : this.source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractConstrainedElement other = (AbstractConstrainedElement) obj;
        if (this.source != other.source) {
            return false;
        }
        return true;
    }
}