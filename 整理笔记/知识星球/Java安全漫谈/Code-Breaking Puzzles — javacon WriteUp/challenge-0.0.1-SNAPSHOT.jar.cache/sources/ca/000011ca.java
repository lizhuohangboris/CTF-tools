package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ContainerElementTypeConfigurationBuilder.class */
public class ContainerElementTypeConfigurationBuilder {
    private final List<ContainerElementTypeStaxBuilder> containerElementTypeStaxBuilders = new ArrayList();
    private final Set<ContainerElementTypePath> configuredPaths = new HashSet();

    public void add(ContainerElementTypeStaxBuilder containerElementTypeStaxBuilder) {
        this.containerElementTypeStaxBuilders.add(containerElementTypeStaxBuilder);
    }

    public ContainerElementTypeConfiguration build(ConstraintLocation parentConstraintLocation, Type enclosingType) {
        return build(ContainerElementTypePath.root(), parentConstraintLocation, enclosingType);
    }

    private ContainerElementTypeConfiguration build(ContainerElementTypePath parentConstraintElementTypePath, ConstraintLocation parentConstraintLocation, Type enclosingType) {
        return (ContainerElementTypeConfiguration) this.containerElementTypeStaxBuilders.stream().map(builder -> {
            return builder.build(this.configuredPaths, parentConstraintElementTypePath, parentConstraintLocation, enclosingType);
        }).reduce(ContainerElementTypeConfiguration.EMPTY_CONFIGURATION, ContainerElementTypeConfiguration::merge);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ContainerElementTypeConfigurationBuilder$ContainerElementTypeConfiguration.class */
    public static class ContainerElementTypeConfiguration {
        public static final ContainerElementTypeConfiguration EMPTY_CONFIGURATION = new ContainerElementTypeConfiguration(Collections.emptySet(), Collections.emptyMap());
        private final Set<MetaConstraint<?>> metaConstraints;
        private final Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaDataBuilder;

        public ContainerElementTypeConfiguration(Set<MetaConstraint<?>> metaConstraints, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData) {
            this.metaConstraints = metaConstraints;
            this.containerElementTypesCascadingMetaDataBuilder = containerElementTypesCascadingMetaData;
        }

        public Set<MetaConstraint<?>> getMetaConstraints() {
            return this.metaConstraints;
        }

        public Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaData() {
            return this.containerElementTypesCascadingMetaDataBuilder;
        }

        public static ContainerElementTypeConfiguration merge(ContainerElementTypeConfiguration l, ContainerElementTypeConfiguration r) {
            return new ContainerElementTypeConfiguration((Set) Stream.concat(l.metaConstraints.stream(), r.metaConstraints.stream()).collect(Collectors.toSet()), (Map) Stream.concat(l.containerElementTypesCascadingMetaDataBuilder.entrySet().stream(), r.containerElementTypesCascadingMetaDataBuilder.entrySet().stream()).collect(Collectors.toMap((v0) -> {
                return v0.getKey();
            }, (v0) -> {
                return v0.getValue();
            })));
        }
    }
}