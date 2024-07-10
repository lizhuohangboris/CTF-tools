package org.hibernate.validator.internal.engine.valueextraction;

import javafx.beans.value.ObservableValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;

@UnwrapByDefault
@IgnoreForbiddenApisErrors(reason = "Usage of JavaFX classes")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ObservableValueValueExtractor.class */
class ObservableValueValueExtractor implements ValueExtractor<ObservableValue<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new ObservableValueValueExtractor());

    private ObservableValueValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(ObservableValue<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.getValue());
    }
}