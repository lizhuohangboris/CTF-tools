package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/server/StaticResourceJars.class */
class StaticResourceJars {
    /* JADX INFO: Access modifiers changed from: package-private */
    public List<URL> getUrls() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return getUrlsFrom(((URLClassLoader) classLoader).getURLs());
        }
        return getUrlsFrom((URL[]) Stream.of((Object[]) ManagementFactory.getRuntimeMXBean().getClassPath().split(File.pathSeparator)).map(this::toUrl).toArray(x$0 -> {
            return new URL[x$0];
        }));
    }

    List<URL> getUrlsFrom(URL... urls) {
        List<URL> resourceJarUrls = new ArrayList<>();
        for (URL url : urls) {
            addUrl(resourceJarUrls, url);
        }
        return resourceJarUrls;
    }

    private URL toUrl(String classPathEntry) {
        try {
            return new File(classPathEntry).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("URL could not be created from '" + classPathEntry + "'", ex);
        }
    }

    private File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch (IllegalArgumentException e) {
            return null;
        } catch (URISyntaxException e2) {
            throw new IllegalStateException("Failed to create File from URL '" + url + "'");
        }
    }

    private void addUrl(List<URL> urls, URL url) {
        try {
            if (!"file".equals(url.getProtocol())) {
                addUrlConnection(urls, url, url.openConnection());
            } else {
                File file = toFile(url);
                if (file != null) {
                    addUrlFile(urls, url, file);
                } else {
                    addUrlConnection(urls, url, url.openConnection());
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void addUrlFile(List<URL> urls, URL url, File file) {
        if ((file.isDirectory() && new File(file, "META-INF/resources").isDirectory()) || isResourcesJar(file)) {
            urls.add(url);
        }
    }

    private void addUrlConnection(List<URL> urls, URL url, URLConnection connection) {
        if ((connection instanceof JarURLConnection) && isResourcesJar((JarURLConnection) connection)) {
            urls.add(url);
        }
    }

    private boolean isResourcesJar(JarURLConnection connection) {
        try {
            return isResourcesJar(connection.getJarFile());
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isResourcesJar(File file) {
        try {
            return isResourcesJar(new JarFile(file));
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isResourcesJar(JarFile jar) throws IOException {
        boolean z;
        try {
            if (jar.getName().endsWith(".jar")) {
                if (jar.getJarEntry("META-INF/resources") != null) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            jar.close();
        }
    }
}