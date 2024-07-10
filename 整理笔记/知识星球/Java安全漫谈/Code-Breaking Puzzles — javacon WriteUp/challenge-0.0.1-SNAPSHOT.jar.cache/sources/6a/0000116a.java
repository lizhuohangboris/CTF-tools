package org.hibernate.validator.internal.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorDescriptor;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/TypeHelper.class */
public final class TypeHelper {
    private static final Map<Class<?>, Set<Class<?>>> SUBTYPES_BY_PRIMITIVE;
    private static final int CONSTRAINT_TYPE_INDEX = 0;
    private static final int VALIDATOR_TYPE_INDEX = 1;
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    static {
        Map<Class<?>, Set<Class<?>>> subtypesByPrimitive = CollectionHelper.newHashMap();
        putPrimitiveSubtypes(subtypesByPrimitive, Void.TYPE, new Class[0]);
        putPrimitiveSubtypes(subtypesByPrimitive, Boolean.TYPE, new Class[0]);
        putPrimitiveSubtypes(subtypesByPrimitive, Byte.TYPE, new Class[0]);
        putPrimitiveSubtypes(subtypesByPrimitive, Character.TYPE, new Class[0]);
        putPrimitiveSubtypes(subtypesByPrimitive, Short.TYPE, Byte.TYPE);
        putPrimitiveSubtypes(subtypesByPrimitive, Integer.TYPE, Character.TYPE, Short.TYPE);
        putPrimitiveSubtypes(subtypesByPrimitive, Long.TYPE, Integer.TYPE);
        putPrimitiveSubtypes(subtypesByPrimitive, Float.TYPE, Long.TYPE);
        putPrimitiveSubtypes(subtypesByPrimitive, Double.TYPE, Float.TYPE);
        SUBTYPES_BY_PRIMITIVE = Collections.unmodifiableMap(subtypesByPrimitive);
    }

    private TypeHelper() {
        throw new AssertionError();
    }

    public static boolean isAssignable(Type supertype, Type type) {
        Contracts.assertNotNull(supertype, "supertype");
        Contracts.assertNotNull(type, "type");
        if (supertype.equals(type)) {
            return true;
        }
        if (supertype instanceof Class) {
            if (type instanceof Class) {
                return isClassAssignable((Class) supertype, (Class) type);
            }
            if (type instanceof ParameterizedType) {
                return isAssignable(supertype, ((ParameterizedType) type).getRawType());
            }
            if (type instanceof TypeVariable) {
                return isTypeVariableAssignable(supertype, (TypeVariable) type);
            }
            if (type instanceof GenericArrayType) {
                if (((Class) supertype).isArray()) {
                    return isAssignable(getComponentType(supertype), getComponentType(type));
                }
                return isArraySupertype((Class) supertype);
            } else if (type instanceof WildcardType) {
                return isClassAssignableToWildcardType((Class) supertype, (WildcardType) type);
            } else {
                return false;
            }
        } else if (supertype instanceof ParameterizedType) {
            if (type instanceof Class) {
                return isSuperAssignable(supertype, type);
            }
            if (type instanceof ParameterizedType) {
                return isParameterizedTypeAssignable((ParameterizedType) supertype, (ParameterizedType) type);
            }
            return false;
        } else if (type instanceof TypeVariable) {
            return isTypeVariableAssignable(supertype, (TypeVariable) type);
        } else {
            if (supertype instanceof GenericArrayType) {
                if (isArray(type)) {
                    return isAssignable(getComponentType(supertype), getComponentType(type));
                }
                return false;
            } else if (supertype instanceof WildcardType) {
                return isWildcardTypeAssignable((WildcardType) supertype, type);
            } else {
                return false;
            }
        }
    }

    public static Type getErasedType(Type type) {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return getErasedType(rawType);
        } else if (isArray(type)) {
            Type componentType = getComponentType(type);
            Type erasedComponentType = getErasedType(componentType);
            return getArrayType(erasedComponentType);
        } else if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable) type).getBounds();
            return getErasedType(bounds[0]);
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return getErasedType(upperBounds[0]);
        } else {
            return type;
        }
    }

    public static Class<?> getErasedReferenceType(Type type) {
        Contracts.assertTrue(isReferenceType(type), "type is not a reference type: %s", type);
        return (Class) getErasedType(type);
    }

    public static boolean isArray(Type type) {
        return ((type instanceof Class) && ((Class) type).isArray()) || (type instanceof GenericArrayType);
    }

    public static Type getComponentType(Type type) {
        if (type instanceof Class) {
            Class<?> klass = (Class) type;
            if (klass.isArray()) {
                return klass.getComponentType();
            }
            return null;
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        } else {
            return null;
        }
    }

    private static Type getArrayType(Type componentType) {
        Contracts.assertNotNull(componentType, "componentType");
        if (componentType instanceof Class) {
            return Array.newInstance((Class) componentType, 0).getClass();
        }
        return genericArrayType(componentType);
    }

    public static GenericArrayType genericArrayType(final Type componentType) {
        return new GenericArrayType() { // from class: org.hibernate.validator.internal.util.TypeHelper.1
            @Override // java.lang.reflect.GenericArrayType
            public Type getGenericComponentType() {
                return componentType;
            }
        };
    }

    public static boolean isInstance(Type type, Object object) {
        return getErasedReferenceType(type).isInstance(object);
    }

    public static ParameterizedType parameterizedType(final Class<?> rawType, final Type... actualTypeArguments) {
        return new ParameterizedType() { // from class: org.hibernate.validator.internal.util.TypeHelper.2
            @Override // java.lang.reflect.ParameterizedType
            public Type[] getActualTypeArguments() {
                return actualTypeArguments;
            }

            @Override // java.lang.reflect.ParameterizedType
            public Type getRawType() {
                return rawType;
            }

            @Override // java.lang.reflect.ParameterizedType
            public Type getOwnerType() {
                return null;
            }
        };
    }

    private static Type getResolvedSuperclass(Type type) {
        Contracts.assertNotNull(type, "type");
        Class<?> rawType = getErasedReferenceType(type);
        Type supertype = rawType.getGenericSuperclass();
        if (supertype == null) {
            return null;
        }
        return resolveTypeVariables(supertype, type);
    }

    private static Type[] getResolvedInterfaces(Type type) {
        Contracts.assertNotNull(type, "type");
        Class<?> rawType = getErasedReferenceType(type);
        Type[] interfaces = rawType.getGenericInterfaces();
        Type[] resolvedInterfaces = new Type[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            resolvedInterfaces[i] = resolveTypeVariables(interfaces[i], type);
        }
        return resolvedInterfaces;
    }

    public static <A extends Annotation> Map<Type, ConstraintValidatorDescriptor<A>> getValidatorTypes(Class<A> annotationType, List<ConstraintValidatorDescriptor<A>> validators) {
        Map<Type, ConstraintValidatorDescriptor<A>> validatorsTypes = CollectionHelper.newHashMap();
        for (ConstraintValidatorDescriptor<A> validator : validators) {
            Type type = validator.getValidatedType();
            ConstraintValidatorDescriptor<A> previous = validatorsTypes.put(type, validator);
            if (previous != null) {
                throw LOG.getMultipleValidatorsForSameTypeException(annotationType, type, previous.getValidatorClass(), validator.getValidatorClass());
            }
        }
        return validatorsTypes;
    }

    public static Type extractValidatedType(Class<? extends ConstraintValidator<?, ?>> validator) {
        return extractConstraintValidatorTypeArgumentType(validator, 1);
    }

    public static Type extractConstraintType(Class<? extends ConstraintValidator<?, ?>> validator) {
        return extractConstraintValidatorTypeArgumentType(validator, 0);
    }

    public static Type extractConstraintValidatorTypeArgumentType(Class<? extends ConstraintValidator<?, ?>> validator, int typeArgumentIndex) {
        Map<Type, Type> resolvedTypes = new HashMap<>();
        Type constraintValidatorType = resolveTypes(resolvedTypes, validator);
        Type type = ((ParameterizedType) constraintValidatorType).getActualTypeArguments()[typeArgumentIndex];
        if (type == null) {
            throw LOG.getNullIsAnInvalidTypeForAConstraintValidatorException();
        }
        if (type instanceof GenericArrayType) {
            type = getArrayType(getComponentType(type));
        }
        while (resolvedTypes.containsKey(type)) {
            type = resolvedTypes.get(type);
        }
        return type;
    }

    public static boolean isUnboundWildcard(Type type) {
        if (!(type instanceof WildcardType)) {
            return false;
        }
        WildcardType wildcardType = (WildcardType) type;
        return isEmptyBounds(wildcardType.getUpperBounds()) && isEmptyBounds(wildcardType.getLowerBounds());
    }

    private static Type resolveTypes(Map<Type, Type> resolvedTypes, Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            Type returnedType = resolveTypeForClassAndHierarchy(resolvedTypes, clazz);
            if (returnedType != null) {
                return returnedType;
            }
            return null;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            if (!(paramType.getRawType() instanceof Class)) {
                return null;
            }
            Class<?> rawType = (Class) paramType.getRawType();
            TypeVariable<?>[] originalTypes = rawType.getTypeParameters();
            Type[] partiallyResolvedTypes = paramType.getActualTypeArguments();
            int nbrOfParams = originalTypes.length;
            for (int i = 0; i < nbrOfParams; i++) {
                resolvedTypes.put(originalTypes[i], partiallyResolvedTypes[i]);
            }
            if (rawType.equals(ConstraintValidator.class)) {
                return type;
            }
            Type returnedType2 = resolveTypeForClassAndHierarchy(resolvedTypes, rawType);
            if (returnedType2 != null) {
                return returnedType2;
            }
            return null;
        } else {
            return null;
        }
    }

    private static Type resolveTypeForClassAndHierarchy(Map<Type, Type> resolvedTypes, Class<?> clazz) {
        Type[] genericInterfaces;
        Type returnedType = resolveTypes(resolvedTypes, clazz.getGenericSuperclass());
        if (returnedType != null) {
            return returnedType;
        }
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            Type returnedType2 = resolveTypes(resolvedTypes, genericInterface);
            if (returnedType2 != null) {
                return returnedType2;
            }
        }
        return null;
    }

    private static void putPrimitiveSubtypes(Map<Class<?>, Set<Class<?>>> subtypesByPrimitive, Class<?> primitiveType, Class<?>... directSubtypes) {
        Set<Class<?>> subtypes = CollectionHelper.newHashSet();
        for (Class<?> directSubtype : directSubtypes) {
            subtypes.add(directSubtype);
            subtypes.addAll(subtypesByPrimitive.get(directSubtype));
        }
        subtypesByPrimitive.put(primitiveType, Collections.unmodifiableSet(subtypes));
    }

    private static boolean isClassAssignable(Class<?> supertype, Class<?> type) {
        if (supertype.isPrimitive() && type.isPrimitive()) {
            return SUBTYPES_BY_PRIMITIVE.get(supertype).contains(type);
        }
        return supertype.isAssignableFrom(type);
    }

    private static boolean isClassAssignableToWildcardType(Class<?> supertype, WildcardType type) {
        Type[] upperBounds;
        for (Type upperBound : type.getUpperBounds()) {
            if (!isAssignable(supertype, upperBound)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isParameterizedTypeAssignable(ParameterizedType supertype, ParameterizedType type) {
        Type rawSupertype = supertype.getRawType();
        Type rawType = type.getRawType();
        if (!rawSupertype.equals(rawType)) {
            if ((rawSupertype instanceof Class) && (rawType instanceof Class) && !((Class) rawSupertype).isAssignableFrom((Class) rawType)) {
                return false;
            }
            return isSuperAssignable(supertype, type);
        }
        Type[] supertypeArgs = supertype.getActualTypeArguments();
        Type[] typeArgs = type.getActualTypeArguments();
        if (supertypeArgs.length != typeArgs.length) {
            return false;
        }
        for (int i = 0; i < supertypeArgs.length; i++) {
            Type supertypeArg = supertypeArgs[i];
            Type typeArg = typeArgs[i];
            if (supertypeArg instanceof WildcardType) {
                if (!isWildcardTypeAssignable((WildcardType) supertypeArg, typeArg)) {
                    return false;
                }
            } else if (!supertypeArg.equals(typeArg)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTypeVariableAssignable(Type supertype, TypeVariable<?> type) {
        Type[] bounds;
        for (Type bound : type.getBounds()) {
            if (isAssignable(supertype, bound)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWildcardTypeAssignable(WildcardType supertype, Type type) {
        Type[] upperBounds;
        Type[] lowerBounds;
        for (Type upperBound : supertype.getUpperBounds()) {
            if (!isAssignable(upperBound, type)) {
                return false;
            }
        }
        for (Type lowerBound : supertype.getLowerBounds()) {
            if (!isAssignable(type, lowerBound)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSuperAssignable(Type supertype, Type type) {
        Type[] resolvedInterfaces;
        Type superclass = getResolvedSuperclass(type);
        if (superclass != null && isAssignable(supertype, superclass)) {
            return true;
        }
        for (Type interphace : getResolvedInterfaces(type)) {
            if (isAssignable(supertype, interphace)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isReferenceType(Type type) {
        return type == null || (type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof TypeVariable) || (type instanceof GenericArrayType);
    }

    private static boolean isArraySupertype(Class<?> type) {
        return Object.class.equals(type) || Cloneable.class.equals(type) || Serializable.class.equals(type);
    }

    private static Type resolveTypeVariables(Type type, Type subtype) {
        if (!(type instanceof ParameterizedType)) {
            return type;
        }
        Map<Type, Type> actualTypeArgumentsByParameter = getActualTypeArgumentsByParameter(type, subtype);
        Class<?> rawType = getErasedReferenceType(type);
        return parameterizeClass(rawType, actualTypeArgumentsByParameter);
    }

    private static Map<Type, Type> getActualTypeArgumentsByParameter(Type... types) {
        Map<Type, Type> actualTypeArgumentsByParameter = new LinkedHashMap<>();
        for (Type type : types) {
            actualTypeArgumentsByParameter.putAll(getActualTypeArgumentsByParameterInternal(type));
        }
        return normalize(actualTypeArgumentsByParameter);
    }

    private static Map<Type, Type> getActualTypeArgumentsByParameterInternal(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return Collections.emptyMap();
        }
        Type[] typeParameters = getErasedReferenceType(type).getTypeParameters();
        Object[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
        if (typeParameters.length != typeArguments.length) {
            throw new MalformedParameterizedTypeException();
        }
        Map<Type, Type> actualTypeArgumentsByParameter = new LinkedHashMap<>();
        for (int i = 0; i < typeParameters.length; i++) {
            if (!typeParameters[i].equals(typeArguments[i])) {
                actualTypeArgumentsByParameter.put(typeParameters[i], typeArguments[i]);
            }
        }
        return actualTypeArgumentsByParameter;
    }

    private static ParameterizedType parameterizeClass(Class<?> type, Map<Type, Type> actualTypeArgumentsByParameter) {
        return parameterizeClassCapture(type, actualTypeArgumentsByParameter);
    }

    private static <T> ParameterizedType parameterizeClassCapture(Class<T> type, Map<Type, Type> actualTypeArgumentsByParameter) {
        TypeVariable<Class<T>>[] typeParameters = type.getTypeParameters();
        Type[] actualTypeArguments = new Type[typeParameters.length];
        for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<Class<T>> typeParameter = typeParameters[i];
            Type actualTypeArgument = actualTypeArgumentsByParameter.get(typeParameter);
            if (actualTypeArgument == null) {
                throw LOG.getMissingActualTypeArgumentForTypeParameterException(typeParameter);
            }
            actualTypeArguments[i] = actualTypeArgument;
        }
        return parameterizedType(getErasedReferenceType(type), actualTypeArguments);
    }

    private static <K, V> Map<K, V> normalize(Map<K, V> map) {
        V value;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value2 = entry.getValue();
            while (true) {
                value = value2;
                if (map.containsKey(value)) {
                    value2 = map.get(value);
                }
            }
            map.put(key, value);
        }
        return map;
    }

    private static boolean isEmptyBounds(Type[] bounds) {
        return bounds == null || bounds.length == 0 || (bounds.length == 1 && Object.class.equals(bounds[0]));
    }
}