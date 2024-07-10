package org.hibernate.validator.internal.metadata.raw;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.util.StringHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConstrainedField.class */
public class ConstrainedField extends AbstractConstrainedElement {
    private final Field field;

    public ConstrainedField(ConfigurationSource source, Field field, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> typeArgumentConstraints, CascadingMetaDataBuilder cascadingMetaDataBuilder) {
        super(source, ConstrainedElement.ConstrainedElementKind.FIELD, constraints, typeArgumentConstraints, cascadingMetaDataBuilder);
        this.field = field;
    }

    public Field getField() {
        return this.field;
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public String toString() {
        return "ConstrainedField [field=" + StringHelper.toShortString((Member) this.field) + "]";
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + (this.field == null ? 0 : this.field.hashCode());
    }

    @Override // org.hibernate.validator.internal.metadata.raw.AbstractConstrainedElement
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ConstrainedField other = (ConstrainedField) obj;
        if (this.field == null) {
            if (other.field != null) {
                return false;
            }
            return true;
        } else if (!this.field.equals(other.field)) {
            return false;
        } else {
            return true;
        }
    }
}