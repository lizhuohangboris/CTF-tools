package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Map;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/MapValueExtractor.class */
class MapValueExtractor implements ValueExtractor<Map<?, ?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new MapValueExtractor());

    private MapValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(Map<?, ?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Map.Entry<?, ?> entry : originalValue.entrySet()) {
            receiver.keyedValue(NodeImpl.MAP_VALUE_NODE_NAME, entry.getKey(), entry.getValue());
        }
    }
}