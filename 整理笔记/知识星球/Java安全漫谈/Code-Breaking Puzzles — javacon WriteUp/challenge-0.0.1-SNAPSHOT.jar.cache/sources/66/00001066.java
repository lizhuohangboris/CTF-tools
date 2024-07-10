package org.hibernate.validator.internal.engine;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule;
import org.hibernate.validator.internal.metadata.aggregated.rule.OverridingMethodMustNotAlterParameterConstraints;
import org.hibernate.validator.internal.metadata.aggregated.rule.ParallelMethodsMustNotDefineGroupConversionForCascadedReturnValue;
import org.hibernate.validator.internal.metadata.aggregated.rule.ParallelMethodsMustNotDefineParameterConstraints;
import org.hibernate.validator.internal.metadata.aggregated.rule.ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine;
import org.hibernate.validator.internal.metadata.aggregated.rule.VoidMethodsMustNotBeReturnValueConstrained;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/MethodValidationConfiguration.class */
public class MethodValidationConfiguration {
    private boolean allowOverridingMethodAlterParameterConstraint;
    private boolean allowMultipleCascadedValidationOnReturnValues;
    private boolean allowParallelMethodsDefineParameterConstraints;
    private Set<MethodConfigurationRule> configuredRuleSet;

    private MethodValidationConfiguration(boolean allowOverridingMethodAlterParameterConstraint, boolean allowMultipleCascadedValidationOnReturnValues, boolean allowParallelMethodsDefineParameterConstraints) {
        this.allowOverridingMethodAlterParameterConstraint = false;
        this.allowMultipleCascadedValidationOnReturnValues = false;
        this.allowParallelMethodsDefineParameterConstraints = false;
        this.allowOverridingMethodAlterParameterConstraint = allowOverridingMethodAlterParameterConstraint;
        this.allowMultipleCascadedValidationOnReturnValues = allowMultipleCascadedValidationOnReturnValues;
        this.allowParallelMethodsDefineParameterConstraints = allowParallelMethodsDefineParameterConstraints;
        this.configuredRuleSet = buildConfiguredRuleSet(allowOverridingMethodAlterParameterConstraint, allowMultipleCascadedValidationOnReturnValues, allowParallelMethodsDefineParameterConstraints);
    }

    public boolean isAllowOverridingMethodAlterParameterConstraint() {
        return this.allowOverridingMethodAlterParameterConstraint;
    }

    public boolean isAllowMultipleCascadedValidationOnReturnValues() {
        return this.allowMultipleCascadedValidationOnReturnValues;
    }

    public boolean isAllowParallelMethodsDefineParameterConstraints() {
        return this.allowParallelMethodsDefineParameterConstraints;
    }

    public Set<MethodConfigurationRule> getConfiguredRuleSet() {
        return this.configuredRuleSet;
    }

    private static Set<MethodConfigurationRule> buildConfiguredRuleSet(boolean allowOverridingMethodAlterParameterConstraint, boolean allowMultipleCascadedValidationOnReturnValues, boolean allowParallelMethodsDefineParameterConstraints) {
        HashSet<MethodConfigurationRule> result = CollectionHelper.newHashSet(5);
        if (!allowOverridingMethodAlterParameterConstraint) {
            result.add(new OverridingMethodMustNotAlterParameterConstraints());
        }
        if (!allowParallelMethodsDefineParameterConstraints) {
            result.add(new ParallelMethodsMustNotDefineParameterConstraints());
        }
        result.add(new VoidMethodsMustNotBeReturnValueConstrained());
        if (!allowMultipleCascadedValidationOnReturnValues) {
            result.add(new ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine());
        }
        result.add(new ParallelMethodsMustNotDefineGroupConversionForCascadedReturnValue());
        return CollectionHelper.toImmutableSet(result);
    }

    public int hashCode() {
        int result = (31 * 1) + (this.allowMultipleCascadedValidationOnReturnValues ? 1231 : 1237);
        return (31 * ((31 * result) + (this.allowOverridingMethodAlterParameterConstraint ? 1231 : 1237))) + (this.allowParallelMethodsDefineParameterConstraints ? 1231 : 1237);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MethodValidationConfiguration other = (MethodValidationConfiguration) obj;
        if (this.allowMultipleCascadedValidationOnReturnValues != other.allowMultipleCascadedValidationOnReturnValues || this.allowOverridingMethodAlterParameterConstraint != other.allowOverridingMethodAlterParameterConstraint || this.allowParallelMethodsDefineParameterConstraints != other.allowParallelMethodsDefineParameterConstraints) {
            return false;
        }
        return true;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/MethodValidationConfiguration$Builder.class */
    public static class Builder {
        private boolean allowOverridingMethodAlterParameterConstraint;
        private boolean allowMultipleCascadedValidationOnReturnValues;
        private boolean allowParallelMethodsDefineParameterConstraints;

        public Builder() {
            this.allowOverridingMethodAlterParameterConstraint = false;
            this.allowMultipleCascadedValidationOnReturnValues = false;
            this.allowParallelMethodsDefineParameterConstraints = false;
        }

        public Builder(MethodValidationConfiguration template) {
            this.allowOverridingMethodAlterParameterConstraint = false;
            this.allowMultipleCascadedValidationOnReturnValues = false;
            this.allowParallelMethodsDefineParameterConstraints = false;
            this.allowOverridingMethodAlterParameterConstraint = template.allowOverridingMethodAlterParameterConstraint;
            this.allowMultipleCascadedValidationOnReturnValues = template.allowMultipleCascadedValidationOnReturnValues;
            this.allowParallelMethodsDefineParameterConstraints = template.allowParallelMethodsDefineParameterConstraints;
        }

        public Builder allowOverridingMethodAlterParameterConstraint(boolean allow) {
            this.allowOverridingMethodAlterParameterConstraint = allow;
            return this;
        }

        public Builder allowMultipleCascadedValidationOnReturnValues(boolean allow) {
            this.allowMultipleCascadedValidationOnReturnValues = allow;
            return this;
        }

        public Builder allowParallelMethodsDefineParameterConstraints(boolean allow) {
            this.allowParallelMethodsDefineParameterConstraints = allow;
            return this;
        }

        public boolean isAllowOverridingMethodAlterParameterConstraint() {
            return this.allowOverridingMethodAlterParameterConstraint;
        }

        public boolean isAllowMultipleCascadedValidationOnReturnValues() {
            return this.allowMultipleCascadedValidationOnReturnValues;
        }

        public boolean isAllowParallelMethodsDefineParameterConstraints() {
            return this.allowParallelMethodsDefineParameterConstraints;
        }

        public MethodValidationConfiguration build() {
            return new MethodValidationConfiguration(this.allowOverridingMethodAlterParameterConstraint, this.allowMultipleCascadedValidationOnReturnValues, this.allowParallelMethodsDefineParameterConstraints);
        }
    }
}