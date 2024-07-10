package org.springframework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ReflectionUtils.class */
public abstract class ReflectionUtils {
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
    private static final Method[] NO_METHODS = new Method[0];
    private static final Field[] NO_FIELDS = new Field[0];
    public static final FieldFilter COPYABLE_FIELDS = field -> {
        return (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) ? false : true;
    };
    public static final MethodFilter NON_BRIDGED_METHODS = method -> {
        return !method.isBridge();
    };
    public static final MethodFilter USER_DECLARED_METHODS = method -> {
        return (method.isBridge() || method.isSynthetic() || method.getDeclaringClass() == Object.class) ? false : true;
    };
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap(256);

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ReflectionUtils$FieldCallback.class */
    public interface FieldCallback {
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ReflectionUtils$FieldFilter.class */
    public interface FieldFilter {
        boolean matches(Field field);
    }

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ReflectionUtils$MethodCallback.class */
    public interface MethodCallback {
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ReflectionUtils$MethodFilter.class */
    public interface MethodFilter {
        boolean matches(Method method);
    }

    @Nullable
    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:63:0x0066, code lost:
        return r0;
     */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.reflect.Field findField(java.lang.Class<?> r3, @org.springframework.lang.Nullable java.lang.String r4, @org.springframework.lang.Nullable java.lang.Class<?> r5) {
        /*
            r0 = r3
            java.lang.String r1 = "Class must not be null"
            org.springframework.util.Assert.notNull(r0, r1)
            r0 = r4
            if (r0 != 0) goto Le
            r0 = r5
            if (r0 == 0) goto L12
        Le:
            r0 = 1
            goto L13
        L12:
            r0 = 0
        L13:
            java.lang.String r1 = "Either name or type of the field must be specified"
            org.springframework.util.Assert.isTrue(r0, r1)
            r0 = r3
            r6 = r0
        L1a:
            java.lang.Class<java.lang.Object> r0 = java.lang.Object.class
            r1 = r6
            if (r0 == r1) goto L75
            r0 = r6
            if (r0 == 0) goto L75
            r0 = r6
            java.lang.reflect.Field[] r0 = getDeclaredFields(r0)
            r7 = r0
            r0 = r7
            r8 = r0
            r0 = r8
            int r0 = r0.length
            r9 = r0
            r0 = 0
            r10 = r0
        L36:
            r0 = r10
            r1 = r9
            if (r0 >= r1) goto L6d
            r0 = r8
            r1 = r10
            r0 = r0[r1]
            r11 = r0
            r0 = r4
            if (r0 == 0) goto L54
            r0 = r4
            r1 = r11
            java.lang.String r1 = r1.getName()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L67
        L54:
            r0 = r5
            if (r0 == 0) goto L64
            r0 = r5
            r1 = r11
            java.lang.Class r1 = r1.getType()
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L67
        L64:
            r0 = r11
            return r0
        L67:
            int r10 = r10 + 1
            goto L36
        L6d:
            r0 = r6
            java.lang.Class r0 = r0.getSuperclass()
            r6 = r0
            goto L1a
        L75:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.util.ReflectionUtils.findField(java.lang.Class, java.lang.String, java.lang.Class):java.lang.reflect.Field");
    }

    public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    @Nullable
    public static Object getField(Field field, @Nullable Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class[0]);
    }

    @Nullable
    public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
        int i;
        Method method;
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class<?> cls = clazz;
        loop0: while (true) {
            Class<?> searchType = cls;
            if (searchType != null) {
                Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType);
                int length = methods.length;
                for (i = 0; i < length; i = i + 1) {
                    method = methods[i];
                    i = (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) ? 0 : i + 1;
                }
                cls = searchType.getSuperclass();
            } else {
                return null;
            }
        }
        return method;
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target) {
        return invokeMethod(method, target, new Object[0]);
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }

    @Nullable
    public static Object invokeJdbcMethod(Method method, @Nullable Object target) throws SQLException {
        return invokeJdbcMethod(method, target, new Object[0]);
    }

    @Nullable
    public static Object invokeJdbcMethod(Method method, @Nullable Object target, @Nullable Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        } catch (InvocationTargetException ex2) {
            if (ex2.getTargetException() instanceof SQLException) {
                throw ((SQLException) ex2.getTargetException());
            }
            handleInvocationTargetException(ex2);
            throw new IllegalStateException("Should never get here");
        }
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw ((RuntimeException) ex);
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw ((RuntimeException) ex);
        }
        if (ex instanceof Error) {
            throw ((Error) ex);
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw ((Exception) ex);
        }
        if (ex instanceof Error) {
            throw ((Error) ex);
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static boolean declaresException(Method method, Class<?> exceptionType) {
        Assert.notNull(method, "Method must not be null");
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return paramTypes.length == 1 && paramTypes[0] == Object.class;
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return method != null && method.getName().equals(IdentityNamingStrategy.HASH_CODE_KEY) && method.getParameterCount() == 0;
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return method != null && method.getName().equals("toString") && method.getParameterCount() == 0;
    }

    public static boolean isObjectMethod(@Nullable Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) {
                i--;
            }
            return i > CGLIB_RENAMED_METHOD_PREFIX.length() && i < name.length() - 1 && name.charAt(i) == '$';
        }
        return false;
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        makeAccessible((Constructor<?>) ctor);
        return ctor;
    }

    public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
        doWithMethods(clazz, mc, null);
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
        Class<?>[] interfaces;
        Method[] methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (mf == null || mf.matches(method)) {
                try {
                    mc.doWith(method);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
        List<Method> methods = new ArrayList<>(32);
        methods.getClass();
        doWithMethods(leafClass, (v1) -> {
            r1.add(v1);
        });
        return (Method[]) methods.toArray(new Method[0]);
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
        List<Method> methods = new ArrayList<>(32);
        doWithMethods(leafClass, method -> {
            boolean knownSignature = false;
            Method methodBeingOverriddenWithCovariantReturnType = null;
            Iterator it = methods.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Method existingMethod = (Method) it.next();
                if (method.getName().equals(existingMethod.getName()) && Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                    if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                        methodBeingOverriddenWithCovariantReturnType = existingMethod;
                    } else {
                        knownSignature = true;
                    }
                }
            }
            if (methodBeingOverriddenWithCovariantReturnType != null) {
                methods.remove(methodBeingOverriddenWithCovariantReturnType);
            }
            if (!knownSignature && !isCglibRenamedMethod(method)) {
                methods.add(method);
            }
        });
        return (Method[]) methods.toArray(new Method[0]);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result = declaredMethods;
                }
                declaredMethodsCache.put(clazz, result.length == 0 ? NO_METHODS : result);
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    @Nullable
    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        Class<?>[] interfaces;
        Method[] methods;
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
        Field[] declaredFields;
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        doWithFields(clazz, fc, null);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff == null || ff.matches(field)) {
                    try {
                        fc.doWith(field);
                    } catch (IllegalAccessException ex) {
                        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
            if (targetClass == null) {
                return;
            }
        } while (targetClass != Object.class);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, result.length == 0 ? NO_FIELDS : result);
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    public static void shallowCopyFieldState(Object src, Object dest) {
        Assert.notNull(src, "Source for field copy cannot be null");
        Assert.notNull(dest, "Destination for field copy cannot be null");
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        doWithFields(src.getClass(), field -> {
            makeAccessible(field);
            Object srcValue = field.get(src);
            field.set(dest, srcValue);
        }, COPYABLE_FIELDS);
    }

    public static void clearCache() {
        declaredMethodsCache.clear();
        declaredFieldsCache.clear();
    }
}