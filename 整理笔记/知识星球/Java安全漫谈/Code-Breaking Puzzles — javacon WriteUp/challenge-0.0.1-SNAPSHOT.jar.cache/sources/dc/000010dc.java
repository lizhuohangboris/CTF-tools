package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorManager.class */
public class ValueExtractorManager {
    public static final Set<ValueExtractorDescriptor> SPEC_DEFINED_EXTRACTORS;
    private final Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> registeredValueExtractors;
    private final ValueExtractorResolver valueExtractorResolver;

    static {
        LinkedHashSet<ValueExtractorDescriptor> specDefinedExtractors = new LinkedHashSet<>();
        if (isJavaFxInClasspath()) {
            specDefinedExtractors.add(ObservableValueValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(ListPropertyValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(ReadOnlyListPropertyValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(MapPropertyValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(ReadOnlyMapPropertyValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(MapPropertyKeyExtractor.DESCRIPTOR);
            specDefinedExtractors.add(ReadOnlyMapPropertyKeyExtractor.DESCRIPTOR);
            specDefinedExtractors.add(SetPropertyValueExtractor.DESCRIPTOR);
            specDefinedExtractors.add(ReadOnlySetPropertyValueExtractor.DESCRIPTOR);
        }
        specDefinedExtractors.add(ByteArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(ShortArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(IntArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(LongArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(FloatArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(DoubleArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(CharArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(BooleanArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(ObjectArrayValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(ListValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(MapValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(MapKeyExtractor.DESCRIPTOR);
        specDefinedExtractors.add(IterableValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(OptionalValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(OptionalIntValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(OptionalDoubleValueExtractor.DESCRIPTOR);
        specDefinedExtractors.add(OptionalLongValueExtractor.DESCRIPTOR);
        SPEC_DEFINED_EXTRACTORS = Collections.unmodifiableSet(specDefinedExtractors);
    }

    public ValueExtractorManager(Set<ValueExtractor<?>> externalExtractors) {
        LinkedHashMap<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> tmpValueExtractors = new LinkedHashMap<>();
        for (ValueExtractorDescriptor descriptor : SPEC_DEFINED_EXTRACTORS) {
            tmpValueExtractors.put(descriptor.getKey(), descriptor);
        }
        for (ValueExtractor<?> valueExtractor : externalExtractors) {
            ValueExtractorDescriptor descriptor2 = new ValueExtractorDescriptor(valueExtractor);
            tmpValueExtractors.put(descriptor2.getKey(), descriptor2);
        }
        this.registeredValueExtractors = Collections.unmodifiableMap(tmpValueExtractors);
        this.valueExtractorResolver = new ValueExtractorResolver(new HashSet(this.registeredValueExtractors.values()));
    }

    public ValueExtractorManager(ValueExtractorManager template, Map<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> externalValueExtractorDescriptors) {
        LinkedHashMap<ValueExtractorDescriptor.Key, ValueExtractorDescriptor> tmpValueExtractors = new LinkedHashMap<>(template.registeredValueExtractors);
        tmpValueExtractors.putAll(externalValueExtractorDescriptors);
        this.registeredValueExtractors = Collections.unmodifiableMap(tmpValueExtractors);
        this.valueExtractorResolver = new ValueExtractorResolver(new HashSet(this.registeredValueExtractors.values()));
    }

    public static Set<ValueExtractor<?>> getDefaultValueExtractors() {
        return (Set) SPEC_DEFINED_EXTRACTORS.stream().map(d -> {
            return d.getValueExtractor();
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
    }

    public ValueExtractorDescriptor getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(Type declaredType, TypeVariable<?> typeParameter, Class<?> runtimeType, Collection<ValueExtractorDescriptor> valueExtractorCandidates) {
        if (valueExtractorCandidates.size() == 1) {
            return valueExtractorCandidates.iterator().next();
        }
        if (!valueExtractorCandidates.isEmpty()) {
            return this.valueExtractorResolver.getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(declaredType, typeParameter, runtimeType, valueExtractorCandidates);
        }
        return this.valueExtractorResolver.getMaximallySpecificAndRuntimeContainerElementCompliantValueExtractor(declaredType, typeParameter, runtimeType, this.registeredValueExtractors.values());
    }

    public ValueExtractorResolver getResolver() {
        return this.valueExtractorResolver;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.registeredValueExtractors == null ? 0 : this.registeredValueExtractors.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ValueExtractorManager other = (ValueExtractorManager) obj;
        return this.registeredValueExtractors.equals(other.registeredValueExtractors);
    }

    private static boolean isJavaFxInClasspath() {
        return isClassPresent("javafx.beans.value.ObservableValue", false);
    }

    private static boolean isClassPresent(String className, boolean fallbackOnTCCL) {
        try {
            run(LoadClass.action(className, ValueExtractorManager.class.getClassLoader(), fallbackOnTCCL));
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    public void clear() {
        this.valueExtractorResolver.clear();
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}