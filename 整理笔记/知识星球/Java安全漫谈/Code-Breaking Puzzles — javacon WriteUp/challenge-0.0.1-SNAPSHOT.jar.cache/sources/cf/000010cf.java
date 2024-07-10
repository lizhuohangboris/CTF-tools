package org.hibernate.validator.internal.engine.valueextraction;

import java.util.OptionalDouble;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

@UnwrapByDefault
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/OptionalDoubleValueExtractor.class */
class OptionalDoubleValueExtractor implements ValueExtractor<OptionalDouble> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new OptionalDoubleValueExtractor());

    OptionalDoubleValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(OptionalDouble originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.isPresent() ? Double.valueOf(originalValue.getAsDouble()) : null);
    }
}