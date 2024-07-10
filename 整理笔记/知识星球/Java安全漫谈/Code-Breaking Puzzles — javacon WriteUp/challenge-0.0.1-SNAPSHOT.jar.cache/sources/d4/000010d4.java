package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Map;
import javafx.beans.property.ReadOnlyMapProperty;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.hibernate.validator.internal.engine.path.NodeImpl;

@IgnoreForbiddenApisErrors(reason = "Usage of JavaFX classes")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ReadOnlyMapPropertyKeyExtractor.class */
class ReadOnlyMapPropertyKeyExtractor implements ValueExtractor<ReadOnlyMapProperty<?, ?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ReadOnlyMapPropertyKeyExtractor());

    private ReadOnlyMapPropertyKeyExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(ReadOnlyMapProperty<?, ?> originalValue, ValueExtractor.ValueReceiver receiver) {
        for (Map.Entry<?, ?> entry : originalValue.entrySet()) {
            receiver.keyedValue(NodeImpl.MAP_KEY_NODE_NAME, entry.getKey(), entry.getKey());
        }
    }
}