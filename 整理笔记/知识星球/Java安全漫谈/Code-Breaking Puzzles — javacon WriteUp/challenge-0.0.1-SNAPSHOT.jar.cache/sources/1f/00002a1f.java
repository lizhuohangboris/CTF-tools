package org.unbescape;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/ClassLoaderUtils.class */
final class ClassLoaderUtils {
    private static final ClassLoader classClassLoader = getClassClassLoader(ClassLoaderUtils.class);
    private static final ClassLoader systemClassLoader = getSystemClassLoader();
    private static final boolean systemClassLoaderAccessibleFromClassClassLoader = isKnownClassLoaderAccessibleFrom(systemClassLoader, classClassLoader);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InputStream loadResourceAsStream(String resourceName) throws IOException {
        InputStream inputStream = findResourceAsStream(resourceName);
        if (inputStream != null) {
            return inputStream;
        }
        throw new IOException("Could not locate resource '" + resourceName + "' in the application's class path");
    }

    static InputStream findResourceAsStream(String resourceName) {
        InputStream inputStream;
        InputStream inputStream2;
        InputStream inputStream3;
        ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null && (inputStream3 = contextClassLoader.getResourceAsStream(resourceName)) != null) {
            return inputStream3;
        }
        if (!isKnownLeafClassLoader(contextClassLoader)) {
            if (classClassLoader != null && classClassLoader != contextClassLoader && (inputStream2 = classClassLoader.getResourceAsStream(resourceName)) != null) {
                return inputStream2;
            }
            if (!systemClassLoaderAccessibleFromClassClassLoader && systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader && (inputStream = systemClassLoader.getResourceAsStream(resourceName)) != null) {
                return inputStream;
            }
            return null;
        }
        return null;
    }

    private static ClassLoader getThreadContextClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            return null;
        }
    }

    private static ClassLoader getClassClassLoader(Class<?> clazz) {
        try {
            return clazz.getClassLoader();
        } catch (SecurityException e) {
            return null;
        }
    }

    private static ClassLoader getSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (SecurityException e) {
            return null;
        }
    }

    private static boolean isKnownClassLoaderAccessibleFrom(ClassLoader accessibleCL, ClassLoader fromCL) {
        if (fromCL == null) {
            return false;
        }
        ClassLoader parent = fromCL;
        while (parent != null && parent != accessibleCL) {
            try {
                parent = parent.getParent();
            } catch (SecurityException e) {
                return false;
            }
        }
        return parent != null && parent == accessibleCL;
    }

    private static boolean isKnownLeafClassLoader(ClassLoader classLoader) {
        if (classLoader == null || !isKnownClassLoaderAccessibleFrom(classClassLoader, classLoader)) {
            return false;
        }
        return systemClassLoaderAccessibleFromClassClassLoader;
    }

    private ClassLoaderUtils() {
    }
}