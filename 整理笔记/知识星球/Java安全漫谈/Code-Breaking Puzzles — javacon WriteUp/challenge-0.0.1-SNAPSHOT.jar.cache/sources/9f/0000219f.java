package org.springframework.instrument.classloading;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/SimpleLoadTimeWeaver.class */
public class SimpleLoadTimeWeaver implements LoadTimeWeaver {
    private final SimpleInstrumentableClassLoader classLoader;

    public SimpleLoadTimeWeaver() {
        this.classLoader = new SimpleInstrumentableClassLoader(ClassUtils.getDefaultClassLoader());
    }

    public SimpleLoadTimeWeaver(SimpleInstrumentableClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = classLoader;
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
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