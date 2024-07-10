package org.springframework.instrument.classloading.weblogic;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/weblogic/WebLogicLoadTimeWeaver.class */
public class WebLogicLoadTimeWeaver implements LoadTimeWeaver {
    private final WebLogicClassLoaderAdapter classLoader;

    public WebLogicLoadTimeWeaver() {
        this(ClassUtils.getDefaultClassLoader());
    }

    public WebLogicLoadTimeWeaver(@Nullable ClassLoader classLoader) {
        Assert.notNull(classLoader, "ClassLoader must not be null");
        this.classLoader = new WebLogicClassLoaderAdapter(classLoader);
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        this.classLoader.addTransformer(transformer);
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getInstrumentableClassLoader() {
        return this.classLoader.getClassLoader();
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getThrowawayClassLoader() {
        return new OverridingClassLoader(this.classLoader.getClassLoader(), this.classLoader.getThrowawayClassLoader());
    }
}