package org.springframework.boot.web.embedded.tomcat;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TomcatEmbeddedWebappClassLoader.class */
public class TomcatEmbeddedWebappClassLoader extends ParallelWebappClassLoader {
    private static final Log logger = LogFactory.getLog(TomcatEmbeddedWebappClassLoader.class);

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public TomcatEmbeddedWebappClassLoader() {
    }

    public TomcatEmbeddedWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override // org.apache.catalina.loader.WebappClassLoaderBase, java.net.URLClassLoader, java.lang.ClassLoader
    public URL findResource(String name) {
        return null;
    }

    @Override // org.apache.catalina.loader.WebappClassLoaderBase, java.net.URLClassLoader, java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        return Collections.emptyEnumeration();
    }

    @Override // org.apache.catalina.loader.WebappClassLoaderBase, java.lang.ClassLoader
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> resolveIfNecessary;
        synchronized (getClassLoadingLock(name)) {
            Class<?> result = findExistingLoadedClass(name);
            Class<?> result2 = result != null ? result : doLoadClass(name);
            if (result2 == null) {
                throw new ClassNotFoundException(name);
            }
            resolveIfNecessary = resolveIfNecessary(result2, resolve);
        }
        return resolveIfNecessary;
    }

    private Class<?> findExistingLoadedClass(String name) {
        Class<?> resultClass = findLoadedClass0(name);
        return resultClass != null ? resultClass : findLoadedClass(name);
    }

    private Class<?> doLoadClass(String name) throws ClassNotFoundException {
        checkPackageAccess(name);
        if (this.delegate || filter(name, true)) {
            Class<?> result = loadFromParent(name);
            return result != null ? result : findClassIgnoringNotFound(name);
        }
        Class<?> result2 = findClassIgnoringNotFound(name);
        return result2 != null ? result2 : loadFromParent(name);
    }

    private Class<?> resolveIfNecessary(Class<?> resultClass, boolean resolve) {
        if (resolve) {
            resolveClass(resultClass);
        }
        return resultClass;
    }

    @Override // org.apache.catalina.loader.WebappClassLoaderBase, java.net.URLClassLoader
    protected void addURL(URL url) {
        if (logger.isTraceEnabled()) {
            logger.trace("Ignoring request to add " + url + " to the tomcat classloader");
        }
    }

    private Class<?> loadFromParent(String name) {
        if (this.parent == null) {
            return null;
        }
        try {
            return Class.forName(name, false, this.parent);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Class<?> findClassIgnoringNotFound(String name) {
        try {
            return findClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void checkPackageAccess(String name) throws ClassNotFoundException {
        if (this.securityManager != null && name.lastIndexOf(46) >= 0) {
            try {
                this.securityManager.checkPackageAccess(name.substring(0, name.lastIndexOf(46)));
            } catch (SecurityException ex) {
                throw new ClassNotFoundException("Security Violation, attempt to use Restricted Class: " + name, ex);
            }
        }
    }
}