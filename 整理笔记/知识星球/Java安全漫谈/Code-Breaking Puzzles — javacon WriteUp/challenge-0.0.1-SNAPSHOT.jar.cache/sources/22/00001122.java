package org.hibernate.validator.internal.metadata.facets;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/facets/Cascadable.class */
public interface Cascadable {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/facets/Cascadable$Builder.class */
    public interface Builder {
        void mergeCascadingMetaData(CascadingMetaDataBuilder cascadingMetaDataBuilder);

        Cascadable build();
    }

    ElementType getElementType();

    Type getCascadableType();

    Object getValue(Object obj);

    void appendTo(PathImpl pathImpl);

    CascadingMetaData getCascadingMetaData();
}