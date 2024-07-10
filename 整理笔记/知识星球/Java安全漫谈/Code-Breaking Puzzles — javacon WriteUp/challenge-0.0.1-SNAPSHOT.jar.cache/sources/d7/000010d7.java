package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Iterator;
import javafx.beans.property.SetProperty;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.hibernate.validator.internal.engine.path.NodeImpl;

@IgnoreForbiddenApisErrors(reason = "Usage of JavaFX classes")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/SetPropertyValueExtractor.class */
class SetPropertyValueExtractor implements ValueExtractor<SetProperty<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new SetPropertyValueExtractor());

    private SetPropertyValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(SetProperty<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        Iterator it = originalValue.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            receiver.iterableValue(NodeImpl.ITERABLE_ELEMENT_NODE_NAME, object);
        }
    }
}