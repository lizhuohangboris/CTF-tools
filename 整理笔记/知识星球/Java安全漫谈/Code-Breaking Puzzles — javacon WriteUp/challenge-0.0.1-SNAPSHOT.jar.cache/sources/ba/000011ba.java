package org.hibernate.validator.internal.xml.mapping;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/mapping/ClassLoadingHelper.class */
public class ClassLoadingHelper {
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String ARRAY_CLASS_NAME_PREFIX = "[L";
    private static final String ARRAY_CLASS_NAME_SUFFIX = ";";
    private static final Map<String, Class<?>> PRIMITIVE_NAME_TO_PRIMITIVE;
    private final ClassLoader externalClassLoader;
    private final ClassLoader threadContextClassLoader;

    static {
        Map<String, Class<?>> tmpMap = CollectionHelper.newHashMap(9);
        tmpMap.put(Boolean.TYPE.getName(), Boolean.TYPE);
        tmpMap.put(Character.TYPE.getName(), Character.TYPE);
        tmpMap.put(Double.TYPE.getName(), Double.TYPE);
        tmpMap.put(Float.TYPE.getName(), Float.TYPE);
        tmpMap.put(Long.TYPE.getName(), Long.TYPE);
        tmpMap.put(Integer.TYPE.getName(), Integer.TYPE);
        tmpMap.put(Short.TYPE.getName(), Short.TYPE);
        tmpMap.put(Byte.TYPE.getName(), Byte.TYPE);
        tmpMap.put(Void.TYPE.getName(), Void.TYPE);
        PRIMITIVE_NAME_TO_PRIMITIVE = Collections.unmodifiableMap(tmpMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassLoadingHelper(ClassLoader externalClassLoader, ClassLoader threadContextClassLoader) {
        this.externalClassLoader = externalClassLoader;
        this.threadContextClassLoader = threadContextClassLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Class<?> loadClass(String className, String defaultPackage) {
        if (PRIMITIVE_NAME_TO_PRIMITIVE.containsKey(className)) {
            return PRIMITIVE_NAME_TO_PRIMITIVE.get(className);
        }
        StringBuilder fullyQualifiedClass = new StringBuilder();
        String tmpClassName = className;
        if (isArrayClassName(className)) {
            fullyQualifiedClass.append(ARRAY_CLASS_NAME_PREFIX);
            tmpClassName = getArrayElementClassName(className);
        }
        if (isQualifiedClass(tmpClassName)) {
            fullyQualifiedClass.append(tmpClassName);
        } else {
            fullyQualifiedClass.append(defaultPackage);
            fullyQualifiedClass.append(".");
            fullyQualifiedClass.append(tmpClassName);
        }
        if (isArrayClassName(className)) {
            fullyQualifiedClass.append(ARRAY_CLASS_NAME_SUFFIX);
        }
        return loadClass(fullyQualifiedClass.toString());
    }

    private Class<?> loadClass(String className) {
        return (Class) run(LoadClass.action(className, this.externalClassLoader, this.threadContextClassLoader));
    }

    private static boolean isArrayClassName(String className) {
        return className.startsWith(ARRAY_CLASS_NAME_PREFIX) && className.endsWith(ARRAY_CLASS_NAME_SUFFIX);
    }

    private static String getArrayElementClassName(String className) {
        return className.substring(2, className.length() - 1);
    }

    private static boolean isQualifiedClass(String clazz) {
        return clazz.contains(".");
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}