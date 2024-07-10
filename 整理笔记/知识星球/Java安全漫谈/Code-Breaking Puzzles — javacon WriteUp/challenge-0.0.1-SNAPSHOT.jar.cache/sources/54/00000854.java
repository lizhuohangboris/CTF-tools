package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/loader/WebappClassLoader.class */
public class WebappClassLoader extends WebappClassLoaderBase {
    public WebappClassLoader() {
    }

    public WebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override // org.apache.tomcat.InstrumentableClassLoader
    public WebappClassLoader copyWithoutTransformers() {
        WebappClassLoader result = new WebappClassLoader(getParent());
        super.copyStateWithoutTransformers(result);
        try {
            result.start();
            return result;
        } catch (LifecycleException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Object getClassLoadingLock(String className) {
        return this;
    }
}