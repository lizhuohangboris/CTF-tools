package org.apache.tomcat.util.compat;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Deque;
import java.util.Set;
import java.util.jar.JarFile;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/compat/Jre9Compat.class */
class Jre9Compat extends JreCompat {
    private static final Log log = LogFactory.getLog(Jre9Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre9Compat.class);
    private static final Class<?> inaccessibleObjectExceptionClazz;
    private static final Method setApplicationProtocolsMethod;
    private static final Method getApplicationProtocolMethod;
    private static final Method setDefaultUseCachesMethod;
    private static final Method bootMethod;
    private static final Method configurationMethod;
    private static final Method modulesMethod;
    private static final Method referenceMethod;
    private static final Method locationMethod;
    private static final Method isPresentMethod;
    private static final Method getMethod;
    private static final Constructor<JarFile> jarFileConstructor;
    private static final Method isMultiReleaseMethod;
    private static final Object RUNTIME_VERSION;
    private static final int RUNTIME_MAJOR_VERSION;

    static {
        Class<?> c1 = null;
        Method m2 = null;
        Method m3 = null;
        Method m4 = null;
        Method m5 = null;
        Method m6 = null;
        Method m7 = null;
        Method m8 = null;
        Method m9 = null;
        Method m10 = null;
        Method m11 = null;
        Constructor<JarFile> c12 = null;
        Method m13 = null;
        Object o14 = null;
        Object o15 = null;
        try {
            Class<?> moduleLayerClazz = Class.forName("java.lang.ModuleLayer");
            Class<?> configurationClazz = Class.forName("java.lang.module.Configuration");
            Class<?> resolvedModuleClazz = Class.forName("java.lang.module.ResolvedModule");
            Class<?> moduleReferenceClazz = Class.forName("java.lang.module.ModuleReference");
            Class<?> optionalClazz = Class.forName("java.util.Optional");
            Class<?> versionClazz = Class.forName("java.lang.Runtime$Version");
            Method runtimeVersionMethod = JarFile.class.getMethod("runtimeVersion", new Class[0]);
            Method majorMethod = versionClazz.getMethod("major", new Class[0]);
            c1 = Class.forName("java.lang.reflect.InaccessibleObjectException");
            m2 = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            m3 = SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
            m4 = URLConnection.class.getMethod("setDefaultUseCaches", String.class, Boolean.TYPE);
            m5 = moduleLayerClazz.getMethod("boot", new Class[0]);
            m6 = moduleLayerClazz.getMethod("configuration", new Class[0]);
            m7 = configurationClazz.getMethod("modules", new Class[0]);
            m8 = resolvedModuleClazz.getMethod("reference", new Class[0]);
            m9 = moduleReferenceClazz.getMethod("location", new Class[0]);
            m10 = optionalClazz.getMethod("isPresent", new Class[0]);
            m11 = optionalClazz.getMethod(BeanUtil.PREFIX_GETTER_GET, new Class[0]);
            c12 = JarFile.class.getConstructor(File.class, Boolean.TYPE, Integer.TYPE, versionClazz);
            m13 = JarFile.class.getMethod("isMultiRelease", new Class[0]);
            o14 = runtimeVersionMethod.invoke(null, new Object[0]);
            o15 = majorMethod.invoke(o14, new Object[0]);
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException | ReflectiveOperationException e2) {
        }
        inaccessibleObjectExceptionClazz = c1;
        setApplicationProtocolsMethod = m2;
        getApplicationProtocolMethod = m3;
        setDefaultUseCachesMethod = m4;
        bootMethod = m5;
        configurationMethod = m6;
        modulesMethod = m7;
        referenceMethod = m8;
        locationMethod = m9;
        isPresentMethod = m10;
        getMethod = m11;
        jarFileConstructor = c12;
        isMultiReleaseMethod = m13;
        RUNTIME_VERSION = o14;
        if (o15 != null) {
            RUNTIME_MAJOR_VERSION = ((Integer) o15).intValue();
        } else {
            RUNTIME_MAJOR_VERSION = 8;
        }
    }

    public static boolean isSupported() {
        return inaccessibleObjectExceptionClazz != null;
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public boolean isInstanceOfInaccessibleObjectException(Throwable t) {
        if (t == null) {
            return false;
        }
        return inaccessibleObjectExceptionClazz.isAssignableFrom(t.getClass());
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public void setApplicationProtocols(SSLParameters sslParameters, String[] protocols) {
        try {
            setApplicationProtocolsMethod.invoke(sslParameters, protocols);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public String getApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String) getApplicationProtocolMethod.invoke(sslEngine, new Object[0]);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public void disableCachingForJarUrlConnections() throws IOException {
        try {
            setDefaultUseCachesMethod.invoke(null, "JAR", Boolean.FALSE);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public void addBootModulePath(Deque<URL> classPathUrlsToProcess) {
        try {
            Object bootLayer = bootMethod.invoke(null, new Object[0]);
            Object bootConfiguration = configurationMethod.invoke(bootLayer, new Object[0]);
            Set<?> resolvedModules = (Set) modulesMethod.invoke(bootConfiguration, new Object[0]);
            for (Object resolvedModule : resolvedModules) {
                Object moduleReference = referenceMethod.invoke(resolvedModule, new Object[0]);
                Object optionalURI = locationMethod.invoke(moduleReference, new Object[0]);
                Boolean isPresent = (Boolean) isPresentMethod.invoke(optionalURI, new Object[0]);
                if (isPresent.booleanValue()) {
                    URI uri = (URI) getMethod.invoke(optionalURI, new Object[0]);
                    try {
                        URL url = uri.toURL();
                        classPathUrlsToProcess.add(url);
                    } catch (MalformedURLException e) {
                        log.warn(sm.getString("jre9Compat.invalidModuleUri", uri), e);
                    }
                }
            }
        } catch (ReflectiveOperationException e2) {
            throw new UnsupportedOperationException(e2);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public JarFile jarFileNewInstance(File f) throws IOException {
        try {
            return jarFileConstructor.newInstance(f, Boolean.TRUE, 1, RUNTIME_VERSION);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public boolean jarFileIsMultiRelease(JarFile jarFile) {
        try {
            return ((Boolean) isMultiReleaseMethod.invoke(jarFile, new Object[0])).booleanValue();
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public int jarFileRuntimeMajorVersion() {
        return RUNTIME_MAJOR_VERSION;
    }
}