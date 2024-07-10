package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Map;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.path.NodeImpl;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/MapKeyExtractor.class */
public class MapKeyExtractor implements ValueExtractor<Map<?, ?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new MapKeyExtractor());

    private MapKeyExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(Map<?, ?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Map.Entry<?, ?> entry : originalValue.entrySet()) {
            receiver.keyedValue(NodeImpl.MAP_KEY_NODE_NAME, entry.getKey(), entry.getKey());
        }
    }
}