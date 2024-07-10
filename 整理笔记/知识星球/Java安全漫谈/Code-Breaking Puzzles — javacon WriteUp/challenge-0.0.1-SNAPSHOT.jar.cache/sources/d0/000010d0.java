package org.hibernate.validator.internal.engine.valueextraction;

import java.util.OptionalInt;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

@UnwrapByDefault
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/OptionalIntValueExtractor.class */
class OptionalIntValueExtractor implements ValueExtractor<OptionalInt> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new OptionalIntValueExtractor());

    OptionalIntValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(OptionalInt originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.isPresent() ? Integer.valueOf(originalValue.getAsInt()) : null);
    }
}