package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorHelper.class */
public class ValueExtractorHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private ValueExtractorHelper() {
    }

    public static Set<Class<? extends ValueExtractor>> toValueExtractorClasses(Set<ValueExtractorDescriptor> valueExtractorDescriptors) {
        return (Set) valueExtractorDescriptors.stream().map(valueExtractorDescriptor -> {
            return valueExtractorDescriptor.getValueExtractor().getClass();
        }).collect(Collectors.toSet());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void extractValues(ValueExtractorDescriptor valueExtractorDescriptor, Object containerValue, ValueExtractor.ValueReceiver valueReceiver) {
        ValueExtractor valueExtractor = valueExtractorDescriptor.getValueExtractor();
        try {
            valueExtractor.extractValues(containerValue, valueReceiver);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw LOG.getErrorWhileExtractingValuesInValueExtractorException(valueExtractor.getClass(), e2);
        }
    }
}