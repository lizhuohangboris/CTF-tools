package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/type/TypeFactory.class */
public final class TypeFactory implements Serializable {
    private static final long serialVersionUID = 1;
    private static final JavaType[] NO_TYPES = new JavaType[0];
    protected static final TypeFactory instance = new TypeFactory();
    protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
    private static final Class<?> CLS_STRING = String.class;
    private static final Class<?> CLS_OBJECT = Object.class;
    private static final Class<?> CLS_COMPARABLE = Comparable.class;
    private static final Class<?> CLS_CLASS = Class.class;
    private static final Class<?> CLS_ENUM = Enum.class;
    private static final Class<?> CLS_BOOL = Boolean.TYPE;
    private static final Class<?> CLS_INT = Integer.TYPE;
    private static final Class<?> CLS_LONG = Long.TYPE;
    protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
    protected static final SimpleType CORE_TYPE_INT = new SimpleType(CLS_INT);
    protected static final SimpleType CORE_TYPE_LONG = new SimpleType(CLS_LONG);
    protected static final SimpleType CORE_TYPE_STRING = new SimpleType(CLS_STRING);
    protected static final SimpleType CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
    protected static final SimpleType CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
    protected static final SimpleType CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
    protected static final SimpleType CORE_TYPE_CLASS = new SimpleType(CLS_CLASS);
    protected final LRUMap<Object, JavaType> _typeCache;
    protected final TypeModifier[] _modifiers;
    protected final TypeParser _parser;
    protected final ClassLoader _classLoader;

    private TypeFactory() {
        this(null);
    }

    protected TypeFactory(LRUMap<Object, JavaType> typeCache) {
        this._typeCache = typeCache == null ? new LRUMap<>(16, 200) : typeCache;
        this._parser = new TypeParser(this);
        this._modifiers = null;
        this._classLoader = null;
    }

    protected TypeFactory(LRUMap<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
        this._typeCache = typeCache == null ? new LRUMap<>(16, 200) : typeCache;
        this._parser = p.withFactory(this);
        this._modifiers = mods;
        this._classLoader = classLoader;
    }

    public TypeFactory withModifier(TypeModifier mod) {
        TypeModifier[] mods;
        LRUMap<Object, JavaType> typeCache = this._typeCache;
        if (mod == null) {
            mods = null;
            typeCache = null;
        } else {
            mods = this._modifiers == null ? new TypeModifier[]{mod} : (TypeModifier[]) ArrayBuilders.insertInListNoDup(this._modifiers, mod);
        }
        return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
    }

    public TypeFactory withClassLoader(ClassLoader classLoader) {
        return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
    }

    public TypeFactory withCache(LRUMap<Object, JavaType> cache) {
        return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
    }

    public static TypeFactory defaultInstance() {
        return instance;
    }

    public void clearCache() {
        this._typeCache.clear();
    }

    public ClassLoader getClassLoader() {
        return this._classLoader;
    }

    public static JavaType unknownType() {
        return defaultInstance()._unknownType();
    }

    public static Class<?> rawClass(Type t) {
        if (t instanceof Class) {
            return (Class) t;
        }
        return defaultInstance().constructType(t).getRawClass();
    }

    public Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> prim;
        if (className.indexOf(46) < 0 && (prim = _findPrimitive(className)) != null) {
            return prim;
        }
        Throwable prob = null;
        ClassLoader loader = getClassLoader();
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        if (loader != null) {
            try {
                return classForName(className, true, loader);
            } catch (Exception e) {
                prob = ClassUtil.getRootCause(e);
            }
        }
        try {
            return classForName(className);
        } catch (Exception e2) {
            if (prob == null) {
                prob = ClassUtil.getRootCause(e2);
            }
            ClassUtil.throwIfRTE(prob);
            throw new ClassNotFoundException(prob.getMessage(), prob);
        }
    }

    protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
        return Class.forName(name, true, loader);
    }

    protected Class<?> classForName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    protected Class<?> _findPrimitive(String className) {
        if ("int".equals(className)) {
            return Integer.TYPE;
        }
        if ("long".equals(className)) {
            return Long.TYPE;
        }
        if ("float".equals(className)) {
            return Float.TYPE;
        }
        if ("double".equals(className)) {
            return Double.TYPE;
        }
        if ("boolean".equals(className)) {
            return Boolean.TYPE;
        }
        if ("byte".equals(className)) {
            return Byte.TYPE;
        }
        if ("char".equals(className)) {
            return Character.TYPE;
        }
        if ("short".equals(className)) {
            return Short.TYPE;
        }
        if ("void".equals(className)) {
            return Void.TYPE;
        }
        return null;
    }

    public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) {
        JavaType newType;
        Class<?> rawBase = baseType.getRawClass();
        if (rawBase == subclass) {
            return baseType;
        }
        if (rawBase == Object.class) {
            newType = _fromClass(null, subclass, EMPTY_BINDINGS);
        } else if (!rawBase.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException(String.format("Class %s not subtype of %s", subclass.getName(), baseType));
        } else {
            if (baseType.getBindings().isEmpty()) {
                newType = _fromClass(null, subclass, EMPTY_BINDINGS);
            } else {
                if (baseType.isContainerType()) {
                    if (baseType.isMapLikeType()) {
                        if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
                            newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
                        }
                    } else if (baseType.isCollectionLikeType()) {
                        if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
                            newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
                        } else if (rawBase == EnumSet.class) {
                            return baseType;
                        }
                    }
                }
                int typeParamCount = subclass.getTypeParameters().length;
                if (typeParamCount == 0) {
                    newType = _fromClass(null, subclass, EMPTY_BINDINGS);
                } else {
                    TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
                    newType = _fromClass(null, subclass, tb);
                }
            }
        }
        return newType.withHandlersFrom(baseType);
    }

    private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass) {
        PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];
        for (int i = 0; i < typeParamCount; i++) {
            placeholders[i] = new PlaceholderForType(i);
        }
        TypeBindings b = TypeBindings.create(subclass, placeholders);
        JavaType tmpSub = _fromClass(null, subclass, b);
        JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
        if (baseWithPlaceholders == null) {
            throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", baseType.getRawClass().getName(), subclass.getName()));
        }
        String error = _resolveTypePlaceholders(baseType, baseWithPlaceholders);
        if (error != null) {
            throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass.getName() + ", problem: " + error);
        }
        JavaType[] typeParams = new JavaType[typeParamCount];
        for (int i2 = 0; i2 < typeParamCount; i2++) {
            JavaType t = placeholders[i2].actualType();
            if (t == null) {
                t = unknownType();
            }
            typeParams[i2] = t;
        }
        return TypeBindings.create(subclass, typeParams);
    }

    private String _resolveTypePlaceholders(JavaType sourceType, JavaType actualType) throws IllegalArgumentException {
        List<JavaType> expectedTypes = sourceType.getBindings().getTypeParameters();
        List<JavaType> actualTypes = actualType.getBindings().getTypeParameters();
        int len = expectedTypes.size();
        for (int i = 0; i < len; i++) {
            JavaType exp = expectedTypes.get(i);
            JavaType act = actualTypes.get(i);
            if (!_verifyAndResolvePlaceholders(exp, act) && !exp.hasRawClass(Object.class) && (i != 0 || !sourceType.hasRawClass(Map.class) || !act.hasRawClass(Object.class))) {
                return String.format("Type parameter #%d/%d differs; can not specialize %s with %s", Integer.valueOf(i + 1), Integer.valueOf(len), exp.toCanonical(), act.toCanonical());
            }
        }
        return null;
    }

    private boolean _verifyAndResolvePlaceholders(JavaType exp, JavaType act) {
        if (act instanceof PlaceholderForType) {
            ((PlaceholderForType) act).actualType(exp);
            return true;
        } else if (exp.getRawClass() != act.getRawClass()) {
            return false;
        } else {
            List<JavaType> expectedTypes = exp.getBindings().getTypeParameters();
            List<JavaType> actualTypes = act.getBindings().getTypeParameters();
            int len = expectedTypes.size();
            for (int i = 0; i < len; i++) {
                JavaType exp2 = expectedTypes.get(i);
                JavaType act2 = actualTypes.get(i);
                if (!_verifyAndResolvePlaceholders(exp2, act2)) {
                    return false;
                }
            }
            return true;
        }
    }

    public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
        Class<?> rawBase = baseType.getRawClass();
        if (rawBase == superClass) {
            return baseType;
        }
        JavaType superType = baseType.findSuperType(superClass);
        if (superType == null) {
            if (!superClass.isAssignableFrom(rawBase)) {
                throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", superClass.getName(), baseType));
            }
            throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", superClass.getName(), baseType));
        }
        return superType;
    }

    public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
        return this._parser.parse(canonical);
    }

    public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
        JavaType match = type.findSuperType(expType);
        if (match == null) {
            return NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }

    @Deprecated
    public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
        return findTypeParameters(constructType(clz, bindings), expType);
    }

    @Deprecated
    public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
        return findTypeParameters(constructType(clz), expType);
    }

    public JavaType moreSpecificType(JavaType type1, JavaType type2) {
        if (type1 == null) {
            return type2;
        }
        if (type2 == null) {
            return type1;
        }
        Class<?> raw1 = type1.getRawClass();
        Class<?> raw2 = type2.getRawClass();
        if (raw1 == raw2) {
            return type1;
        }
        if (raw1.isAssignableFrom(raw2)) {
            return type2;
        }
        return type1;
    }

    public JavaType constructType(Type type) {
        return _fromAny(null, type, EMPTY_BINDINGS);
    }

    public JavaType constructType(Type type, TypeBindings bindings) {
        return _fromAny(null, type, bindings);
    }

    public JavaType constructType(TypeReference<?> typeRef) {
        return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
    }

    @Deprecated
    public JavaType constructType(Type type, Class<?> contextClass) {
        JavaType contextType = contextClass == null ? null : constructType(contextClass);
        return constructType(type, contextType);
    }

    @Deprecated
    public JavaType constructType(Type type, JavaType contextType) {
        TypeBindings bindings;
        if (contextType == null) {
            bindings = EMPTY_BINDINGS;
        } else {
            bindings = contextType.getBindings();
            if (type.getClass() != Class.class) {
                while (bindings.isEmpty()) {
                    contextType = contextType.getSuperClass();
                    if (contextType == null) {
                        break;
                    }
                    bindings = contextType.getBindings();
                }
            }
        }
        return _fromAny(null, type, bindings);
    }

    public ArrayType constructArrayType(Class<?> elementType) {
        return ArrayType.construct(_fromAny(null, elementType, null), null);
    }

    public ArrayType constructArrayType(JavaType elementType) {
        return ArrayType.construct(elementType, null);
    }

    public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
    }

    public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
        TypeBindings bindings = TypeBindings.createIfNeeded(collectionClass, elementType);
        CollectionType result = (CollectionType) _fromClass(null, collectionClass, bindings);
        if (bindings.isEmpty() && elementType != null) {
            JavaType t = result.findSuperType(Collection.class);
            JavaType realET = t.getContentType();
            if (!realET.equals(elementType)) {
                throw new IllegalArgumentException(String.format("Non-generic Collection class %s did not resolve to something with element type %s but %s ", ClassUtil.nameOf(collectionClass), elementType, realET));
            }
        }
        return result;
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
        return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
    }

    public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
        JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
        if (type instanceof CollectionLikeType) {
            return (CollectionLikeType) type;
        }
        return CollectionLikeType.upgradeFrom(type, elementType);
    }

    public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        JavaType kt;
        JavaType vt;
        if (mapClass == Properties.class) {
            JavaType javaType = CORE_TYPE_STRING;
            vt = javaType;
            kt = javaType;
        } else {
            kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
            vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
        }
        return constructMapType(mapClass, kt, vt);
    }

    public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        TypeBindings bindings = TypeBindings.createIfNeeded(mapClass, new JavaType[]{keyType, valueType});
        MapType result = (MapType) _fromClass(null, mapClass, bindings);
        if (bindings.isEmpty()) {
            JavaType t = result.findSuperType(Map.class);
            JavaType realKT = t.getKeyType();
            if (!realKT.equals(keyType)) {
                throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with key type %s but %s ", ClassUtil.nameOf(mapClass), keyType, realKT));
            }
            JavaType realVT = t.getContentType();
            if (!realVT.equals(valueType)) {
                throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with value type %s but %s ", ClassUtil.nameOf(mapClass), valueType, realVT));
            }
        }
        return result;
    }

    public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
    }

    public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
        JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[]{keyType, valueType}));
        if (type instanceof MapLikeType) {
            return (MapLikeType) type;
        }
        return MapLikeType.upgradeFrom(type, keyType, valueType);
    }

    public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
        return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
    }

    @Deprecated
    public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
        return constructSimpleType(rawType, parameterTypes);
    }

    public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
        return ReferenceType.construct(rawType, null, null, null, referredType);
    }

    @Deprecated
    public JavaType uncheckedSimpleType(Class<?> cls) {
        return _constructSimple(cls, EMPTY_BINDINGS, null, null);
    }

    public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        int len = parameterClasses.length;
        JavaType[] pt = new JavaType[len];
        for (int i = 0; i < len; i++) {
            pt[i] = _fromClass(null, parameterClasses[i], EMPTY_BINDINGS);
        }
        return constructParametricType(parametrized, pt);
    }

    public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
        return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
    }

    @Deprecated
    public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
        return constructParametricType(parametrized, parameterTypes);
    }

    @Deprecated
    public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
        return constructParametricType(parametrized, parameterClasses);
    }

    public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
        return constructCollectionType(collectionClass, unknownType());
    }

    public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
        return constructCollectionLikeType(collectionClass, unknownType());
    }

    public MapType constructRawMapType(Class<? extends Map> mapClass) {
        return constructMapType(mapClass, unknownType(), unknownType());
    }

    public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
        return constructMapLikeType(mapClass, unknownType(), unknownType());
    }

    private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType kt;
        JavaType vt;
        if (rawClass == Properties.class) {
            JavaType javaType = CORE_TYPE_STRING;
            vt = javaType;
            kt = javaType;
        } else {
            List<JavaType> typeParams = bindings.getTypeParameters();
            switch (typeParams.size()) {
                case 0:
                    JavaType _unknownType = _unknownType();
                    vt = _unknownType;
                    kt = _unknownType;
                    break;
                case 2:
                    kt = typeParams.get(0);
                    vt = typeParams.get(1);
                    break;
                default:
                    throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": cannot determine type parameters");
            }
        }
        return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
    }

    private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType ct;
        List<JavaType> typeParams = bindings.getTypeParameters();
        if (typeParams.isEmpty()) {
            ct = _unknownType();
        } else if (typeParams.size() == 1) {
            ct = typeParams.get(0);
        } else {
            throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": cannot determine type parameters");
        }
        return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }

    private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType ct;
        List<JavaType> typeParams = bindings.getTypeParameters();
        if (typeParams.isEmpty()) {
            ct = _unknownType();
        } else if (typeParams.size() == 1) {
            ct = typeParams.get(0);
        } else {
            throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": cannot determine type parameters");
        }
        return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
    }

    protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        JavaType result;
        if (bindings.isEmpty() && (result = _findWellKnownSimple(raw)) != null) {
            return result;
        }
        return _newSimpleType(raw, bindings, superClass, superInterfaces);
    }

    protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        return new SimpleType(raw, bindings, superClass, superInterfaces);
    }

    protected JavaType _unknownType() {
        return CORE_TYPE_OBJECT;
    }

    protected JavaType _findWellKnownSimple(Class<?> clz) {
        if (clz.isPrimitive()) {
            if (clz == CLS_BOOL) {
                return CORE_TYPE_BOOL;
            }
            if (clz == CLS_INT) {
                return CORE_TYPE_INT;
            }
            if (clz == CLS_LONG) {
                return CORE_TYPE_LONG;
            }
            return null;
        } else if (clz == CLS_STRING) {
            return CORE_TYPE_STRING;
        } else {
            if (clz == CLS_OBJECT) {
                return CORE_TYPE_OBJECT;
            }
            return null;
        }
    }

    protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings) {
        JavaType resultType;
        if (type instanceof Class) {
            resultType = _fromClass(context, (Class) type, EMPTY_BINDINGS);
        } else if (type instanceof ParameterizedType) {
            resultType = _fromParamType(context, (ParameterizedType) type, bindings);
        } else if (type instanceof JavaType) {
            return (JavaType) type;
        } else {
            if (type instanceof GenericArrayType) {
                resultType = _fromArrayType(context, (GenericArrayType) type, bindings);
            } else if (type instanceof TypeVariable) {
                resultType = _fromVariable(context, (TypeVariable) type, bindings);
            } else if (type instanceof WildcardType) {
                resultType = _fromWildcard(context, (WildcardType) type, bindings);
            } else {
                throw new IllegalArgumentException("Unrecognized Type: " + (type == null ? "[null]" : type.toString()));
            }
        }
        if (this._modifiers != null) {
            TypeBindings b = resultType.getBindings();
            if (b == null) {
                b = EMPTY_BINDINGS;
            }
            TypeModifier[] arr$ = this._modifiers;
            for (TypeModifier mod : arr$) {
                JavaType t = mod.modifyType(resultType, type, b, this);
                if (t == null) {
                    throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", mod, mod.getClass().getName(), resultType));
                }
                resultType = t;
            }
        }
        return resultType;
    }

    public JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
        Object key;
        ClassStack context2;
        JavaType superClass;
        JavaType[] superInterfaces;
        JavaType result = _findWellKnownSimple(rawType);
        if (result != null) {
            return result;
        }
        if (bindings == null || bindings.isEmpty()) {
            key = rawType;
        } else {
            key = bindings.asKey(rawType);
        }
        JavaType result2 = this._typeCache.get(key);
        if (result2 != null) {
            return result2;
        }
        if (context == null) {
            context2 = new ClassStack(rawType);
        } else {
            ClassStack prev = context.find(rawType);
            if (prev != null) {
                ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
                prev.addSelfReference(selfRef);
                return selfRef;
            }
            context2 = context.child(rawType);
        }
        if (rawType.isArray()) {
            result2 = ArrayType.construct(_fromAny(context2, rawType.getComponentType(), bindings), bindings);
        } else {
            if (rawType.isInterface()) {
                superClass = null;
                superInterfaces = _resolveSuperInterfaces(context2, rawType, bindings);
            } else {
                superClass = _resolveSuperClass(context2, rawType, bindings);
                superInterfaces = _resolveSuperInterfaces(context2, rawType, bindings);
            }
            if (rawType == Properties.class) {
                result2 = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
            } else if (superClass != null) {
                result2 = superClass.refine(rawType, bindings, superClass, superInterfaces);
            }
            if (result2 == null) {
                result2 = _fromWellKnownClass(context2, rawType, bindings, superClass, superInterfaces);
                if (result2 == null) {
                    result2 = _fromWellKnownInterface(context2, rawType, bindings, superClass, superInterfaces);
                    if (result2 == null) {
                        result2 = _newSimpleType(rawType, bindings, superClass, superInterfaces);
                    }
                }
            }
        }
        context2.resolveSelfReferences(result2);
        if (!result2.hasHandlers()) {
            this._typeCache.putIfAbsent(key, result2);
        }
        return result2;
    }

    protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
        Type parent = ClassUtil.getGenericSuperclass(rawType);
        if (parent == null) {
            return null;
        }
        return _fromAny(context, parent, parentBindings);
    }

    protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
        Type[] types = ClassUtil.getGenericInterfaces(rawType);
        if (types == null || types.length == 0) {
            return NO_TYPES;
        }
        int len = types.length;
        JavaType[] resolved = new JavaType[len];
        for (int i = 0; i < len; i++) {
            Type type = types[i];
            resolved[i] = _fromAny(context, type, parentBindings);
        }
        return resolved;
    }

    protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        if (bindings == null) {
            bindings = EMPTY_BINDINGS;
        }
        if (rawType == Map.class) {
            return _mapType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == Collection.class) {
            return _collectionType(rawType, bindings, superClass, superInterfaces);
        }
        if (rawType == AtomicReference.class) {
            return _referenceType(rawType, bindings, superClass, superInterfaces);
        }
        return null;
    }

    protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
        for (JavaType javaType : superInterfaces) {
            JavaType result = javaType.refine(rawType, bindings, superClass, superInterfaces);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
        TypeBindings newBindings;
        Class<?> rawType = (Class) ptype.getRawType();
        if (rawType == CLS_ENUM) {
            return CORE_TYPE_ENUM;
        }
        if (rawType == CLS_COMPARABLE) {
            return CORE_TYPE_COMPARABLE;
        }
        if (rawType == CLS_CLASS) {
            return CORE_TYPE_CLASS;
        }
        Type[] args = ptype.getActualTypeArguments();
        int paramCount = args == null ? 0 : args.length;
        if (paramCount == 0) {
            newBindings = EMPTY_BINDINGS;
        } else {
            JavaType[] pt = new JavaType[paramCount];
            for (int i = 0; i < paramCount; i++) {
                pt[i] = _fromAny(context, args[i], parentBindings);
            }
            newBindings = TypeBindings.create(rawType, pt);
        }
        return _fromClass(context, rawType, newBindings);
    }

    protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
        JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
        return ArrayType.construct(elementType, bindings);
    }

    protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
        String name = var.getName();
        if (bindings == null) {
            throw new Error("No Bindings!");
        }
        JavaType type = bindings.findBoundType(name);
        if (type != null) {
            return type;
        }
        if (bindings.hasUnbound(name)) {
            return CORE_TYPE_OBJECT;
        }
        TypeBindings bindings2 = bindings.withUnboundVariable(name);
        Type[] bounds = var.getBounds();
        return _fromAny(context, bounds[0], bindings2);
    }

    protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
        return _fromAny(context, type.getUpperBounds()[0], bindings);
    }
}