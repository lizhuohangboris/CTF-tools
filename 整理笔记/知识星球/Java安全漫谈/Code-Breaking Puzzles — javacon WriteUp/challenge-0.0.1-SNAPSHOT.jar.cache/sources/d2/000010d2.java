package org.hibernate.validator.internal.engine.valueextraction;

import java.util.Optional;
import javax.validation.valueextraction.ValueExtractor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/OptionalValueExtractor.class */
class OptionalValueExtractor implements ValueExtractor<Optional<?>> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new OptionalValueExtractor());

    private OptionalValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(Optional<?> originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.isPresent() ? originalValue.get() : null);
    }
}