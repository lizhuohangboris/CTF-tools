package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/weblogic/WebLogicClassLoaderAdapter.class */
class WebLogicClassLoaderAdapter {
    private static final String GENERIC_CLASS_LOADER_NAME = "weblogic.utils.classloaders.GenericClassLoader";
    private static final String CLASS_PRE_PROCESSOR_NAME = "weblogic.utils.classloaders.ClassPreProcessor";
    private final ClassLoader classLoader;
    private final Class<?> wlPreProcessorClass;
    private final Method addPreProcessorMethod;
    private final Method getClassFinderMethod;
    private final Method getParentMethod;
    private final Constructor<?> wlGenericClassLoaderConstructor;

    public WebLogicClassLoaderAdapter(ClassLoader classLoader) {
        try {
            Class<?> wlGenericClassLoaderClass = classLoader.loadClass(GENERIC_CLASS_LOADER_NAME);
            this.wlPreProcessorClass = classLoader.loadClass(CLASS_PRE_PROCESSOR_NAME);
            this.addPreProcessorMethod = classLoader.getClass().getMethod("addInstanceClassPreProcessor", this.wlPreProcessorClass);
            this.getClassFinderMethod = classLoader.getClass().getMethod("getClassFinder", new Class[0]);
            this.getParentMethod = classLoader.getClass().getMethod("getParent", new Class[0]);
            this.wlGenericClassLoaderConstructor = wlGenericClassLoaderClass.getConstructor(this.getClassFinderMethod.getReturnType(), ClassLoader.class);
            if (!wlGenericClassLoaderClass.isInstance(classLoader)) {
                throw new IllegalArgumentException("ClassLoader must be an instance of [" + wlGenericClassLoaderClass.getName() + "]: " + classLoader);
            }
            this.classLoader = classLoader;
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize WebLogic LoadTimeWeaver because WebLogic 10 API classes are not available", ex);
        }
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull(transformer, "ClassFileTransformer must not be null");
        try {
            InvocationHandler adapter = new WebLogicClassPreProcessorAdapter(transformer, this.classLoader);
            Object adapterInstance = Proxy.newProxyInstance(this.wlPreProcessorClass.getClassLoader(), new Class[]{this.wlPreProcessorClass}, adapter);
            this.addPreProcessorMethod.invoke(this.classLoader, adapterInstance);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic addInstanceClassPreProcessor method threw exception", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not invoke WebLogic addInstanceClassPreProcessor method", ex2);
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ClassLoader getThrowawayClassLoader() {
        try {
            Object classFinder = this.getClassFinderMethod.invoke(this.classLoader, new Object[0]);
            Object parent = this.getParentMethod.invoke(this.classLoader, new Object[0]);
            return (ClassLoader) this.wlGenericClassLoaderConstructor.newInstance(classFinder, parent);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("WebLogic GenericClassLoader constructor failed", ex.getCause());
        } catch (Throwable ex2) {
            throw new IllegalStateException("Could not construct WebLogic GenericClassLoader", ex2);
        }
    }
}