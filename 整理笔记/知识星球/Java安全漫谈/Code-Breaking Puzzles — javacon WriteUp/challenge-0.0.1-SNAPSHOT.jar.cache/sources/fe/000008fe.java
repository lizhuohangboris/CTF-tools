package org.apache.catalina.startup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ClassLoaderFactory.class */
public final class ClassLoaderFactory {
    private static final Log log = LogFactory.getLog(ClassLoaderFactory.class);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ClassLoaderFactory$RepositoryType.class */
    public enum RepositoryType {
        DIR,
        GLOB,
        JAR,
        URL
    }

    public static ClassLoader createClassLoader(File[] unpacked, File[] packed, final ClassLoader parent) throws Exception {
        String[] filenames;
        if (log.isDebugEnabled()) {
            log.debug("Creating new class loader");
        }
        Set<URL> set = new LinkedHashSet<>();
        if (unpacked != null) {
            for (File file : unpacked) {
                if (file.canRead()) {
                    URL url = new File(file.getCanonicalPath() + File.separator).toURI().toURL();
                    if (log.isDebugEnabled()) {
                        log.debug("  Including directory " + url);
                    }
                    set.add(url);
                }
            }
        }
        if (packed != null) {
            for (File directory : packed) {
                if (directory.isDirectory() && directory.canRead() && (filenames = directory.list()) != null) {
                    for (int j = 0; j < filenames.length; j++) {
                        String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                        if (filename.endsWith(".jar")) {
                            File file2 = new File(directory, filenames[j]);
                            if (log.isDebugEnabled()) {
                                log.debug("  Including jar file " + file2.getAbsolutePath());
                            }
                            set.add(file2.toURI().toURL());
                        }
                    }
                }
            }
        }
        final URL[] array = (URL[]) set.toArray(new URL[set.size()]);
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() { // from class: org.apache.catalina.startup.ClassLoaderFactory.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public URLClassLoader run() {
                if (parent == null) {
                    return new URLClassLoader(array);
                }
                return new URLClassLoader(array, parent);
            }
        });
    }

    public static ClassLoader createClassLoader(List<Repository> repositories, final ClassLoader parent) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Creating new class loader");
        }
        Set<URL> set = new LinkedHashSet<>();
        if (repositories != null) {
            for (Repository repository : repositories) {
                if (repository.getType() == RepositoryType.URL) {
                    URL url = buildClassLoaderUrl(repository.getLocation());
                    if (log.isDebugEnabled()) {
                        log.debug("  Including URL " + url);
                    }
                    set.add(url);
                } else if (repository.getType() == RepositoryType.DIR) {
                    File directory = new File(repository.getLocation()).getCanonicalFile();
                    if (validateFile(directory, RepositoryType.DIR)) {
                        URL url2 = buildClassLoaderUrl(directory);
                        if (log.isDebugEnabled()) {
                            log.debug("  Including directory " + url2);
                        }
                        set.add(url2);
                    }
                } else if (repository.getType() == RepositoryType.JAR) {
                    File file = new File(repository.getLocation()).getCanonicalFile();
                    if (validateFile(file, RepositoryType.JAR)) {
                        URL url3 = buildClassLoaderUrl(file);
                        if (log.isDebugEnabled()) {
                            log.debug("  Including jar file " + url3);
                        }
                        set.add(url3);
                    }
                } else if (repository.getType() == RepositoryType.GLOB) {
                    File directory2 = new File(repository.getLocation()).getCanonicalFile();
                    if (validateFile(directory2, RepositoryType.GLOB)) {
                        if (log.isDebugEnabled()) {
                            log.debug("  Including directory glob " + directory2.getAbsolutePath());
                        }
                        String[] filenames = directory2.list();
                        if (filenames != null) {
                            for (int j = 0; j < filenames.length; j++) {
                                String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                                if (filename.endsWith(".jar")) {
                                    File file2 = new File(directory2, filenames[j]).getCanonicalFile();
                                    if (validateFile(file2, RepositoryType.JAR)) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("    Including glob jar file " + file2.getAbsolutePath());
                                        }
                                        set.add(buildClassLoaderUrl(file2));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final URL[] array = (URL[]) set.toArray(new URL[set.size()]);
        if (log.isDebugEnabled()) {
            for (int i = 0; i < array.length; i++) {
                log.debug("  location " + i + " is " + array[i]);
            }
        }
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() { // from class: org.apache.catalina.startup.ClassLoaderFactory.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public URLClassLoader run() {
                if (parent == null) {
                    return new URLClassLoader(array);
                }
                return new URLClassLoader(array, parent);
            }
        });
    }

    private static boolean validateFile(File file, RepositoryType type) throws IOException {
        if (RepositoryType.DIR == type || RepositoryType.GLOB == type) {
            if (!file.isDirectory() || !file.canRead()) {
                String msg = "Problem with directory [" + file + "], exists: [" + file.exists() + "], isDirectory: [" + file.isDirectory() + "], canRead: [" + file.canRead() + "]";
                File home = new File(Bootstrap.getCatalinaHome());
                File home2 = home.getCanonicalFile();
                File base = new File(Bootstrap.getCatalinaBase()).getCanonicalFile();
                File defaultValue = new File(base, "lib");
                if (!home2.getPath().equals(base.getPath()) && file.getPath().equals(defaultValue.getPath()) && !file.exists()) {
                    log.debug(msg);
                    return false;
                }
                log.warn(msg);
                return false;
            }
            return true;
        } else if (RepositoryType.JAR == type && !file.canRead()) {
            log.warn("Problem with JAR file [" + file + "], exists: [" + file.exists() + "], canRead: [" + file.canRead() + "]");
            return false;
        } else {
            return true;
        }
    }

    private static URL buildClassLoaderUrl(String urlString) throws MalformedURLException {
        String result = urlString.replaceAll(ResourceUtils.JAR_URL_SEPARATOR, "%21/");
        return new URL(result);
    }

    private static URL buildClassLoaderUrl(File file) throws MalformedURLException {
        String fileUrlString = file.toURI().toString();
        return new URL(fileUrlString.replaceAll(ResourceUtils.JAR_URL_SEPARATOR, "%21/"));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/ClassLoaderFactory$Repository.class */
    public static class Repository {
        private final String location;
        private final RepositoryType type;

        public Repository(String location, RepositoryType type) {
            this.location = location;
            this.type = type;
        }

        public String getLocation() {
            return this.location;
        }

        public RepositoryType getType() {
            return this.type;
        }
    }
}