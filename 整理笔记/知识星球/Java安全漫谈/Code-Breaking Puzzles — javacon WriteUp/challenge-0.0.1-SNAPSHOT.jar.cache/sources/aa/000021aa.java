package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Hashtable;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/weblogic/WebLogicClassPreProcessorAdapter.class */
class WebLogicClassPreProcessorAdapter implements InvocationHandler {
    private final ClassFileTransformer transformer;
    private final ClassLoader loader;

    public WebLogicClassPreProcessorAdapter(ClassFileTransformer transformer, ClassLoader loader) {
        this.transformer = transformer;
        this.loader = loader;
    }

    @Override // java.lang.reflect.InvocationHandler
    @Nullable
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
            if ("initialize".equals(name)) {
                initialize((Hashtable) args[0]);
                return null;
            } else if ("preProcess".equals(name)) {
                return preProcess((String) args[0], (byte[]) args[1]);
            } else {
                throw new IllegalArgumentException("Unknown method: " + method);
            }
        }
    }

    public void initialize(Hashtable<?, ?> params) {
    }

    public byte[] preProcess(String className, byte[] classBytes) {
        try {
            byte[] result = this.transformer.transform(this.loader, className, (Class) null, (ProtectionDomain) null, classBytes);
            return result != null ? result : classBytes;
        } catch (IllegalClassFormatException ex) {
            throw new IllegalStateException("Cannot transform due to illegal class format", ex);
        }
    }

    public String toString() {
        return getClass().getName() + " for transformer: " + this.transformer;
    }
}