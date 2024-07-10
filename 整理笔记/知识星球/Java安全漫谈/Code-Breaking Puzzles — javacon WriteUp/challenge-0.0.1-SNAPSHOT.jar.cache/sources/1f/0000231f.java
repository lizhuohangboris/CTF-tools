package org.springframework.util;

import java.beans.Introspector;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ClassUtils.class */
public abstract class ClassUtils {
    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';
    private static final char INNER_CLASS_SEPARATOR = '$';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    public static final String CLASS_FILE_SUFFIX = ".class";
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap(64);
    private static final Set<Class<?>> javaLanguageInterfaces;

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }
        Set<Class<?>> primitiveTypes = new HashSet<>(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class);
        primitiveTypes.add(Void.TYPE);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }
        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);
        Class<?>[] javaLanguageInterfaceArray = {Serializable.class, Externalizable.class, Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
        registerCommonClasses(javaLanguageInterfaceArray);
        javaLanguageInterfaces = new HashSet(Arrays.asList(javaLanguageInterfaceArray));
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    @Nullable
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable th) {
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable th2) {
                }
            }
        }
        return cl;
    }

    @Nullable
    public static ClassLoader overrideThreadContextClassLoader(@Nullable ClassLoader classLoaderToUse) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(classLoaderToUse);
            return threadContextClassLoader;
        }
        return null;
    }

    public static Class<?> forName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass2 = forName(elementName, classLoader);
            return Array.newInstance(elementClass2, 0).getClass();
        } else if (name.startsWith("[")) {
            String elementName2 = name.substring("[".length());
            Class<?> elementClass3 = forName(elementName2, classLoader);
            return Array.newInstance(elementClass3, 0).getClass();
        } else {
            ClassLoader clToUse = classLoader;
            if (clToUse == null) {
                clToUse = getDefaultClassLoader();
            }
            try {
                return Class.forName(name, false, clToUse);
            } catch (ClassNotFoundException ex) {
                int lastDotIndex = name.lastIndexOf(46);
                if (lastDotIndex != -1) {
                    String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
                    try {
                        return Class.forName(innerClassName, false, clToUse);
                    } catch (ClassNotFoundException e) {
                        throw ex;
                    }
                }
                throw ex;
            }
        }
    }

    public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
        } catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(), err);
        } catch (LinkageError err2) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err2);
        }
    }

    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(), err);
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean isVisible(Class<?> clazz, @Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            if (clazz.getClassLoader() == classLoader) {
                return true;
            }
        } catch (SecurityException e) {
        }
        return isLoadable(clazz, classLoader);
    }

    public static boolean isCacheSafe(Class<?> clazz, @Nullable ClassLoader classLoader) {
        ClassLoader target;
        Assert.notNull(clazz, "Class must not be null");
        try {
            target = clazz.getClassLoader();
        } catch (SecurityException e) {
        }
        if (target == classLoader || target == null) {
            return true;
        }
        if (classLoader == null) {
            return false;
        }
        ClassLoader current = classLoader;
        while (current != null) {
            current = current.getParent();
            if (current == target) {
                return true;
            }
        }
        while (target != null) {
            target = target.getParent();
            if (target == classLoader) {
                return false;
            }
        }
        return classLoader != null && isLoadable(clazz, classLoader);
    }

    private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
        try {
            return clazz == classLoader.loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Nullable
    public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isPrimitiveArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && clazz.getComponentType().isPrimitive();
    }

    public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType());
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (!clazz.isPrimitive() || clazz == Void.TYPE) ? clazz : primitiveTypeToWrapperMap.get(clazz);
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (lhsType == resolvedPrimitive) {
                return true;
            }
            return false;
        }
        Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
        if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
            return true;
        }
        return false;
    }

    public static boolean isAssignableValue(Class<?> type, @Nullable Object value) {
        Assert.notNull(type, "Type must not be null");
        return value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive();
    }

    public static String convertResourcePathToClassName(String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace('/', '.');
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace('.', '/');
    }

    public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
        Assert.notNull(resourceName, "Resource name must not be null");
        if (!resourceName.startsWith("/")) {
            return classPackageAsResourcePath(clazz) + '/' + resourceName;
        }
        return classPackageAsResourcePath(clazz) + resourceName;
    }

    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        String className;
        int packageEndIndex;
        if (clazz == null || (packageEndIndex = (className = clazz.getName()).lastIndexOf(46)) == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace('.', '/');
    }

    public static String classNamesToString(Class<?>... classes) {
        return classNamesToString(Arrays.asList(classes));
    }

    public static String classNamesToString(@Nullable Collection<Class<?>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return ARRAY_SUFFIX;
        }
        StringBuilder sb = new StringBuilder("[");
        Iterator<Class<?>> it = classes.iterator();
        while (it.hasNext()) {
            Class<?> clazz = it.next();
            sb.append(clazz.getName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
        return (Class[]) collection.toArray(new Class[0]);
    }

    public static Class<?>[] getAllInterfaces(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClass(instance.getClass());
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
        return getAllInterfacesForClass(clazz, null);
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, @Nullable ClassLoader classLoader) {
        return toClassArray(getAllInterfacesForClassAsSet(clazz, classLoader));
    }

    public static Set<Class<?>> getAllInterfacesAsSet(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
        return getAllInterfacesForClassAsSet(clazz, null);
    }

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        Class<?> cls = clazz;
        while (true) {
            Class<?> current = cls;
            if (current != null) {
                Class<?>[] ifcs = current.getInterfaces();
                for (Class<?> ifc : ifcs) {
                    if (isVisible(ifc, classLoader)) {
                        interfaces.add(ifc);
                    }
                }
                cls = current.getSuperclass();
            } else {
                return interfaces;
            }
        }
    }

    public static Class<?> createCompositeInterface(Class<?>[] interfaces, @Nullable ClassLoader classLoader) {
        Assert.notEmpty(interfaces, "Interfaces must not be empty");
        return Proxy.getProxyClass(classLoader, interfaces);
    }

    @Nullable
    public static Class<?> determineCommonAncestor(@Nullable Class<?> clazz1, @Nullable Class<?> clazz2) {
        if (clazz1 == null) {
            return clazz2;
        }
        if (clazz2 == null) {
            return clazz1;
        }
        if (clazz1.isAssignableFrom(clazz2)) {
            return clazz1;
        }
        if (clazz2.isAssignableFrom(clazz1)) {
            return clazz2;
        }
        Class<?> ancestor = clazz1;
        do {
            ancestor = ancestor.getSuperclass();
            if (ancestor == null || Object.class == ancestor) {
                return null;
            }
        } while (!ancestor.isAssignableFrom(clazz2));
        return ancestor;
    }

    public static boolean isJavaLanguageInterface(Class<?> ifc) {
        return javaLanguageInterfaces.contains(ifc);
    }

    public static boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    public static boolean isCglibProxy(Object object) {
        return isCglibProxyClass(object.getClass());
    }

    public static boolean isCglibProxyClass(@Nullable Class<?> clazz) {
        return clazz != null && isCglibProxyClassName(clazz.getName());
    }

    public static boolean isCglibProxyClassName(@Nullable String className) {
        return className != null && className.contains(CGLIB_CLASS_SEPARATOR);
    }

    public static Class<?> getUserClass(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        Class<?> superclass;
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR) && (superclass = clazz.getSuperclass()) != null && superclass != Object.class) {
            return superclass;
        }
        return clazz;
    }

    @Nullable
    public static String getDescriptiveType(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (Proxy.isProxyClass(clazz)) {
            StringBuilder result = new StringBuilder(clazz.getName());
            result.append(" implementing ");
            Class<?>[] ifcs = clazz.getInterfaces();
            for (int i = 0; i < ifcs.length; i++) {
                result.append(ifcs[i].getName());
                if (i < ifcs.length - 1) {
                    result.append(',');
                }
            }
            return result.toString();
        }
        return clazz.getTypeName();
    }

    public static boolean matchesTypeName(Class<?> clazz, @Nullable String typeName) {
        return typeName != null && (typeName.equals(clazz.getTypeName()) || typeName.equals(clazz.getSimpleName()));
    }

    public static String getShortName(String className) {
        Assert.hasLength(className, "Class name must not be empty");
        int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        return shortName.replace('$', '.');
    }

    public static String getShortName(Class<?> clazz) {
        return getShortName(getQualifiedName(clazz));
    }

    public static String getShortNameAsProperty(Class<?> clazz) {
        String shortName = getShortName(clazz);
        int dotIndex = shortName.lastIndexOf(46);
        return Introspector.decapitalize(dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
    }

    public static String getClassFileName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(46);
        return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
    }

    public static String getPackageName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return getPackageName(clazz.getName());
    }

    public static String getPackageName(String fqClassName) {
        Assert.notNull(fqClassName, "Class name must not be null");
        int lastDotIndex = fqClassName.lastIndexOf(46);
        return lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "";
    }

    public static String getQualifiedName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.getTypeName();
    }

    public static String getQualifiedMethodName(Method method) {
        return getQualifiedMethodName(method, null);
    }

    public static String getQualifiedMethodName(Method method, @Nullable Class<?> clazz) {
        Assert.notNull(method, "Method must not be null");
        return (clazz != null ? clazz : method.getDeclaringClass()).getName() + '.' + method.getName();
    }

    public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
        return getConstructorIfAvailable(clazz, paramTypes) != null;
    }

    @Nullable
    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return getMethodIfAvailable(clazz, methodName, paramTypes) != null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Expected method not found: " + ex);
            }
        }
        Set<Method> candidates = new HashSet<>(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("Expected method not found: " + clazz.getName() + '.' + methodName);
        }
        throw new IllegalStateException("No unique method found: " + clazz.getName() + '.' + methodName);
    }

    @Nullable
    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        Set<Method> candidates = new HashSet<>(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        return null;
    }

    public static int getMethodCountForName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        int count = 0;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (methodName.equals(method.getName())) {
                count++;
            }
        }
        Class<?>[] ifcs = clazz.getInterfaces();
        for (Class<?> ifc : ifcs) {
            count += getMethodCountForName(ifc, methodName);
        }
        if (clazz.getSuperclass() != null) {
            count += getMethodCountForName(clazz.getSuperclass(), methodName);
        }
        return count;
    }

    public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        Class<?>[] ifcs = clazz.getInterfaces();
        for (Class<?> ifc : ifcs) {
            if (hasAtLeastOneMethodWithName(ifc, methodName)) {
                return true;
            }
        }
        return clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName);
    }

    public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
        if (targetClass != null && targetClass != method.getDeclaringClass() && isOverridable(method, targetClass)) {
            try {
                if (Modifier.isPublic(method.getModifiers())) {
                    try {
                        return targetClass.getMethod(method.getName(), method.getParameterTypes());
                    } catch (NoSuchMethodException e) {
                        return method;
                    }
                }
                Method specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
                return specificMethod != null ? specificMethod : method;
            } catch (SecurityException e2) {
            }
        }
        return method;
    }

    public static Method getInterfaceMethodIfPossible(Method method) {
        if (Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().isInterface()) {
            Class<?> declaringClass = method.getDeclaringClass();
            while (true) {
                Class<?> current = declaringClass;
                if (current == null || current == Object.class) {
                    break;
                }
                Class<?>[] ifcs = current.getInterfaces();
                for (Class<?> ifc : ifcs) {
                    try {
                        return ifc.getMethod(method.getName(), method.getParameterTypes());
                    } catch (NoSuchMethodException e) {
                    }
                }
                declaringClass = current.getSuperclass();
            }
        }
        return method;
    }

    public static boolean isUserLevelMethod(Method method) {
        Assert.notNull(method, "Method must not be null");
        return method.isBridge() || !(method.isSynthetic() || isGroovyObjectMethod(method));
    }

    private static boolean isGroovyObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
    }

    private static boolean isOverridable(Method method, @Nullable Class<?> targetClass) {
        if (Modifier.isPrivate(method.getModifiers())) {
            return false;
        }
        return Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()) || targetClass == null || getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass));
    }

    @Nullable
    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        try {
            Method method = clazz.getMethod(methodName, args);
            if (Modifier.isStatic(method.getModifiers())) {
                return method;
            }
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}