package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/BooleanArrayValueExtractor.class */
class BooleanArrayValueExtractor implements ValueExtractor<boolean[]> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new BooleanArrayValueExtractor());

    private BooleanArrayValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(boolean[] originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.length; i++) {
            receiver.indexedValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, i, Boolean.valueOf(originalValue[i]));
        }
    }
}