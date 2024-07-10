package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.annotation.NoClass;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ClassUtil.class */
public final class ClassUtil {
    private static final Class<?> CLS_OBJECT = Object.class;
    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
    private static final Ctor[] NO_CTORS = new Ctor[0];
    private static final Iterator<?> EMPTY_ITERATOR = Collections.emptyIterator();

    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }

    public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself) {
        if (type == null || type.hasRawClass(endBefore) || type.hasRawClass(Object.class)) {
            return Collections.emptyList();
        }
        List<JavaType> result = new ArrayList<>(8);
        _addSuperTypes(type, endBefore, result, addClassItself);
        return result;
    }

    public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
        if (cls == null || cls == endBefore || cls == Object.class) {
            return Collections.emptyList();
        }
        List<Class<?>> result = new ArrayList<>(8);
        _addRawSuperTypes(cls, endBefore, result, addClassItself);
        return result;
    }

    public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself) {
        List<Class<?>> result = new LinkedList<>();
        if (cls != null && cls != endBefore) {
            if (addClassItself) {
                result.add(cls);
            }
            while (true) {
                Class<? super Object> superclass = cls.getSuperclass();
                cls = superclass;
                if (superclass == null || cls == endBefore) {
                    break;
                }
                result.add(cls);
            }
        }
        return result;
    }

    @Deprecated
    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
        return findSuperTypes(cls, endBefore, new ArrayList(8));
    }

    @Deprecated
    public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
        _addRawSuperTypes(cls, endBefore, result, false);
        return result;
    }

    private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself) {
        Class<?> cls;
        if (type == null || (cls = type.getRawClass()) == endBefore || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(type)) {
                return;
            }
            result.add(type);
        }
        for (JavaType intCls : type.getInterfaces()) {
            _addSuperTypes(intCls, endBefore, result, true);
        }
        _addSuperTypes(type.getSuperClass(), endBefore, result, true);
    }

    private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
        if (cls == endBefore || cls == null || cls == Object.class) {
            return;
        }
        if (addClassItself) {
            if (result.contains(cls)) {
                return;
            }
            result.add(cls);
        }
        Class<?>[] arr$ = _interfaces(cls);
        for (Class<?> intCls : arr$) {
            _addRawSuperTypes(intCls, endBefore, result, true);
        }
        _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
    }

    public static String canBeABeanType(Class<?> type) {
        if (type.isAnnotation()) {
            return "annotation";
        }
        if (type.isArray()) {
            return BeanDefinitionParserDelegate.ARRAY_ELEMENT;
        }
        if (type.isEnum()) {
            return "enum";
        }
        if (type.isPrimitive()) {
            return "primitive";
        }
        return null;
    }

    public static String isLocalType(Class<?> type, boolean allowNonStatic) {
        try {
            if (hasEnclosingMethod(type)) {
                return "local/anonymous";
            }
            if (!allowNonStatic && !Modifier.isStatic(type.getModifiers())) {
                if (getEnclosingClass(type) != null) {
                    return "non-static member class";
                }
                return null;
            }
            return null;
        } catch (NullPointerException e) {
            return null;
        } catch (SecurityException e2) {
            return null;
        }
    }

    public static Class<?> getOuterClass(Class<?> type) {
        try {
            if (!hasEnclosingMethod(type) && !Modifier.isStatic(type.getModifiers())) {
                return getEnclosingClass(type);
            }
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

    public static boolean isProxyType(Class<?> type) {
        String name = type.getName();
        if (name.startsWith("net.sf.cglib.proxy.") || name.startsWith("org.hibernate.proxy.")) {
            return true;
        }
        return false;
    }

    public static boolean isConcrete(Class<?> type) {
        int mod = type.getModifiers();
        return (mod & 1536) == 0;
    }

    public static boolean isConcrete(Member member) {
        int mod = member.getModifiers();
        return (mod & 1536) == 0;
    }

    public static boolean isCollectionMapOrArray(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public static boolean isBogusClass(Class<?> cls) {
        return cls == Void.class || cls == Void.TYPE || cls == NoClass.class;
    }

    public static boolean isNonStaticInnerClass(Class<?> cls) {
        return (Modifier.isStatic(cls.getModifiers()) || getEnclosingClass(cls) == null) ? false : true;
    }

    public static boolean isObjectOrPrimitive(Class<?> cls) {
        return cls == CLS_OBJECT || cls.isPrimitive();
    }

    public static boolean hasClass(Object inst, Class<?> raw) {
        return inst != null && inst.getClass() == raw;
    }

    public static void verifyMustOverride(Class<?> expType, Object instance, String method) {
        if (instance.getClass() != expType) {
            throw new IllegalStateException(String.format("Sub-class %s (of class %s) must override method '%s'", instance.getClass().getName(), expType.getName(), method));
        }
    }

    @Deprecated
    public static boolean hasGetterSignature(Method m) {
        if (Modifier.isStatic(m.getModifiers())) {
            return false;
        }
        Class<?>[] pts = m.getParameterTypes();
        if ((pts != null && pts.length != 0) || Void.TYPE == m.getReturnType()) {
            return false;
        }
        return true;
    }

    public static Throwable throwIfError(Throwable t) {
        if (t instanceof Error) {
            throw ((Error) t);
        }
        return t;
    }

    public static Throwable throwIfRTE(Throwable t) {
        if (t instanceof RuntimeException) {
            throw ((RuntimeException) t);
        }
        return t;
    }

    public static Throwable throwIfIOE(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw ((IOException) t);
        }
        return t;
    }

    public static Throwable getRootCause(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static Throwable throwRootCauseIfIOE(Throwable t) throws IOException {
        return throwIfIOE(getRootCause(t));
    }

    public static void throwAsIAE(Throwable t) {
        throwAsIAE(t, t.getMessage());
    }

    public static void throwAsIAE(Throwable t, String msg) {
        throwIfRTE(t);
        throwIfError(t);
        throw new IllegalArgumentException(msg, t);
    }

    public static <T> T throwAsMappingException(DeserializationContext ctxt, IOException e0) throws JsonMappingException {
        if (e0 instanceof JsonMappingException) {
            throw ((JsonMappingException) e0);
        }
        JsonMappingException e = JsonMappingException.from(ctxt, e0.getMessage());
        e.initCause(e0);
        throw e;
    }

    public static void unwrapAndThrowAsIAE(Throwable t) {
        throwAsIAE(getRootCause(t));
    }

    public static void unwrapAndThrowAsIAE(Throwable t, String msg) {
        throwAsIAE(getRootCause(t), msg);
    }

    public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Exception fail) throws IOException {
        g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        try {
            g.close();
        } catch (Exception e) {
            fail.addSuppressed(e);
        }
        throwIfIOE(fail);
        throwIfRTE(fail);
        throw new RuntimeException(fail);
    }

    public static void closeOnFailAndThrowAsIOE(JsonGenerator g, Closeable toClose, Exception fail) throws IOException {
        if (g != null) {
            g.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
            try {
                g.close();
            } catch (Exception e) {
                fail.addSuppressed(e);
            }
        }
        if (toClose != null) {
            try {
                toClose.close();
            } catch (Exception e2) {
                fail.addSuppressed(e2);
            }
        }
        throwIfIOE(fail);
        throwIfRTE(fail);
        throw new RuntimeException(fail);
    }

    public static <T> T createInstance(Class<T> cls, boolean canFixAccess) throws IllegalArgumentException {
        Constructor<T> ctor = findConstructor(cls, canFixAccess);
        if (ctor == null) {
            throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
        }
        try {
            return ctor.newInstance(new Object[0]);
        } catch (Exception e) {
            unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage());
            return null;
        }
    }

    public static <T> Constructor<T> findConstructor(Class<T> cls, boolean forceAccess) throws IllegalArgumentException {
        try {
            Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
            if (forceAccess) {
                checkAndFixAccess(ctor, forceAccess);
            } else if (!Modifier.isPublic(ctor.getModifiers())) {
                throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: cannot instantiate type");
            }
            return ctor;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e2) {
            unwrapAndThrowAsIAE(e2, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e2.getMessage());
            return null;
        }
    }

    public static Class<?> classOf(Object inst) {
        if (inst == null) {
            return null;
        }
        return inst.getClass();
    }

    public static Class<?> rawClass(JavaType t) {
        if (t == null) {
            return null;
        }
        return t.getRawClass();
    }

    public static <T> T nonNull(T valueOrNull, T defaultValue) {
        return valueOrNull == null ? defaultValue : valueOrNull;
    }

    public static String nullOrToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String nonNullString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    public static String quotedOr(Object str, String forNull) {
        if (str == null) {
            return forNull;
        }
        return String.format("\"%s\"", str);
    }

    public static String getClassDescription(Object classOrInstance) {
        if (classOrInstance == null) {
            return "unknown";
        }
        Class<?> cls = classOrInstance instanceof Class ? (Class) classOrInstance : classOrInstance.getClass();
        return nameOf(cls);
    }

    public static String classNameOf(Object inst) {
        if (inst == null) {
            return "[null]";
        }
        return nameOf(inst.getClass());
    }

    public static String nameOf(Class<?> cls) {
        if (cls == null) {
            return "[null]";
        }
        int index = 0;
        while (cls.isArray()) {
            index++;
            cls = cls.getComponentType();
        }
        String base = cls.isPrimitive() ? cls.getSimpleName() : cls.getName();
        if (index > 0) {
            StringBuilder sb = new StringBuilder(base);
            do {
                sb.append(ClassUtils.ARRAY_SUFFIX);
                index--;
            } while (index > 0);
            base = sb.toString();
        }
        return backticked(base);
    }

    public static String nameOf(Named named) {
        if (named == null) {
            return "[null]";
        }
        return backticked(named.getName());
    }

    public static String backticked(String text) {
        if (text == null) {
            return "[null]";
        }
        return new StringBuilder(text.length() + 2).append('`').append(text).append('`').toString();
    }

    public static String exceptionMessage(Throwable t) {
        if (t instanceof JsonProcessingException) {
            return ((JsonProcessingException) t).getOriginalMessage();
        }
        return t.getMessage();
    }

    public static Object defaultValue(Class<?> cls) {
        if (cls == Integer.TYPE) {
            return 0;
        }
        if (cls == Long.TYPE) {
            return 0L;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(0.0d);
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (cls == Byte.TYPE) {
            return (byte) 0;
        }
        if (cls == Short.TYPE) {
            return (short) 0;
        }
        if (cls == Character.TYPE) {
            return (char) 0;
        }
        throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
    }

    public static Class<?> wrapperType(Class<?> primitiveType) {
        if (primitiveType == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveType == Long.TYPE) {
            return Long.class;
        }
        if (primitiveType == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveType == Double.TYPE) {
            return Double.class;
        }
        if (primitiveType == Float.TYPE) {
            return Float.class;
        }
        if (primitiveType == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveType == Short.TYPE) {
            return Short.class;
        }
        if (primitiveType == Character.TYPE) {
            return Character.class;
        }
        throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
    }

    public static Class<?> primitiveType(Class<?> type) {
        if (type.isPrimitive()) {
            return type;
        }
        if (type == Integer.class) {
            return Integer.TYPE;
        }
        if (type == Long.class) {
            return Long.TYPE;
        }
        if (type == Boolean.class) {
            return Boolean.TYPE;
        }
        if (type == Double.class) {
            return Double.TYPE;
        }
        if (type == Float.class) {
            return Float.TYPE;
        }
        if (type == Byte.class) {
            return Byte.TYPE;
        }
        if (type == Short.class) {
            return Short.TYPE;
        }
        if (type == Character.class) {
            return Character.TYPE;
        }
        return null;
    }

    @Deprecated
    public static void checkAndFixAccess(Member member) {
        checkAndFixAccess(member, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0021, code lost:
        if (java.lang.reflect.Modifier.isPublic(r5.getDeclaringClass().getModifiers()) != false) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void checkAndFixAccess(java.lang.reflect.Member r5, boolean r6) {
        /*
            r0 = r5
            java.lang.reflect.AccessibleObject r0 = (java.lang.reflect.AccessibleObject) r0
            r7 = r0
            r0 = r6
            if (r0 != 0) goto L24
            r0 = r5
            int r0 = r0.getModifiers()     // Catch: java.lang.SecurityException -> L2c
            boolean r0 = java.lang.reflect.Modifier.isPublic(r0)     // Catch: java.lang.SecurityException -> L2c
            if (r0 == 0) goto L24
            r0 = r5
            java.lang.Class r0 = r0.getDeclaringClass()     // Catch: java.lang.SecurityException -> L2c
            int r0 = r0.getModifiers()     // Catch: java.lang.SecurityException -> L2c
            boolean r0 = java.lang.reflect.Modifier.isPublic(r0)     // Catch: java.lang.SecurityException -> L2c
            if (r0 != 0) goto L29
        L24:
            r0 = r7
            r1 = 1
            r0.setAccessible(r1)     // Catch: java.lang.SecurityException -> L2c
        L29:
            goto L70
        L2c:
            r8 = move-exception
            r0 = r7
            boolean r0 = r0.isAccessible()
            if (r0 != 0) goto L70
            r0 = r5
            java.lang.Class r0 = r0.getDeclaringClass()
            r9 = r0
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            java.lang.String r3 = "Cannot access "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r5
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = " (from class "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r9
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = "; failed to set access: "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r8
            java.lang.String r3 = r3.getMessage()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r0
        L70:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.util.ClassUtil.checkAndFixAccess(java.lang.reflect.Member, boolean):void");
    }

    public static Class<? extends Enum<?>> findEnumType(EnumSet<?> s) {
        if (!s.isEmpty()) {
            return findEnumType((Enum) s.iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(s);
    }

    public static Class<? extends Enum<?>> findEnumType(EnumMap<?, ?> m) {
        if (!m.isEmpty()) {
            return findEnumType((Enum) m.keySet().iterator().next());
        }
        return EnumTypeLocator.instance.enumTypeFor(m);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Class<? extends Enum<?>> findEnumType(Enum<?> en) {
        Class<? super Object> cls = en.getClass();
        Class<? super Object> superclass = cls.getSuperclass();
        Class<? super Object> cls2 = cls;
        if (superclass != Enum.class) {
            cls2 = cls.getSuperclass();
        }
        return cls2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Class<? extends Enum<?>> findEnumType(Class<?> cls) {
        if (cls.getSuperclass() != Enum.class) {
            cls = cls.getSuperclass();
        }
        return cls;
    }

    public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass) {
        Field[] fields = getDeclaredFields(enumClass);
        for (Field field : fields) {
            if (field.isEnumConstant()) {
                Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
                if (defaultValueAnnotation != null) {
                    String name = field.getName();
                    Enum<?>[] arr$ = enumClass.getEnumConstants();
                    for (Enum<?> enumValue : arr$) {
                        if (name.equals(enumValue.name())) {
                            return enumValue;
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    public static boolean isJacksonStdImpl(Object impl) {
        return impl == null || isJacksonStdImpl(impl.getClass());
    }

    public static boolean isJacksonStdImpl(Class<?> implClass) {
        return implClass.getAnnotation(JacksonStdImpl.class) != null;
    }

    public static String getPackageName(Class<?> cls) {
        Package pkg = cls.getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getName();
    }

    public static boolean hasEnclosingMethod(Class<?> cls) {
        return (isObjectOrPrimitive(cls) || cls.getEnclosingMethod() == null) ? false : true;
    }

    public static Field[] getDeclaredFields(Class<?> cls) {
        return cls.getDeclaredFields();
    }

    public static Method[] getDeclaredMethods(Class<?> cls) {
        return cls.getDeclaredMethods();
    }

    public static Annotation[] findClassAnnotations(Class<?> cls) {
        if (isObjectOrPrimitive(cls)) {
            return NO_ANNOTATIONS;
        }
        return cls.getDeclaredAnnotations();
    }

    public static Method[] getClassMethods(Class<?> cls) {
        try {
            return getDeclaredMethods(cls);
        } catch (NoClassDefFoundError ex) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                throw ex;
            }
            try {
                Class<?> contextClass = loader.loadClass(cls.getName());
                return contextClass.getDeclaredMethods();
            } catch (ClassNotFoundException e) {
                ex.addSuppressed(e);
                throw ex;
            }
        }
    }

    public static Ctor[] getConstructors(Class<?> cls) {
        if (cls.isInterface() || isObjectOrPrimitive(cls)) {
            return NO_CTORS;
        }
        Constructor<?>[] rawCtors = cls.getDeclaredConstructors();
        int len = rawCtors.length;
        Ctor[] result = new Ctor[len];
        for (int i = 0; i < len; i++) {
            result[i] = new Ctor(rawCtors[i]);
        }
        return result;
    }

    public static Class<?> getDeclaringClass(Class<?> cls) {
        if (isObjectOrPrimitive(cls)) {
            return null;
        }
        return cls.getDeclaringClass();
    }

    public static Type getGenericSuperclass(Class<?> cls) {
        return cls.getGenericSuperclass();
    }

    public static Type[] getGenericInterfaces(Class<?> cls) {
        return cls.getGenericInterfaces();
    }

    public static Class<?> getEnclosingClass(Class<?> cls) {
        if (isObjectOrPrimitive(cls)) {
            return null;
        }
        return cls.getEnclosingClass();
    }

    private static Class<?>[] _interfaces(Class<?> cls) {
        return cls.getInterfaces();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ClassUtil$EnumTypeLocator.class */
    public static class EnumTypeLocator {
        static final EnumTypeLocator instance = new EnumTypeLocator();
        private final Field enumSetTypeField = locateField(EnumSet.class, "elementType", Class.class);
        private final Field enumMapTypeField = locateField(EnumMap.class, "elementType", Class.class);

        private EnumTypeLocator() {
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumSet<?> set) {
            if (this.enumSetTypeField != null) {
                return (Class) get(set, this.enumSetTypeField);
            }
            throw new IllegalStateException("Cannot figure out type for EnumSet (odd JDK platform?)");
        }

        public Class<? extends Enum<?>> enumTypeFor(EnumMap<?, ?> set) {
            if (this.enumMapTypeField != null) {
                return (Class) get(set, this.enumMapTypeField);
            }
            throw new IllegalStateException("Cannot figure out type for EnumMap (odd JDK platform?)");
        }

        private Object get(Object bean, Field field) {
            try {
                return field.get(bean);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type) {
            Field found = null;
            Field[] fields = ClassUtil.getDeclaredFields(fromClass);
            int len$ = fields.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                Field f = fields[i$];
                if (!expectedName.equals(f.getName()) || f.getType() != type) {
                    i$++;
                } else {
                    found = f;
                    break;
                }
            }
            if (found == null) {
                for (Field f2 : fields) {
                    if (f2.getType() == type) {
                        if (found != null) {
                            return null;
                        }
                        found = f2;
                    }
                }
            }
            if (found != null) {
                try {
                    found.setAccessible(true);
                } catch (Throwable th) {
                }
            }
            return found;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/ClassUtil$Ctor.class */
    public static final class Ctor {
        public final Constructor<?> _ctor;
        private Annotation[] _annotations;
        private Annotation[][] _paramAnnotations;
        private int _paramCount = -1;

        public Ctor(Constructor<?> ctor) {
            this._ctor = ctor;
        }

        public Constructor<?> getConstructor() {
            return this._ctor;
        }

        public int getParamCount() {
            int c = this._paramCount;
            if (c < 0) {
                c = this._ctor.getParameterTypes().length;
                this._paramCount = c;
            }
            return c;
        }

        public Class<?> getDeclaringClass() {
            return this._ctor.getDeclaringClass();
        }

        public Annotation[] getDeclaredAnnotations() {
            Annotation[] result = this._annotations;
            if (result == null) {
                result = this._ctor.getDeclaredAnnotations();
                this._annotations = result;
            }
            return result;
        }

        public Annotation[][] getParameterAnnotations() {
            Annotation[][] result = this._paramAnnotations;
            if (result == null) {
                result = this._ctor.getParameterAnnotations();
                this._paramAnnotations = result;
            }
            return result;
        }
    }
}