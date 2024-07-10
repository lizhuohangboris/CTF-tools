package org.springframework.objenesis.instantiator.util;

import org.springframework.objenesis.ObjenesisException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/util/ClassUtils.class */
public final class ClassUtils {
    private ClassUtils() {
    }

    public static String classNameToInternalClassName(String className) {
        return className.replace('.', '/');
    }

    public static String classNameToResource(String className) {
        return classNameToInternalClassName(className) + org.springframework.util.ClassUtils.CLASS_FILE_SUFFIX;
    }

    public static <T> Class<T> getExistingClass(ClassLoader classLoader, String className) {
        try {
            return (Class<T>) Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ObjenesisException(e);
        }
    }
}