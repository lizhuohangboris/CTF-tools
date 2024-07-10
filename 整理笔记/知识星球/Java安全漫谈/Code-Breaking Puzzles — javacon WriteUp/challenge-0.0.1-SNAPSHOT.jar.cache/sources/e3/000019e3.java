package org.springframework.boot.loader;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/LaunchedURLClassLoader.class */
public class LaunchedURLClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public LaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public URL findResource(String name) {
        Handler.setUseFastConnectionExceptions(true);
        try {
            URL findResource = super.findResource(name);
            Handler.setUseFastConnectionExceptions(false);
            return findResource;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        Handler.setUseFastConnectionExceptions(true);
        try {
            UseFastConnectionExceptionsEnumeration useFastConnectionExceptionsEnumeration = new UseFastConnectionExceptionsEnumeration(super.findResources(name));
            Handler.setUseFastConnectionExceptions(false);
            return useFastConnectionExceptionsEnumeration;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    @Override // java.lang.ClassLoader
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Handler.setUseFastConnectionExceptions(true);
        try {
            try {
                definePackageIfNecessary(name);
            } catch (IllegalArgumentException e) {
                if (getPackage(name) == null) {
                    throw new AssertionError("Package " + name + " has already been defined but it could not be found");
                }
            }
            Class<?> loadClass = super.loadClass(name, resolve);
            Handler.setUseFastConnectionExceptions(false);
            return loadClass;
        } catch (Throwable th) {
            Handler.setUseFastConnectionExceptions(false);
            throw th;
        }
    }

    private void definePackageIfNecessary(String className) {
        int lastDot = className.lastIndexOf(46);
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (getPackage(packageName) == null) {
                try {
                    definePackage(className, packageName);
                } catch (IllegalArgumentException e) {
                    if (getPackage(packageName) == null) {
                        throw new AssertionError("Package " + packageName + " has already been defined but it could not be found");
                    }
                }
            }
        }
    }

    private void definePackage(String className, String packageName) {
        try {
            AccessController.doPrivileged(() -> {
                URL[] uRLs;
                String packageEntryName = packageName.replace('.', '/') + "/";
                String classEntryName = className.replace('.', '/') + ClassUtils.CLASS_FILE_SUFFIX;
                for (URL url : getURLs()) {
                    try {
                        URLConnection connection = url.openConnection();
                        if (connection instanceof JarURLConnection) {
                            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                            if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null && jarFile.getManifest() != null) {
                                definePackage(packageName, jarFile.getManifest(), url);
                                return null;
                            }
                        } else {
                            continue;
                        }
                    } catch (IOException e) {
                    }
                }
                return null;
            }, AccessController.getContext());
        } catch (PrivilegedActionException e) {
        }
    }

    public void clearCache() {
        URL[] uRLs;
        for (URL url : getURLs()) {
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    clearCache(connection);
                }
            } catch (IOException e) {
            }
        }
    }

    private void clearCache(URLConnection connection) throws IOException {
        Object jarFile = ((JarURLConnection) connection).getJarFile();
        if (jarFile instanceof org.springframework.boot.loader.jar.JarFile) {
            ((org.springframework.boot.loader.jar.JarFile) jarFile).clearCache();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/LaunchedURLClassLoader$UseFastConnectionExceptionsEnumeration.class */
    private static class UseFastConnectionExceptionsEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> delegate;

        UseFastConnectionExceptionsEnumeration(Enumeration<URL> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            Handler.setUseFastConnectionExceptions(true);
            try {
                boolean hasMoreElements = this.delegate.hasMoreElements();
                Handler.setUseFastConnectionExceptions(false);
                return hasMoreElements;
            } catch (Throwable th) {
                Handler.setUseFastConnectionExceptions(false);
                throw th;
            }
        }

        @Override // java.util.Enumeration
        public URL nextElement() {
            Handler.setUseFastConnectionExceptions(true);
            try {
                URL nextElement = this.delegate.nextElement();
                Handler.setUseFastConnectionExceptions(false);
                return nextElement;
            } catch (Throwable th) {
                Handler.setUseFastConnectionExceptions(false);
                throw th;
            }
        }
    }
}