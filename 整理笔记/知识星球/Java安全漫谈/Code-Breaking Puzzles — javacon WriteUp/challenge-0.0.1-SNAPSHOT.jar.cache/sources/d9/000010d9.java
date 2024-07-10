package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.StringHelper;
import org.hibernate.validator.internal.util.TypeHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorDescriptor.class */
public class ValueExtractorDescriptor {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final Key key;
    private final ValueExtractor<?> valueExtractor;
    private final boolean unwrapByDefault;
    private final Optional<Class<?>> extractedType;

    public ValueExtractorDescriptor(ValueExtractor<?> valueExtractor) {
        AnnotatedParameterizedType valueExtractorDefinition = getValueExtractorDefinition(valueExtractor.getClass());
        this.key = new Key(getContainerType(valueExtractorDefinition, valueExtractor.getClass()), getExtractedTypeParameter(valueExtractorDefinition, valueExtractor.getClass()));
        this.valueExtractor = valueExtractor;
        this.unwrapByDefault = hasUnwrapByDefaultAnnotation(valueExtractor.getClass());
        this.extractedType = getExtractedType(valueExtractorDefinition);
    }

    private static TypeVariable<?> getExtractedTypeParameter(AnnotatedParameterizedType valueExtractorDefinition, Class<? extends ValueExtractor> extractorImplementationType) {
        AnnotatedType[] annotatedActualTypeArguments;
        AnnotatedArrayType annotatedArrayType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        Class<?> containerTypeRaw = (Class) TypeHelper.getErasedType(annotatedArrayType.getType());
        TypeVariable<?> extractedTypeParameter = null;
        if (annotatedArrayType.isAnnotationPresent(ExtractedValue.class)) {
            if (annotatedArrayType instanceof AnnotatedArrayType) {
                extractedTypeParameter = new ArrayElement(annotatedArrayType);
            } else {
                extractedTypeParameter = AnnotatedObject.INSTANCE;
            }
        }
        if (annotatedArrayType instanceof AnnotatedParameterizedType) {
            AnnotatedParameterizedType parameterizedExtractedType = (AnnotatedParameterizedType) annotatedArrayType;
            int i = 0;
            for (AnnotatedType typeArgument : parameterizedExtractedType.getAnnotatedActualTypeArguments()) {
                if (!TypeHelper.isUnboundWildcard(typeArgument.getType())) {
                    throw LOG.getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException(extractorImplementationType);
                }
                if (typeArgument.isAnnotationPresent(ExtractedValue.class)) {
                    if (extractedTypeParameter != null) {
                        throw LOG.getValueExtractorDeclaresExtractedValueMultipleTimesException(extractorImplementationType);
                    }
                    if (!Void.TYPE.equals(((ExtractedValue) typeArgument.getAnnotation(ExtractedValue.class)).type())) {
                        throw LOG.getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException(extractorImplementationType);
                    }
                    extractedTypeParameter = containerTypeRaw.getTypeParameters()[i];
                }
                i++;
            }
        }
        if (extractedTypeParameter == null) {
            throw LOG.getValueExtractorFailsToDeclareExtractedValueException(extractorImplementationType);
        }
        return extractedTypeParameter;
    }

    private static Optional<Class<?>> getExtractedType(AnnotatedParameterizedType valueExtractorDefinition) {
        AnnotatedType containerType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        if (containerType.isAnnotationPresent(ExtractedValue.class)) {
            Class<?> extractedType = ((ExtractedValue) containerType.getAnnotation(ExtractedValue.class)).type();
            if (!Void.TYPE.equals(extractedType)) {
                return Optional.of(ReflectionHelper.boxedType(extractedType));
            }
        }
        return Optional.empty();
    }

    private static Class<?> getContainerType(AnnotatedParameterizedType valueExtractorDefinition, Class<? extends ValueExtractor> extractorImplementationType) {
        AnnotatedType containerType = valueExtractorDefinition.getAnnotatedActualTypeArguments()[0];
        return TypeHelper.getErasedReferenceType(containerType.getType());
    }

    private static AnnotatedParameterizedType getValueExtractorDefinition(Class<?> extractorImplementationType) {
        List<AnnotatedType> valueExtractorAnnotatedTypes = new ArrayList<>();
        determineValueExtractorDefinitions(valueExtractorAnnotatedTypes, extractorImplementationType);
        if (valueExtractorAnnotatedTypes.size() == 1) {
            return (AnnotatedParameterizedType) valueExtractorAnnotatedTypes.get(0);
        }
        if (valueExtractorAnnotatedTypes.size() > 1) {
            throw LOG.getParallelDefinitionsOfValueExtractorsException(extractorImplementationType);
        }
        throw new AssertionError(extractorImplementationType.getName() + " should be a subclass of " + ValueExtractor.class.getSimpleName());
    }

    private static void determineValueExtractorDefinitions(List<AnnotatedType> valueExtractorDefinitions, Class<?> extractorImplementationType) {
        Class<?>[] interfaces;
        AnnotatedType[] annotatedInterfaces;
        if (!ValueExtractor.class.isAssignableFrom(extractorImplementationType)) {
            return;
        }
        Class<?> superClass = extractorImplementationType.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass)) {
            determineValueExtractorDefinitions(valueExtractorDefinitions, superClass);
        }
        for (Class<?> implementedInterface : extractorImplementationType.getInterfaces()) {
            if (!ValueExtractor.class.equals(implementedInterface)) {
                determineValueExtractorDefinitions(valueExtractorDefinitions, implementedInterface);
            }
        }
        for (AnnotatedType annotatedInterface : extractorImplementationType.getAnnotatedInterfaces()) {
            if (ValueExtractor.class.equals(ReflectionHelper.getClassFromType(annotatedInterface.getType()))) {
                valueExtractorDefinitions.add(annotatedInterface);
            }
        }
    }

    private static boolean hasUnwrapByDefaultAnnotation(Class<?> extractorImplementationType) {
        return extractorImplementationType.isAnnotationPresent(UnwrapByDefault.class);
    }

    public Key getKey() {
        return this.key;
    }

    public Class<?> getContainerType() {
        return this.key.containerType;
    }

    public TypeVariable<?> getExtractedTypeParameter() {
        return this.key.extractedTypeParameter;
    }

    public Optional<Class<?>> getExtractedType() {
        return this.extractedType;
    }

    public ValueExtractor<?> getValueExtractor() {
        return this.valueExtractor;
    }

    public boolean isUnwrapByDefault() {
        return this.unwrapByDefault;
    }

    public String toString() {
        return "ValueExtractorDescriptor [key=" + this.key + ", valueExtractor=" + this.valueExtractor + ", unwrapByDefault=" + this.unwrapByDefault + "]";
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/valueextraction/ValueExtractorDescriptor$Key.class */
    public static class Key {
        private final Class<?> containerType;
        private final TypeVariable<?> extractedTypeParameter;
        private final int hashCode;

        public Key(Class<?> containerType, TypeVariable<?> extractedTypeParameter) {
            this.containerType = containerType;
            this.extractedTypeParameter = extractedTypeParameter;
            this.hashCode = buildHashCode(containerType, extractedTypeParameter);
        }

        private static int buildHashCode(Type containerType, TypeVariable<?> extractedTypeParameter) {
            int result = (31 * 1) + containerType.hashCode();
            return (31 * result) + extractedTypeParameter.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            return this.containerType.equals(other.containerType) && this.extractedTypeParameter.equals(other.extractedTypeParameter);
        }

        public String toString() {
            return "Key [containerType=" + StringHelper.toShortString((Type) this.containerType) + ", extractedTypeParameter=" + this.extractedTypeParameter + "]";
        }
    }
}