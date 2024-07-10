package org.springframework.instrument.classloading;

import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/instrument/classloading/SimpleThrowawayClassLoader.class */
public class SimpleThrowawayClassLoader extends OverridingClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public SimpleThrowawayClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }
}