package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ElementKind;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.ParameterDescriptor;
import org.hibernate.validator.internal.engine.MethodValidationConfiguration;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.ParameterMetaData;
import org.hibernate.validator.internal.metadata.aggregated.rule.MethodConfigurationRule;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.descriptor.ExecutableDescriptorImpl;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedParameter;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ExecutableMetaData.class */
public class ExecutableMetaData extends AbstractConstraintMetaData {
    private final Class<?>[] parameterTypes;
    private final List<ParameterMetaData> parameterMetaDataList;
    private final ValidatableParametersMetaData validatableParametersMetaData;
    private final Set<MetaConstraint<?>> crossParameterConstraints;
    private final boolean isGetter;
    private final Set<String> signatures;
    private final ReturnValueMetaData returnValueMetaData;
    private final ElementKind kind;

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public /* bridge */ /* synthetic */ ElementDescriptor asDescriptor(boolean z, List list) {
        return asDescriptor(z, (List<Class<?>>) list);
    }

    private ExecutableMetaData(String name, Type returnType, Class<?>[] parameterTypes, ElementKind kind, Set<String> signatures, Set<MetaConstraint<?>> returnValueConstraints, Set<MetaConstraint<?>> returnValueContainerElementConstraints, List<ParameterMetaData> parameterMetaDataList, Set<MetaConstraint<?>> crossParameterConstraints, CascadingMetaData cascadingMetaData, boolean isConstrained, boolean isGetter) {
        super(name, returnType, returnValueConstraints, returnValueContainerElementConstraints, cascadingMetaData.isMarkedForCascadingOnAnnotatedObjectOrContainerElements(), isConstrained);
        this.parameterTypes = parameterTypes;
        this.parameterMetaDataList = CollectionHelper.toImmutableList(parameterMetaDataList);
        this.validatableParametersMetaData = new ValidatableParametersMetaData(parameterMetaDataList);
        this.crossParameterConstraints = CollectionHelper.toImmutableSet(crossParameterConstraints);
        this.signatures = signatures;
        this.returnValueMetaData = new ReturnValueMetaData(returnType, returnValueConstraints, returnValueContainerElementConstraints, cascadingMetaData);
        this.isGetter = isGetter;
        this.kind = kind;
    }

    public ParameterMetaData getParameterMetaData(int parameterIndex) {
        return this.parameterMetaDataList.get(parameterIndex);
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public Set<String> getSignatures() {
        return this.signatures;
    }

    public Set<MetaConstraint<?>> getCrossParameterConstraints() {
        return this.crossParameterConstraints;
    }

    public ValidatableParametersMetaData getValidatableParametersMetaData() {
        return this.validatableParametersMetaData;
    }

    public ReturnValueMetaData getReturnValueMetaData() {
        return this.returnValueMetaData;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ExecutableDescriptorImpl asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        return new ExecutableDescriptorImpl(getType(), getName(), asDescriptors(getCrossParameterConstraints()), this.returnValueMetaData.asDescriptor(defaultGroupSequenceRedefined, defaultGroupSequence), parametersAsDescriptors(defaultGroupSequenceRedefined, defaultGroupSequence), defaultGroupSequenceRedefined, this.isGetter, defaultGroupSequence);
    }

    private List<ParameterDescriptor> parametersAsDescriptors(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        List<ParameterDescriptor> parameterDescriptorList = CollectionHelper.newArrayList();
        for (ParameterMetaData parameterMetaData : this.parameterMetaDataList) {
            parameterDescriptorList.add(parameterMetaData.asDescriptor(defaultGroupSequenceRedefined, defaultGroupSequence));
        }
        return parameterDescriptorList;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ElementKind getKind() {
        return this.kind;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public String toString() {
        Class<?>[] parameterTypes;
        String sb;
        StringBuilder parameterBuilder = new StringBuilder();
        for (Class<?> oneParameterType : getParameterTypes()) {
            parameterBuilder.append(oneParameterType.getSimpleName());
            parameterBuilder.append(", ");
        }
        if (parameterBuilder.length() > 0) {
            sb = parameterBuilder.substring(0, parameterBuilder.length() - 2);
        } else {
            sb = parameterBuilder.toString();
        }
        String parameters = sb;
        return "ExecutableMetaData [executable=" + getType() + " " + getName() + "(" + parameters + "), isCascading=" + isCascading() + ", isConstrained=" + isConstrained() + "]";
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public int hashCode() {
        int result = super.hashCode();
        return (31 * result) + Arrays.hashCode(this.parameterTypes);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ExecutableMetaData other = (ExecutableMetaData) obj;
        if (!Arrays.equals(this.parameterTypes, other.parameterTypes)) {
            return false;
        }
        return true;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ExecutableMetaData$Builder.class */
    public static class Builder extends MetaDataBuilder {
        private final Set<String> signatures;
        private final ConstrainedElement.ConstrainedElementKind kind;
        private final Set<ConstrainedExecutable> constrainedExecutables;
        private Executable executable;
        private final boolean isGetterMethod;
        private final Set<MetaConstraint<?>> crossParameterConstraints;
        private final Set<MethodConfigurationRule> rules;
        private boolean isConstrained;
        private CascadingMetaDataBuilder cascadingMetaDataBuilder;
        private final Map<Class<?>, ConstrainedExecutable> executablesByDeclaringType;
        private final ExecutableHelper executableHelper;
        private final ExecutableParameterNameProvider parameterNameProvider;

        public Builder(Class<?> beanClass, ConstrainedExecutable constrainedExecutable, ConstraintHelper constraintHelper, ExecutableHelper executableHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager, ExecutableParameterNameProvider parameterNameProvider, MethodValidationConfiguration methodValidationConfiguration) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.signatures = CollectionHelper.newHashSet();
            this.constrainedExecutables = CollectionHelper.newHashSet();
            this.crossParameterConstraints = CollectionHelper.newHashSet();
            this.isConstrained = false;
            this.executablesByDeclaringType = CollectionHelper.newHashMap();
            this.executableHelper = executableHelper;
            this.parameterNameProvider = parameterNameProvider;
            this.kind = constrainedExecutable.getKind();
            this.executable = constrainedExecutable.getExecutable();
            this.rules = methodValidationConfiguration.getConfiguredRuleSet();
            this.isGetterMethod = constrainedExecutable.isGetterMethod();
            add(constrainedExecutable);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public boolean accepts(ConstrainedElement constrainedElement) {
            if (this.kind != constrainedElement.getKind()) {
                return false;
            }
            Executable candidate = ((ConstrainedExecutable) constrainedElement).getExecutable();
            return this.executable.equals(candidate) || overrides(this.executable, candidate) || overrides(candidate, this.executable);
        }

        private boolean overrides(Executable first, Executable other) {
            if ((first instanceof Constructor) || (other instanceof Constructor)) {
                return false;
            }
            return this.executableHelper.overrides((Method) first, (Method) other);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public final void add(ConstrainedElement constrainedElement) {
            super.add(constrainedElement);
            ConstrainedExecutable constrainedExecutable = (ConstrainedExecutable) constrainedElement;
            this.signatures.add(ExecutableHelper.getSignature(constrainedExecutable.getExecutable()));
            this.constrainedExecutables.add(constrainedExecutable);
            this.isConstrained = this.isConstrained || constrainedExecutable.isConstrained();
            this.crossParameterConstraints.addAll(constrainedExecutable.getCrossParameterConstraints());
            if (this.cascadingMetaDataBuilder == null) {
                this.cascadingMetaDataBuilder = constrainedExecutable.getCascadingMetaDataBuilder();
            } else {
                this.cascadingMetaDataBuilder = this.cascadingMetaDataBuilder.merge(constrainedExecutable.getCascadingMetaDataBuilder());
            }
            addToExecutablesByDeclaringType(constrainedExecutable);
            if (this.executable != null && overrides(constrainedExecutable.getExecutable(), this.executable)) {
                this.executable = constrainedExecutable.getExecutable();
            }
        }

        private void addToExecutablesByDeclaringType(ConstrainedExecutable executable) {
            ConstrainedExecutable mergedExecutable;
            Class<?> beanClass = executable.getExecutable().getDeclaringClass();
            ConstrainedExecutable mergedExecutable2 = this.executablesByDeclaringType.get(beanClass);
            if (mergedExecutable2 != null) {
                mergedExecutable = mergedExecutable2.merge(executable);
            } else {
                mergedExecutable = executable;
            }
            this.executablesByDeclaringType.put(beanClass, mergedExecutable);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public ExecutableMetaData build() {
            Set immutableSet;
            assertCorrectnessOfConfiguration();
            String simpleName = this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR ? this.executable.getDeclaringClass().getSimpleName() : this.executable.getName();
            Type typeOf = ReflectionHelper.typeOf(this.executable);
            Class<?>[] parameterTypes = this.executable.getParameterTypes();
            ElementKind elementKind = this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR ? ElementKind.CONSTRUCTOR : ElementKind.METHOD;
            if (this.kind == ConstrainedElement.ConstrainedElementKind.CONSTRUCTOR) {
                immutableSet = Collections.singleton(ExecutableHelper.getSignature(this.executable));
            } else {
                immutableSet = CollectionHelper.toImmutableSet(this.signatures);
            }
            return new ExecutableMetaData(simpleName, typeOf, parameterTypes, elementKind, immutableSet, adaptOriginsAndImplicitGroups(getDirectConstraints()), adaptOriginsAndImplicitGroups(getContainerElementConstraints()), findParameterMetaData(), adaptOriginsAndImplicitGroups(this.crossParameterConstraints), this.cascadingMetaDataBuilder.build(this.valueExtractorManager, this.executable), this.isConstrained, this.isGetterMethod);
        }

        private List<ParameterMetaData> findParameterMetaData() {
            List<ParameterMetaData.Builder> parameterBuilders = null;
            for (ConstrainedExecutable oneExecutable : this.constrainedExecutables) {
                if (parameterBuilders == null) {
                    parameterBuilders = CollectionHelper.newArrayList();
                    for (ConstrainedParameter oneParameter : oneExecutable.getAllParameterMetaData()) {
                        parameterBuilders.add(new ParameterMetaData.Builder(this.executable.getDeclaringClass(), oneParameter, this.constraintHelper, this.typeResolutionHelper, this.valueExtractorManager, this.parameterNameProvider));
                    }
                } else {
                    int i = 0;
                    for (ConstrainedParameter oneParameter2 : oneExecutable.getAllParameterMetaData()) {
                        parameterBuilders.get(i).add(oneParameter2);
                        i++;
                    }
                }
            }
            List<ParameterMetaData> parameterMetaDatas = CollectionHelper.newArrayList();
            for (ParameterMetaData.Builder oneBuilder : parameterBuilders) {
                parameterMetaDatas.add(oneBuilder.build());
            }
            return parameterMetaDatas;
        }

        private void assertCorrectnessOfConfiguration() {
            for (Map.Entry<Class<?>, ConstrainedExecutable> entry : this.executablesByDeclaringType.entrySet()) {
                for (Map.Entry<Class<?>, ConstrainedExecutable> otherEntry : this.executablesByDeclaringType.entrySet()) {
                    for (MethodConfigurationRule rule : this.rules) {
                        rule.apply(entry.getValue(), otherEntry.getValue());
                    }
                }
            }
        }
    }
}