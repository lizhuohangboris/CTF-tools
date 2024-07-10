package org.springframework.instrument.classloading.websphere;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/websphere/WebSphereClassPreDefinePlugin.class */
class WebSphereClassPreDefinePlugin implements InvocationHandler {
    private final ClassFileTransformer transformer;

    public WebSphereClassPreDefinePlugin(ClassFileTransformer transformer) {
        this.transformer = transformer;
        ClassLoader classLoader = transformer.getClass().getClassLoader();
        try {
            String dummyClass = Dummy.class.getName().replace('.', '/');
            byte[] bytes = FileCopyUtils.copyToByteArray(classLoader.getResourceAsStream(dummyClass + ClassUtils.CLASS_FILE_SUFFIX));
            transformer.transform(classLoader, dummyClass, (Class) null, (ProtectionDomain) null, bytes);
        } catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot load transformer", ex);
        }
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("equals".equals(name)) {
            return Boolean.valueOf(proxy == args[0]);
        } else if (IdentityNamingStrategy.HASH_CODE_KEY.equals(name)) {
            return Integer.valueOf(hashCode());
        } else {
            if ("toString".equals(name)) {
                return toString();
            }
            if ("transformClass".equals(name)) {
                return transform((String) args[0], (byte[]) args[1], (CodeSource) args[2], (ClassLoader) args[3]);
            }
            throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    protected byte[] transform(String className, byte[] classfileBuffer, CodeSource codeSource, ClassLoader classLoader) throws Exception {
        byte[] result = this.transformer.transform(classLoader, className.replace('.', '/'), (Class) null, (ProtectionDomain) null, classfileBuffer);
        return result != null ? result : classfileBuffer;
    }

    public String toString() {
        return getClass().getName() + " for transformer: " + this.transformer;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/websphere/WebSphereClassPreDefinePlugin$Dummy.class */
    private static class Dummy {
        private Dummy() {
        }
    }
}