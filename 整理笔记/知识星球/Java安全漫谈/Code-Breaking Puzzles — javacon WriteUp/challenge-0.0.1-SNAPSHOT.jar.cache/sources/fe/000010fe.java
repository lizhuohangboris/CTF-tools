package org.hibernate.validator.internal.metadata.aggregated;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.util.CollectionHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/aggregated/ValidatableParametersMetaData.class */
public class ValidatableParametersMetaData implements Validatable {
    private final List<ParameterMetaData> parameterMetaData;
    private final List<Cascadable> cascadables;

    public ValidatableParametersMetaData(List<ParameterMetaData> parameterMetaData) {
        this.parameterMetaData = CollectionHelper.toImmutableList(parameterMetaData);
        this.cascadables = CollectionHelper.toImmutableList((List) parameterMetaData.stream().filter(p -> {
            return p.isCascading();
        }).collect(Collectors.toList()));
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public Iterable<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override // org.hibernate.validator.internal.metadata.facets.Validatable
    public boolean hasCascadables() {
        return !this.cascadables.isEmpty();
    }
}