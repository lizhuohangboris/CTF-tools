package org.springframework.context.support;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.OverridingClassLoader;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ContextTypeMatchClassLoader.class */
class ContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader {
    private static Method findLoadedClassMethod;
    private final Map<String, byte[]> bytesCache;

    static {
        ClassLoader.registerAsParallelCapable();
        try {
            findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Invalid [java.lang.ClassLoader] class: no 'findLoadedClass' method defined!");
        }
    }

    public ContextTypeMatchClassLoader(@Nullable ClassLoader parent) {
        super(parent);
        this.bytesCache = new ConcurrentHashMap(256);
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return new ContextOverridingClassLoader(getParent()).loadClass(name);
    }

    @Override // org.springframework.core.SmartClassLoader
    public boolean isClassReloadable(Class<?> clazz) {
        return clazz.getClassLoader() instanceof ContextOverridingClassLoader;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ContextTypeMatchClassLoader$ContextOverridingClassLoader.class */
    private class ContextOverridingClassLoader extends OverridingClassLoader {
        public ContextOverridingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override // org.springframework.core.OverridingClassLoader
        protected boolean isEligibleForOverriding(String className) {
            if (!isExcluded(className) && !ContextTypeMatchClassLoader.this.isExcluded(className)) {
                ReflectionUtils.makeAccessible(ContextTypeMatchClassLoader.findLoadedClassMethod);
                ClassLoader parent = getParent();
                while (true) {
                    ClassLoader parent2 = parent;
                    if (parent2 == null) {
                        return true;
                    }
                    if (ReflectionUtils.invokeMethod(ContextTypeMatchClassLoader.findLoadedClassMethod, parent2, className) != null) {
                        return false;
                    }
                    parent = parent2.getParent();
                }
            } else {
                return false;
            }
        }

        @Override // org.springframework.core.OverridingClassLoader
        protected Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
            byte[] bytes = (byte[]) ContextTypeMatchClassLoader.this.bytesCache.get(name);
            if (bytes == null) {
                bytes = loadBytesForClass(name);
                if (bytes != null) {
                    ContextTypeMatchClassLoader.this.bytesCache.put(name, bytes);
                } else {
                    return null;
                }
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}