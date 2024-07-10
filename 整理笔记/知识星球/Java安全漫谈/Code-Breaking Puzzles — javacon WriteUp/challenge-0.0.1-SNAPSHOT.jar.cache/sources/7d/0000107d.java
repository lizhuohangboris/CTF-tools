package org.hibernate.validator.internal.engine.constraintdefinition;

import java.lang.annotation.Annotation;
import java.util.List;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/constraintdefinition/ConstraintDefinitionContribution.class */
public class ConstraintDefinitionContribution<A extends Annotation> {
    private final Class<A> constraintType;
    private final List<ConstraintValidatorDescriptor<A>> validatorDescriptors;
    private final boolean includeExisting;

    public ConstraintDefinitionContribution(Class<A> constraintType, List<ConstraintValidatorDescriptor<A>> validatorDescriptors, boolean includeExisting) {
        this.constraintType = constraintType;
        this.validatorDescriptors = CollectionHelper.toImmutableList(validatorDescriptors);
        this.includeExisting = includeExisting;
    }

    public Class<A> getConstraintType() {
        return this.constraintType;
    }

    public List<ConstraintValidatorDescriptor<A>> getValidatorDescriptors() {
        return this.validatorDescriptors;
    }

    public boolean includeExisting() {
        return this.includeExisting;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConstraintDefinitionContribution<?> that = (ConstraintDefinitionContribution) o;
        if (!this.constraintType.equals(that.constraintType) || !this.validatorDescriptors.equals(that.validatorDescriptors)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.constraintType.hashCode();
        return (31 * result) + this.validatorDescriptors.hashCode();
    }

    public String toString() {
        return "ConstraintDefinitionContribution{constraintType=" + this.constraintType + ", validatorDescriptors=" + this.validatorDescriptors + ", includeExisting=" + this.includeExisting + '}';
    }
}