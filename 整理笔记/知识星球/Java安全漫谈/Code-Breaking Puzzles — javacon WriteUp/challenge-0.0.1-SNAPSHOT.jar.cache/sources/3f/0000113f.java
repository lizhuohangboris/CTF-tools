package org.hibernate.validator.internal.metadata.raw;

import java.util.Collections;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConstrainedType.class */
public class ConstrainedType extends AbstractConstrainedElement {
    private final Class<?> beanClass;

    public ConstrainedType(ConfigurationSource source, Class<?> beanClass, Set<MetaConstraint<?>> constraints) {
        super(source, ConstrainedElement.ConstrainedElementKind.TYPE, constraints, Collections.emptySet(), CascadingMetaDataBuilder.nonCascading());
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + (this.beanClass == null ? 0 : this.beanClass.hashCode());
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ConstrainedType other = (ConstrainedType) obj;
        if (this.beanClass == null) {
            if (other.beanClass != null) {
                return false;
            }
            return true;
        } else if (!this.beanClass.equals(other.beanClass)) {
            return false;
        } else {
            return true;
        }
    }
}