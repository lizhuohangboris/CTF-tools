package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ShortArrayValueExtractor.class */
class ShortArrayValueExtractor implements ValueExtractor<short[]> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ShortArrayValueExtractor());

    private ShortArrayValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(short[] originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.length; i++) {
            receiver.indexedValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, i, Short.valueOf(originalValue[i]));
        }
    }
}