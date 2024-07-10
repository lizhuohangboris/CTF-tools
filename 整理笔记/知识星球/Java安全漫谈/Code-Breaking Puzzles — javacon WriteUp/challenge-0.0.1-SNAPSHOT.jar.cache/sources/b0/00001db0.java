package org.springframework.core;

import ch.qos.logback.classic.spi.CallerData;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import org.springframework.core.SerializableTypeWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType.class */
public class ResolvableType implements Serializable {
    public static final ResolvableType NONE = new ResolvableType(EmptyType.INSTANCE, (SerializableTypeWrapper.TypeProvider) null, (VariableResolver) null, (Integer) 0);
    private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache = new ConcurrentReferenceHashMap<>(256);
    private final Type type;
    @Nullable
    private final SerializableTypeWrapper.TypeProvider typeProvider;
    @Nullable
    private final VariableResolver variableResolver;
    @Nullable
    private final ResolvableType componentType;
    @Nullable
    private final Integer hash;
    @Nullable
    private Class<?> resolved;
    @Nullable
    private volatile ResolvableType superType;
    @Nullable
    private volatile ResolvableType[] interfaces;
    @Nullable
    private volatile ResolvableType[] generics;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$VariableResolver.class */
    public interface VariableResolver extends Serializable {
        Object getSource();

        @Nullable
        ResolvableType resolveVariable(TypeVariable<?> typeVariable);
    }

    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = Integer.valueOf(calculateHashCode());
        this.resolved = null;
    }

    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver, @Nullable Integer hash) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = hash;
        this.resolved = resolveClass();
    }

    private ResolvableType(Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver, @Nullable ResolvableType componentType) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = componentType;
        this.hash = null;
        this.resolved = resolveClass();
    }

    private ResolvableType(@Nullable Class<?> clazz) {
        this.resolved = clazz != null ? clazz : Object.class;
        this.type = this.resolved;
        this.typeProvider = null;
        this.variableResolver = null;
        this.componentType = null;
        this.hash = null;
    }

    public Type getType() {
        return SerializableTypeWrapper.unwrap(this.type);
    }

    @Nullable
    public Class<?> getRawClass() {
        if (this.type == this.resolved) {
            return this.resolved;
        }
        Type rawType = this.type;
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        if (rawType instanceof Class) {
            return (Class) rawType;
        }
        return null;
    }

    public Object getSource() {
        Object source = this.typeProvider != null ? this.typeProvider.getSource() : null;
        return source != null ? source : this.type;
    }

    public Class<?> toClass() {
        return resolve(Object.class);
    }

    public boolean isInstance(@Nullable Object obj) {
        return obj != null && isAssignableFrom(obj.getClass());
    }

    public boolean isAssignableFrom(Class<?> other) {
        return isAssignableFrom(forClass(other), null);
    }

    public boolean isAssignableFrom(ResolvableType other) {
        return isAssignableFrom(other, null);
    }

    private boolean isAssignableFrom(ResolvableType other, @Nullable Map<Type, Type> matchedBefore) {
        ResolvableType resolved;
        ResolvableType resolved2;
        Assert.notNull(other, "ResolvableType must not be null");
        if (this == NONE || other == NONE) {
            return false;
        }
        if (isArray()) {
            return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType());
        } else if (matchedBefore != null && matchedBefore.get(this.type) == other.type) {
            return true;
        } else {
            WildcardBounds ourBounds = WildcardBounds.get(this);
            WildcardBounds typeBounds = WildcardBounds.get(other);
            if (typeBounds != null) {
                return ourBounds != null && ourBounds.isSameKind(typeBounds) && ourBounds.isAssignableFrom(typeBounds.getBounds());
            } else if (ourBounds != null) {
                return ourBounds.isAssignableFrom(other);
            } else {
                boolean exactMatch = matchedBefore != null;
                boolean checkGenerics = true;
                Class<?> ourResolved = null;
                if (this.type instanceof TypeVariable) {
                    TypeVariable<?> variable = (TypeVariable) this.type;
                    if (this.variableResolver != null && (resolved2 = this.variableResolver.resolveVariable(variable)) != null) {
                        ourResolved = resolved2.resolve();
                    }
                    if (ourResolved == null && other.variableResolver != null && (resolved = other.variableResolver.resolveVariable(variable)) != null) {
                        ourResolved = resolved.resolve();
                        checkGenerics = false;
                    }
                    if (ourResolved == null) {
                        exactMatch = false;
                    }
                }
                if (ourResolved == null) {
                    ourResolved = resolve(Object.class);
                }
                Class<?> otherResolved = other.toClass();
                if (exactMatch) {
                    if (!ourResolved.equals(otherResolved)) {
                        return false;
                    }
                } else if (!ClassUtils.isAssignable(ourResolved, otherResolved)) {
                    return false;
                }
                if (checkGenerics) {
                    ResolvableType[] ourGenerics = getGenerics();
                    ResolvableType[] typeGenerics = other.as(ourResolved).getGenerics();
                    if (ourGenerics.length != typeGenerics.length) {
                        return false;
                    }
                    if (matchedBefore == null) {
                        matchedBefore = new IdentityHashMap(1);
                    }
                    matchedBefore.put(this.type, other.type);
                    for (int i = 0; i < ourGenerics.length; i++) {
                        if (!ourGenerics[i].isAssignableFrom(typeGenerics[i], matchedBefore)) {
                            return false;
                        }
                    }
                    return true;
                }
                return true;
            }
        }
    }

    public boolean isArray() {
        if (this == NONE) {
            return false;
        }
        return ((this.type instanceof Class) && ((Class) this.type).isArray()) || (this.type instanceof GenericArrayType) || resolveType().isArray();
    }

    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        if (this.type instanceof Class) {
            Class<?> componentType = ((Class) this.type).getComponentType();
            return forType(componentType, this.variableResolver);
        } else if (this.type instanceof GenericArrayType) {
            return forType(((GenericArrayType) this.type).getGenericComponentType(), this.variableResolver);
        } else {
            return resolveType().getComponentType();
        }
    }

    public ResolvableType asCollection() {
        return as(Collection.class);
    }

    public ResolvableType asMap() {
        return as(Map.class);
    }

    public ResolvableType as(Class<?> type) {
        ResolvableType[] interfaces;
        if (this == NONE) {
            return NONE;
        }
        Class<?> resolved = resolve();
        if (resolved == null || resolved == type) {
            return this;
        }
        for (ResolvableType interfaceType : getInterfaces()) {
            ResolvableType interfaceAsType = interfaceType.as(type);
            if (interfaceAsType != NONE) {
                return interfaceAsType;
            }
        }
        return getSuperType().as(type);
    }

    public ResolvableType getSuperType() {
        Class<?> resolved = resolve();
        if (resolved == null || resolved.getGenericSuperclass() == null) {
            return NONE;
        }
        ResolvableType superType = this.superType;
        if (superType == null) {
            superType = forType(resolved.getGenericSuperclass(), this);
            this.superType = superType;
        }
        return superType;
    }

    public ResolvableType[] getInterfaces() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] interfaces = this.interfaces;
        if (interfaces == null) {
            Type[] genericIfcs = resolved.getGenericInterfaces();
            interfaces = new ResolvableType[genericIfcs.length];
            for (int i = 0; i < genericIfcs.length; i++) {
                interfaces[i] = forType(genericIfcs[i], this);
            }
            this.interfaces = interfaces;
        }
        return interfaces;
    }

    public boolean hasGenerics() {
        return getGenerics().length > 0;
    }

    public boolean isEntirelyUnresolvable() {
        if (this == NONE) {
            return false;
        }
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (!generic.isUnresolvableTypeVariable() && !generic.isWildcardWithoutBounds()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasUnresolvableGenerics() {
        Type[] genericInterfaces;
        if (this == NONE) {
            return false;
        }
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (generic.isUnresolvableTypeVariable() || generic.isWildcardWithoutBounds()) {
                return true;
            }
        }
        Class<?> resolved = resolve();
        if (resolved != null) {
            for (Type genericInterface : resolved.getGenericInterfaces()) {
                if ((genericInterface instanceof Class) && forClass((Class) genericInterface).hasGenerics()) {
                    return true;
                }
            }
            return getSuperType().hasUnresolvableGenerics();
        }
        return false;
    }

    private boolean isUnresolvableTypeVariable() {
        if (this.type instanceof TypeVariable) {
            if (this.variableResolver == null) {
                return true;
            }
            TypeVariable<?> variable = (TypeVariable) this.type;
            ResolvableType resolved = this.variableResolver.resolveVariable(variable);
            if (resolved == null || resolved.isUnresolvableTypeVariable()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isWildcardWithoutBounds() {
        if (this.type instanceof WildcardType) {
            WildcardType wt = (WildcardType) this.type;
            if (wt.getLowerBounds().length == 0) {
                Type[] upperBounds = wt.getUpperBounds();
                if (upperBounds.length != 0) {
                    if (upperBounds.length == 1 && Object.class == upperBounds[0]) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public ResolvableType getNested(int nestingLevel) {
        return getNested(nestingLevel, null);
    }

    public ResolvableType getNested(int nestingLevel, @Nullable Map<Integer, Integer> typeIndexesPerLevel) {
        ResolvableType generic;
        ResolvableType result = this;
        for (int i = 2; i <= nestingLevel; i++) {
            if (result.isArray()) {
                generic = result.getComponentType();
            } else {
                while (result != NONE && !result.hasGenerics()) {
                    result = result.getSuperType();
                }
                Integer index = typeIndexesPerLevel != null ? typeIndexesPerLevel.get(Integer.valueOf(i)) : null;
                generic = result.getGeneric(Integer.valueOf(index == null ? result.getGenerics().length - 1 : index.intValue()).intValue());
            }
            result = generic;
        }
        return result;
    }

    public ResolvableType getGeneric(@Nullable int... indexes) {
        ResolvableType[] generics = getGenerics();
        if (indexes == null || indexes.length == 0) {
            return generics.length == 0 ? NONE : generics[0];
        }
        ResolvableType generic = this;
        for (int index : indexes) {
            ResolvableType[] generics2 = generic.getGenerics();
            if (index < 0 || index >= generics2.length) {
                return NONE;
            }
            generic = generics2[index];
        }
        return generic;
    }

    public ResolvableType[] getGenerics() {
        if (this == NONE) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] generics = this.generics;
        if (generics == null) {
            if (this.type instanceof Class) {
                Type[] typeParams = ((Class) this.type).getTypeParameters();
                generics = new ResolvableType[typeParams.length];
                for (int i = 0; i < generics.length; i++) {
                    generics[i] = forType(typeParams[i], this);
                }
            } else if (this.type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) this.type).getActualTypeArguments();
                generics = new ResolvableType[actualTypeArguments.length];
                for (int i2 = 0; i2 < actualTypeArguments.length; i2++) {
                    generics[i2] = forType(actualTypeArguments[i2], this.variableResolver);
                }
            } else {
                generics = resolveType().getGenerics();
            }
            this.generics = generics;
        }
        return generics;
    }

    public Class<?>[] resolveGenerics() {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve();
        }
        return resolvedGenerics;
    }

    public Class<?>[] resolveGenerics(Class<?> fallback) {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve(fallback);
        }
        return resolvedGenerics;
    }

    @Nullable
    public Class<?> resolveGeneric(int... indexes) {
        return getGeneric(indexes).resolve();
    }

    @Nullable
    public Class<?> resolve() {
        return this.resolved;
    }

    public Class<?> resolve(Class<?> fallback) {
        return this.resolved != null ? this.resolved : fallback;
    }

    @Nullable
    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class) {
            return (Class) this.type;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            if (resolvedComponent != null) {
                return Array.newInstance(resolvedComponent, 0).getClass();
            }
            return null;
        }
        return resolveType().resolve();
    }

    public ResolvableType resolveType() {
        ResolvableType resolved;
        if (this.type instanceof ParameterizedType) {
            return forType(((ParameterizedType) this.type).getRawType(), this.variableResolver);
        }
        if (this.type instanceof WildcardType) {
            Type resolved2 = resolveBounds(((WildcardType) this.type).getUpperBounds());
            if (resolved2 == null) {
                resolved2 = resolveBounds(((WildcardType) this.type).getLowerBounds());
            }
            return forType(resolved2, this.variableResolver);
        } else if (this.type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) this.type;
            if (this.variableResolver != null && (resolved = this.variableResolver.resolveVariable(variable)) != null) {
                return resolved;
            }
            return forType(resolveBounds(variable.getBounds()), this.variableResolver);
        } else {
            return NONE;
        }
    }

    @Nullable
    private Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        return bounds[0];
    }

    @Nullable
    public ResolvableType resolveVariable(TypeVariable<?> variable) {
        if (this.type instanceof TypeVariable) {
            return resolveType().resolveVariable(variable);
        }
        if (this.type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) this.type;
            Class<?> resolved = resolve();
            if (resolved == null) {
                return null;
            }
            TypeVariable<?>[] variables = resolved.getTypeParameters();
            for (int i = 0; i < variables.length; i++) {
                if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
                    Type actualType = parameterizedType.getActualTypeArguments()[i];
                    return forType(actualType, this.variableResolver);
                }
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                return forType(ownerType, this.variableResolver).resolveVariable(variable);
            }
        }
        if (this.variableResolver != null) {
            return this.variableResolver.resolveVariable(variable);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResolvableType)) {
            return false;
        }
        ResolvableType otherType = (ResolvableType) other;
        if (!ObjectUtils.nullSafeEquals(this.type, otherType.type)) {
            return false;
        }
        if (this.typeProvider != otherType.typeProvider && (this.typeProvider == null || otherType.typeProvider == null || !ObjectUtils.nullSafeEquals(this.typeProvider.getType(), otherType.typeProvider.getType()))) {
            return false;
        }
        if ((this.variableResolver != otherType.variableResolver && (this.variableResolver == null || otherType.variableResolver == null || !ObjectUtils.nullSafeEquals(this.variableResolver.getSource(), otherType.variableResolver.getSource()))) || !ObjectUtils.nullSafeEquals(this.componentType, otherType.componentType)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.hash != null ? this.hash.intValue() : calculateHashCode();
    }

    private int calculateHashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.type);
        if (this.typeProvider != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.typeProvider.getType());
        }
        if (this.variableResolver != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.variableResolver.getSource());
        }
        if (this.componentType != null) {
            hashCode = (31 * hashCode) + ObjectUtils.nullSafeHashCode(this.componentType);
        }
        return hashCode;
    }

    @Nullable
    VariableResolver asVariableResolver() {
        if (this == NONE) {
            return null;
        }
        return new DefaultVariableResolver();
    }

    private Object readResolve() {
        return this.type == EmptyType.INSTANCE ? NONE : this;
    }

    public String toString() {
        if (isArray()) {
            return getComponentType() + ClassUtils.ARRAY_SUFFIX;
        }
        if (this.resolved == null) {
            return CallerData.NA;
        }
        if (this.type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable) this.type;
            if (this.variableResolver == null || this.variableResolver.resolveVariable(variable) == null) {
                return CallerData.NA;
            }
        }
        StringBuilder result = new StringBuilder(this.resolved.getName());
        if (hasGenerics()) {
            result.append('<');
            result.append(StringUtils.arrayToDelimitedString(getGenerics(), ", "));
            result.append('>');
        }
        return result.toString();
    }

    public static ResolvableType forClass(@Nullable Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    public static ResolvableType forRawClass(@Nullable final Class<?> clazz) {
        return new ResolvableType(clazz) { // from class: org.springframework.core.ResolvableType.1
            @Override // org.springframework.core.ResolvableType
            public ResolvableType[] getGenerics() {
                return ResolvableType.EMPTY_TYPES_ARRAY;
            }

            @Override // org.springframework.core.ResolvableType
            public boolean isAssignableFrom(Class<?> other) {
                return clazz == null || ClassUtils.isAssignable(clazz, other);
            }

            @Override // org.springframework.core.ResolvableType
            public boolean isAssignableFrom(ResolvableType other) {
                Class<?> otherClass = other.getRawClass();
                return otherClass != null && (clazz == null || ClassUtils.isAssignable(clazz, otherClass));
            }
        };
    }

    public static ResolvableType forClass(Class<?> baseType, Class<?> implementationClass) {
        Assert.notNull(baseType, "Base type must not be null");
        ResolvableType asType = forType(implementationClass).as(baseType);
        return asType == NONE ? forType(baseType) : asType;
    }

    public static ResolvableType forClassWithGenerics(Class<?> clazz, Class<?>... generics) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(generics, "Generics array must not be null");
        ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvableGenerics[i] = forClass(generics[i]);
        }
        return forClassWithGenerics(clazz, resolvableGenerics);
    }

    public static ResolvableType forClassWithGenerics(Class<?> clazz, ResolvableType... generics) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(generics, "Generics array must not be null");
        TypeVariable<Class<?>>[] typeParameters = clazz.getTypeParameters();
        Assert.isTrue(typeParameters.length == generics.length, "Mismatched number of generics specified");
        Type[] arguments = new Type[generics.length];
        for (int i = 0; i < generics.length; i++) {
            ResolvableType generic = generics[i];
            Type argument = generic != null ? generic.getType() : null;
            arguments[i] = (argument == null || (argument instanceof TypeVariable)) ? typeParameters[i] : argument;
        }
        ParameterizedType syntheticType = new SyntheticParameterizedType(clazz, arguments);
        return forType(syntheticType, new TypeVariablesVariableResolver(typeParameters, generics));
    }

    public static ResolvableType forInstance(Object instance) {
        ResolvableType type;
        Assert.notNull(instance, "Instance must not be null");
        if ((instance instanceof ResolvableTypeProvider) && (type = ((ResolvableTypeProvider) instance).getResolvableType()) != null) {
            return type;
        }
        return forClass(instance.getClass());
    }

    public static ResolvableType forField(Field field) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null);
    }

    public static ResolvableType forField(Field field, Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver());
    }

    public static ResolvableType forField(Field field, @Nullable ResolvableType implementationType) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = implementationType != null ? implementationType : NONE;
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.as(field.getDeclaringClass()).asVariableResolver());
    }

    public static ResolvableType forField(Field field, int nestingLevel) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null).getNested(nestingLevel);
    }

    public static ResolvableType forField(Field field, int nestingLevel, @Nullable Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver()).getNested(nestingLevel);
    }

    public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex) {
        Assert.notNull(constructor, "Constructor must not be null");
        return forMethodParameter(new MethodParameter(constructor, parameterIndex));
    }

    public static ResolvableType forConstructorParameter(Constructor<?> constructor, int parameterIndex, Class<?> implementationClass) {
        Assert.notNull(constructor, "Constructor must not be null");
        MethodParameter methodParameter = new MethodParameter(constructor, parameterIndex);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodReturnType(Method method) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(new MethodParameter(method, -1));
    }

    public static ResolvableType forMethodReturnType(Method method, Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        MethodParameter methodParameter = new MethodParameter(method, -1);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(Method method, int parameterIndex) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(new MethodParameter(method, parameterIndex));
    }

    public static ResolvableType forMethodParameter(Method method, int parameterIndex, Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter) {
        return forMethodParameter(methodParameter, (Type) null);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable ResolvableType implementationType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        ResolvableType owner = (implementationType != null ? implementationType : forType(methodParameter.getContainingClass())).as(methodParameter.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
    }

    public static ResolvableType forMethodParameter(MethodParameter methodParameter, @Nullable Type targetType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
        return forType(targetType, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
    }

    public static void resolveMethodParameter(MethodParameter methodParameter) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
        methodParameter.setParameterType(forType(null, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).resolve());
    }

    public static ResolvableType forArrayComponent(ResolvableType componentType) {
        Assert.notNull(componentType, "Component type must not be null");
        Class<?> arrayClass = Array.newInstance(componentType.resolve(), 0).getClass();
        return new ResolvableType(arrayClass, (SerializableTypeWrapper.TypeProvider) null, (VariableResolver) null, componentType);
    }

    public static ResolvableType forType(@Nullable Type type) {
        return forType(type, null, null);
    }

    public static ResolvableType forType(@Nullable Type type, @Nullable ResolvableType owner) {
        VariableResolver variableResolver = null;
        if (owner != null) {
            variableResolver = owner.asVariableResolver();
        }
        return forType(type, variableResolver);
    }

    public static ResolvableType forType(ParameterizedTypeReference<?> typeReference) {
        return forType(typeReference.getType(), null, null);
    }

    public static ResolvableType forType(@Nullable Type type, @Nullable VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }

    static ResolvableType forType(@Nullable Type type, @Nullable SerializableTypeWrapper.TypeProvider typeProvider, @Nullable VariableResolver variableResolver) {
        if (type == null && typeProvider != null) {
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return NONE;
        }
        if (type instanceof Class) {
            return new ResolvableType(type, typeProvider, variableResolver, (ResolvableType) null);
        }
        cache.purgeUnreferencedEntries();
        ResolvableType resultType = new ResolvableType(type, typeProvider, variableResolver);
        ResolvableType cachedType = cache.get(resultType);
        if (cachedType == null) {
            cachedType = new ResolvableType(type, typeProvider, variableResolver, resultType.hash);
            cache.put(cachedType, cachedType);
        }
        resultType.resolved = cachedType.resolved;
        return resultType;
    }

    public static void clearCache() {
        cache.clear();
        SerializableTypeWrapper.cache.clear();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$DefaultVariableResolver.class */
    public class DefaultVariableResolver implements VariableResolver {
        private DefaultVariableResolver() {
            ResolvableType.this = r4;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        @Nullable
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            return ResolvableType.this.resolveVariable(variable);
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        public Object getSource() {
            return ResolvableType.this;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$TypeVariablesVariableResolver.class */
    public static class TypeVariablesVariableResolver implements VariableResolver {
        private final TypeVariable<?>[] variables;
        private final ResolvableType[] generics;

        public TypeVariablesVariableResolver(TypeVariable<?>[] variables, ResolvableType[] generics) {
            this.variables = variables;
            this.generics = generics;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        @Nullable
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            for (int i = 0; i < this.variables.length; i++) {
                TypeVariable<?> v1 = (TypeVariable) SerializableTypeWrapper.unwrap(this.variables[i]);
                TypeVariable<?> v2 = (TypeVariable) SerializableTypeWrapper.unwrap(variable);
                if (ObjectUtils.nullSafeEquals(v1, v2)) {
                    return this.generics[i];
                }
            }
            return null;
        }

        @Override // org.springframework.core.ResolvableType.VariableResolver
        public Object getSource() {
            return this.generics;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$SyntheticParameterizedType.class */
    public static final class SyntheticParameterizedType implements ParameterizedType, Serializable {
        private final Type rawType;
        private final Type[] typeArguments;

        public SyntheticParameterizedType(Type rawType, Type[] typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override // java.lang.reflect.Type
        public String getTypeName() {
            StringBuilder result = new StringBuilder(this.rawType.getTypeName());
            if (this.typeArguments.length > 0) {
                result.append('<');
                for (int i = 0; i < this.typeArguments.length; i++) {
                    if (i > 0) {
                        result.append(", ");
                    }
                    result.append(this.typeArguments[i].getTypeName());
                }
                result.append('>');
            }
            return result.toString();
        }

        @Override // java.lang.reflect.ParameterizedType
        @Nullable
        public Type getOwnerType() {
            return null;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type getRawType() {
            return this.rawType;
        }

        @Override // java.lang.reflect.ParameterizedType
        public Type[] getActualTypeArguments() {
            return this.typeArguments;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType otherType = (ParameterizedType) other;
            return otherType.getOwnerType() == null && this.rawType.equals(otherType.getRawType()) && Arrays.equals(this.typeArguments, otherType.getActualTypeArguments());
        }

        public int hashCode() {
            return (this.rawType.hashCode() * 31) + Arrays.hashCode(this.typeArguments);
        }

        public String toString() {
            return getTypeName();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$WildcardBounds.class */
    public static class WildcardBounds {
        private final Kind kind;
        private final ResolvableType[] bounds;

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$WildcardBounds$Kind.class */
        public enum Kind {
            UPPER,
            LOWER
        }

        public WildcardBounds(Kind kind, ResolvableType[] bounds) {
            this.kind = kind;
            this.bounds = bounds;
        }

        public boolean isSameKind(WildcardBounds bounds) {
            return this.kind == bounds.kind;
        }

        public boolean isAssignableFrom(ResolvableType... types) {
            ResolvableType[] resolvableTypeArr;
            for (ResolvableType bound : this.bounds) {
                for (ResolvableType type : types) {
                    if (!isAssignable(bound, type)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isAssignable(ResolvableType source, ResolvableType from) {
            return this.kind == Kind.UPPER ? source.isAssignableFrom(from) : from.isAssignableFrom(source);
        }

        public ResolvableType[] getBounds() {
            return this.bounds;
        }

        @Nullable
        public static WildcardBounds get(ResolvableType type) {
            ResolvableType resolvableType = type;
            while (true) {
                ResolvableType resolveToWildcard = resolvableType;
                if (resolveToWildcard.getType() instanceof WildcardType) {
                    WildcardType wildcardType = (WildcardType) resolveToWildcard.type;
                    Kind boundsType = wildcardType.getLowerBounds().length > 0 ? Kind.LOWER : Kind.UPPER;
                    Type[] bounds = boundsType == Kind.UPPER ? wildcardType.getUpperBounds() : wildcardType.getLowerBounds();
                    ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
                    for (int i = 0; i < bounds.length; i++) {
                        resolvableBounds[i] = ResolvableType.forType(bounds[i], type.variableResolver);
                    }
                    return new WildcardBounds(boundsType, resolvableBounds);
                } else if (resolveToWildcard == ResolvableType.NONE) {
                    return null;
                } else {
                    resolvableType = resolveToWildcard.resolveType();
                }
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/ResolvableType$EmptyType.class */
    public static class EmptyType implements Type, Serializable {
        static final Type INSTANCE = new EmptyType();

        EmptyType() {
        }

        Object readResolve() {
            return INSTANCE;
        }
    }
}