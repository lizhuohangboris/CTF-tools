package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/DoubleArrayValueExtractor.class */
class DoubleArrayValueExtractor implements ValueExtractor<double[]> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new DoubleArrayValueExtractor());

    private DoubleArrayValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(double[] originalValue, ValueExtractor.ValueReceiver receiver) {
        for (int i = 0; i < originalValue.length; i++) {
            receiver.indexedValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, i, Double.valueOf(originalValue[i]));
        }
    }
}