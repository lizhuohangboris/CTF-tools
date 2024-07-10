package org.thymeleaf.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ClassLoaderUtils.class */
public final class ClassLoaderUtils {
    private static final ClassLoader classClassLoader = getClassClassLoader(ClassLoaderUtils.class);
    private static final ClassLoader systemClassLoader = getSystemClassLoader();
    private static final boolean systemClassLoaderAccessibleFromClassClassLoader = isKnownClassLoaderAccessibleFrom(systemClassLoader, classClassLoader);

    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader clazzClassLoader;
        ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        if (clazz != null && (clazzClassLoader = getClassClassLoader(clazz)) != null) {
            return clazzClassLoader;
        }
        return systemClassLoader;
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassNotFoundException notFoundException = null;
        ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            try {
                return Class.forName(className, false, contextClassLoader);
            } catch (ClassNotFoundException cnfe) {
                notFoundException = cnfe;
            }
        }
        if (!isKnownLeafClassLoader(contextClassLoader)) {
            if (classClassLoader != null && classClassLoader != contextClassLoader) {
                try {
                    return Class.forName(className, false, classClassLoader);
                } catch (ClassNotFoundException cnfe2) {
                    if (notFoundException == null) {
                        notFoundException = cnfe2;
                    }
                }
            }
            if (!systemClassLoaderAccessibleFromClassClassLoader && systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader) {
                try {
                    return Class.forName(className, false, systemClassLoader);
                } catch (ClassNotFoundException cnfe3) {
                    if (notFoundException == null) {
                        notFoundException = cnfe3;
                    }
                }
            }
        }
        throw notFoundException;
    }

    public static Class<?> findClass(String className) {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean isClassPresent(String className) {
        return findClass(className) != null;
    }

    public static URL findResource(String resourceName) {
        URL url;
        URL url2;
        URL url3;
        ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null && (url3 = contextClassLoader.getResource(resourceName)) != null) {
            return url3;
        }
        if (!isKnownLeafClassLoader(contextClassLoader)) {
            if (classClassLoader != null && classClassLoader != contextClassLoader && (url2 = classClassLoader.getResource(resourceName)) != null) {
                return url2;
            }
            if (!systemClassLoaderAccessibleFromClassClassLoader && systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader && (url = systemClassLoader.getResource(resourceName)) != null) {
                return url;
            }
            return null;
        }
        return null;
    }

    public static boolean isResourcePresent(String resourceName) {
        return findResource(resourceName) != null;
    }

    public static InputStream loadResourceAsStream(String resourceName) throws IOException {
        InputStream inputStream = findResourceAsStream(resourceName);
        if (inputStream != null) {
            return inputStream;
        }
        throw new IOException("Could not locate resource '" + resourceName + "' in the application's class path");
    }

    public static InputStream findResourceAsStream(String resourceName) {
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