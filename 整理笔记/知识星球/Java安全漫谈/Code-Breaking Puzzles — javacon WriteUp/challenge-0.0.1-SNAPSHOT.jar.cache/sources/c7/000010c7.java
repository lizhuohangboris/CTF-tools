package org.hibernate.validator.internal.engine.valueextraction;

import java.util.List;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ListValueExtractor.class */
class ListValueExtractor implements ValueExtractor<List<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ListValueExtractor());

    private ListValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(List<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.size(); i++) {
            receiver.indexedValue(NodeImpl.LIST_ELEMENT_NODE_NAME, i, originalValue.get(i));
        }
    }
}