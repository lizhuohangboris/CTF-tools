package org.hibernate.validator.internal.engine.valueextraction;

import javafx.beans.property.ReadOnlyListProperty;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.hibernate.validator.internal.engine.path.NodeImpl;

@IgnoreForbiddenApisErrors(reason = "Usage of JavaFX classes")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ReadOnlyListPropertyValueExtractor.class */
class ReadOnlyListPropertyValueExtractor implements ValueExtractor<ReadOnlyListProperty<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ReadOnlyListPropertyValueExtractor());

    private ReadOnlyListPropertyValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(ReadOnlyListProperty<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.size(); i++) {
            receiver.indexedValue(NodeImpl.LIST_ELEMENT_NODE_NAME, i, originalValue.get(i));
        }
    }
}