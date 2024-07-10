package org.hibernate.validator.internal.engine.valueextraction;

import java.util.OptionalLong;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;

@UnwrapByDefault
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/OptionalLongValueExtractor.class */
class OptionalLongValueExtractor implements ValueExtractor<OptionalLong> {
    static final ValueExtractorDescriptor DESCRIPTOR = new ValueExtractorDescriptor(new OptionalLongValueExtractor());

    OptionalLongValueExtractor() {
    }

    @Override // javax.validation.valueextraction.ValueExtractor
    public void extractValues(OptionalLong originalValue, ValueExtractor.ValueReceiver receiver) {
        receiver.value(null, originalValue.isPresent() ? Long.valueOf(originalValue.getAsLong()) : null);
    }
}