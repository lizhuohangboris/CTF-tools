package org.springframework.instrument.classloading.jboss;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/jboss/JBossLoadTimeWeaver.class */
public class JBossLoadTimeWeaver implements LoadTimeWeaver {
    private static final String DELEGATING_TRANSFORMER_CLASS_NAME = "org.jboss.as.server.deployment.module.DelegatingClassFileTransformer";
    private final ClassLoader classLoader;
    private final Object delegatingTransformer;
    private final Method addTransformer;

    public JBossLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public JBossLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
        try {
            Field transformer = ReflectionUtils.findField(classLoader.getClass(), "transformer");
            if (transformer == null) {
                throw new IllegalArgumentException("Could not find 'transformer' field on JBoss ClassLoader: " + classLoader.getClass().getName());
            }
            transformer.setAccessible(true);
            this.delegatingTransformer = transformer.get(classLoader);
            if (!this.delegatingTransformer.getClass().getName().equals(DELEGATING_TRANSFORMER_CLASS_NAME)) {
                throw new IllegalStateException("Transformer not of the expected type DelegatingClassFileTransformer: " + this.delegatingTransformer.getClass().getName());
            }
            Method addTransformer = ReflectionUtils.findMethod(this.delegatingTransformer.getClass(), "addTransformer", ClassFileTransformer.class);
            if (addTransformer == null) {
                throw new IllegalArgumentException("Could not find 'addTransformer' method on JBoss DelegatingClassFileTransformer: " + this.delegatingTransformer.getClass().getName());
            }
            addTransformer.setAccessible(true);
            this.addTransformer = addTransformer;
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not initialize JBoss LoadTimeWeaver", ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        try {
            this.addTransformer.invoke(this.delegatingTransformer, transformer);
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not add transformer on JBoss ClassLoader: " + this.classLoader, ex);
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader;
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getThrowawayClassLoader() {
        return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
    }
}