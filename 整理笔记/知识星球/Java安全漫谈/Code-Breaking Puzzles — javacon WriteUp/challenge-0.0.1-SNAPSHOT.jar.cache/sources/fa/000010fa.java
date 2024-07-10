package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ElementKind;
import javax.validation.metadata.ElementDescriptor;
import org.hibernate.validator.HibernateValidatorPermission;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.FieldCascadable;
import org.hibernate.validator.internal.metadata.aggregated.GetterCascadable;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.core.MetaConstraints;
import org.hibernate.validator.internal.metadata.descriptor.PropertyDescriptorImpl;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.metadata.location.GetterConstraintLocation;
import org.hibernate.validator.internal.metadata.location.TypeArgumentConstraintLocation;
import org.hibernate.validator.internal.metadata.raw.ConstrainedElement;
import org.hibernate.validator.internal.metadata.raw.ConstrainedExecutable;
import org.hibernate.validator.internal.metadata.raw.ConstrainedField;
import org.hibernate.validator.internal.metadata.raw.ConstrainedType;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeResolutionHelper;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredMethod;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/PropertyMetaData.class */
public class PropertyMetaData extends AbstractConstraintMetaData {
    private final Set<Cascadable> cascadables;

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public /* bridge */ /* synthetic */ ElementDescriptor asDescriptor(boolean z, List list) {
        return asDescriptor(z, (List<Class<?>>) list);
    }

    private PropertyMetaData(String propertyName, Type type, Set<MetaConstraint<?>> constraints, Set<MetaConstraint<?>> containerElementsConstraints, Set<Cascadable> cascadables, boolean cascadingProperty) {
        super(propertyName, type, constraints, containerElementsConstraints, !cascadables.isEmpty(), (cascadables.isEmpty() && constraints.isEmpty() && containerElementsConstraints.isEmpty()) ? false : true);
        this.cascadables = CollectionHelper.toImmutableSet(cascadables);
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public PropertyDescriptorImpl asDescriptor(boolean defaultGroupSequenceRedefined, List<Class<?>> defaultGroupSequence) {
        CascadingMetaData firstCascadingMetaData = this.cascadables.isEmpty() ? null : this.cascadables.iterator().next().getCascadingMetaData();
        return new PropertyDescriptorImpl(getType(), getName(), asDescriptors(getDirectConstraints()), asContainerElementTypeDescriptors(getContainerElementsConstraints(), firstCascadingMetaData, defaultGroupSequenceRedefined, defaultGroupSequence), firstCascadingMetaData != null ? firstCascadingMetaData.isCascading() : false, defaultGroupSequenceRedefined, defaultGroupSequence, firstCascadingMetaData != null ? firstCascadingMetaData.getGroupConversionDescriptors() : Collections.emptySet());
    }

    public Set<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public String toString() {
        return "PropertyMetaData [type=" + getType() + ", propertyName=" + getName() + "]]";
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.ConstraintMetaData
    public ElementKind getKind() {
        return ElementKind.PROPERTY;
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public int hashCode() {
        return super.hashCode();
    }

    @Override // org.hibernate.validator.internal.metadata.aggregated.AbstractConstraintMetaData
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/PropertyMetaData$Builder.class */
    public static class Builder extends MetaDataBuilder {
        private static final EnumSet<ConstrainedElement.ConstrainedElementKind> SUPPORTED_ELEMENT_KINDS = EnumSet.of(ConstrainedElement.ConstrainedElementKind.TYPE, ConstrainedElement.ConstrainedElementKind.FIELD, ConstrainedElement.ConstrainedElementKind.METHOD);
        private final String propertyName;
        private final Map<Member, Cascadable.Builder> cascadableBuilders;
        private final Type propertyType;
        private boolean cascadingProperty;
        private Method getterAccessibleMethod;

        public Builder(Class<?> beanClass, ConstrainedField constrainedField, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.cascadableBuilders = new HashMap();
            this.cascadingProperty = false;
            this.propertyName = constrainedField.getField().getName();
            this.propertyType = ReflectionHelper.typeOf(constrainedField.getField());
            add(constrainedField);
        }

        public Builder(Class<?> beanClass, ConstrainedType constrainedType, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.cascadableBuilders = new HashMap();
            this.cascadingProperty = false;
            this.propertyName = null;
            this.propertyType = null;
            add(constrainedType);
        }

        public Builder(Class<?> beanClass, ConstrainedExecutable constrainedMethod, ConstraintHelper constraintHelper, TypeResolutionHelper typeResolutionHelper, ValueExtractorManager valueExtractorManager) {
            super(beanClass, constraintHelper, typeResolutionHelper, valueExtractorManager);
            this.cascadableBuilders = new HashMap();
            this.cascadingProperty = false;
            this.propertyName = ReflectionHelper.getPropertyName(constrainedMethod.getExecutable());
            this.propertyType = ReflectionHelper.typeOf(constrainedMethod.getExecutable());
            add(constrainedMethod);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public boolean accepts(ConstrainedElement constrainedElement) {
            if (!SUPPORTED_ELEMENT_KINDS.contains(constrainedElement.getKind())) {
                return false;
            }
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD && !((ConstrainedExecutable) constrainedElement).isGetterMethod()) {
                return false;
            }
            return Objects.equals(getPropertyName(constrainedElement), this.propertyName);
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public final void add(ConstrainedElement constrainedElement) {
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD && constrainedElement.isConstrained()) {
                this.getterAccessibleMethod = getAccessible((Method) ((ConstrainedExecutable) constrainedElement).getExecutable());
            }
            super.add(constrainedElement);
            this.cascadingProperty = this.cascadingProperty || constrainedElement.getCascadingMetaDataBuilder().isCascading();
            if (constrainedElement.getCascadingMetaDataBuilder().isMarkedForCascadingOnAnnotatedObjectOrContainerElements() || constrainedElement.getCascadingMetaDataBuilder().hasGroupConversionsOnAnnotatedObjectOrContainerElements()) {
                if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.FIELD) {
                    Field field = ((ConstrainedField) constrainedElement).getField();
                    Cascadable.Builder builder = this.cascadableBuilders.get(field);
                    if (builder == null) {
                        this.cascadableBuilders.put(field, new FieldCascadable.Builder(this.valueExtractorManager, field, constrainedElement.getCascadingMetaDataBuilder()));
                    } else {
                        builder.mergeCascadingMetaData(constrainedElement.getCascadingMetaDataBuilder());
                    }
                } else if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD) {
                    Method method = (Method) ((ConstrainedExecutable) constrainedElement).getExecutable();
                    Cascadable.Builder builder2 = this.cascadableBuilders.get(method);
                    if (builder2 == null) {
                        this.cascadableBuilders.put(method, new GetterCascadable.Builder(this.valueExtractorManager, this.getterAccessibleMethod, constrainedElement.getCascadingMetaDataBuilder()));
                    } else {
                        builder2.mergeCascadingMetaData(constrainedElement.getCascadingMetaDataBuilder());
                    }
                }
            }
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        protected Set<MetaConstraint<?>> adaptConstraints(ConstrainedElement constrainedElement, Set<MetaConstraint<?>> constraints) {
            if (constraints.isEmpty() || constrainedElement.getKind() != ConstrainedElement.ConstrainedElementKind.METHOD) {
                return constraints;
            }
            ConstraintLocation getterConstraintLocation = ConstraintLocation.forGetter(this.getterAccessibleMethod);
            return (Set) constraints.stream().map(c -> {
                return withGetterLocation(getterConstraintLocation, c);
            }).collect(Collectors.toSet());
        }

        private MetaConstraint<?> withGetterLocation(ConstraintLocation getterConstraintLocation, MetaConstraint<?> constraint) {
            ConstraintLocation converted = null;
            if (!(constraint.getLocation() instanceof TypeArgumentConstraintLocation)) {
                if (constraint.getLocation() instanceof GetterConstraintLocation) {
                    converted = constraint.getLocation();
                } else {
                    converted = getterConstraintLocation;
                }
            } else {
                Deque<ConstraintLocation> locationStack = new ArrayDeque<>();
                ConstraintLocation current = constraint.getLocation();
                do {
                    locationStack.addFirst(current);
                    if (current instanceof TypeArgumentConstraintLocation) {
                        current = ((TypeArgumentConstraintLocation) current).getDelegate();
                    } else {
                        current = null;
                    }
                } while (current != null);
                for (ConstraintLocation location : locationStack) {
                    if (!(location instanceof TypeArgumentConstraintLocation)) {
                        if (location instanceof GetterConstraintLocation) {
                            converted = location;
                        } else {
                            converted = getterConstraintLocation;
                        }
                    } else {
                        converted = ConstraintLocation.forTypeArgument(converted, ((TypeArgumentConstraintLocation) location).getTypeParameter(), location.getTypeForValidatorResolution());
                    }
                }
            }
            return MetaConstraints.create(this.typeResolutionHelper, this.valueExtractorManager, constraint.getDescriptor(), converted);
        }

        private String getPropertyName(ConstrainedElement constrainedElement) {
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.FIELD) {
                return ReflectionHelper.getPropertyName(((ConstrainedField) constrainedElement).getField());
            }
            if (constrainedElement.getKind() == ConstrainedElement.ConstrainedElementKind.METHOD) {
                return ReflectionHelper.getPropertyName(((ConstrainedExecutable) constrainedElement).getExecutable());
            }
            return null;
        }

        private Method getAccessible(Method original) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(HibernateValidatorPermission.ACCESS_PRIVATE_MEMBERS);
            }
            Class<?> clazz = original.getDeclaringClass();
            return (Method) run(GetDeclaredMethod.andMakeAccessible(clazz, original.getName(), new Class[0]));
        }

        private <T> T run(PrivilegedAction<T> action) {
            return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
        }

        @Override // org.hibernate.validator.internal.metadata.aggregated.MetaDataBuilder
        public PropertyMetaData build() {
            Set<Cascadable> cascadables = (Set) this.cascadableBuilders.values().stream().map(b -> {
                return b.build();
            }).collect(Collectors.toSet());
            return new PropertyMetaData(this.propertyName, this.propertyType, adaptOriginsAndImplicitGroups(getDirectConstraints()), adaptOriginsAndImplicitGroups(getContainerElementConstraints()), cascadables, this.cascadingProperty);
        }
    }
}