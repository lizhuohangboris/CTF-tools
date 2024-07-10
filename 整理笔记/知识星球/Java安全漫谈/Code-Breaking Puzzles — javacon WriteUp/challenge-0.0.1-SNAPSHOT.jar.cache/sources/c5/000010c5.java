package org.hibernate.validator.internal.engine.valueextraction;

import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/IterableValueExtractor.class */
class IterableValueExtractor implements ValueExtractor<Iterable<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new IterableValueExtractor());

    private IterableValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(Iterable<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Object object : originalValue) {
            receiver.iterableValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, object);
        }
    }
}