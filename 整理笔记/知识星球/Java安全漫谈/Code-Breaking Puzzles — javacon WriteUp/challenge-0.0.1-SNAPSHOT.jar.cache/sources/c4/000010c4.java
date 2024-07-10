package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/IntArrayValueExtractor.class */
class IntArrayValueExtractor implements ValueExtractor<int[]> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new IntArrayValueExtractor());

    private IntArrayValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(int[] originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.length; i++) {
            receiver.indexedValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, i, Integer.valueOf(originalValue[i]));
        }
    }
}