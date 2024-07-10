package org.apache.tomcat;

import java.lang.instrument.ClassFileTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/InstrumentableClassLoader.class */
public interface InstrumentableClassLoader {
    void addTransformer(ClassFileTransformer classFileTransformer);

    void removeTransformer(ClassFileTransformer classFileTransformer);

    ClassLoader copyWithoutTransformers();
}