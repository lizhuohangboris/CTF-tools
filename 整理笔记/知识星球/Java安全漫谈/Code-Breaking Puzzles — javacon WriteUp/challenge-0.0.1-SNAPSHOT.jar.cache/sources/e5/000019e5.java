package org.springframework.boot.loader;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/Launcher.class */
public abstract class Launcher {
    protected abstract String getMainClass() throws Exception;

    protected abstract List<Archive> getClassPathArchives() throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public void launch(String[] args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        ClassLoader classLoader = createClassLoader(getClassPathArchives());
        launch(args, getMainClass(), classLoader);
    }

    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(archives.size());
        for (Archive archive : archives) {
            urls.add(archive.getUrl());
        }
        return createClassLoader((URL[]) urls.toArray(new URL[0]));
    }

    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new LaunchedURLClassLoader(urls, getClass().getClassLoader());
    }

    protected void launch(String[] args, String mainClass, ClassLoader classLoader) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        createMainMethodRunner(mainClass, args, classLoader).run();
    }

    protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
        return new MainMethodRunner(mainClass, args);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource != null ? codeSource.getLocation().toURI() : null;
        String path = location != null ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (root.exists()) {
            return root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root);
        }
        throw new IllegalStateException("Unable to determine code source archive from " + root);
    }
}