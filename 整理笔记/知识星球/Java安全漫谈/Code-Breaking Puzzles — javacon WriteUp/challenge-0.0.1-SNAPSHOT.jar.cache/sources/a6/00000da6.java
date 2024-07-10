package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/scan/JarFileUrlNestedJar.class */
public class JarFileUrlNestedJar extends AbstractInputStreamJar {
    private final JarFile warFile;
    private final JarEntry jarEntry;

    public JarFileUrlNestedJar(URL url) throws IOException {
        super(url);
        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
        jarConn.setUseCaches(false);
        this.warFile = jarConn.getJarFile();
        String urlAsString = url.toString();
        int pathStart = urlAsString.indexOf(ResourceUtils.JAR_URL_SEPARATOR) + 2;
        String jarPath = urlAsString.substring(pathStart);
        this.jarEntry = this.warFile.getJarEntry(jarPath);
    }

    @Override // org.apache.tomcat.Jar, java.lang.AutoCloseable
    public void close() {
        closeStream();
        if (this.warFile != null) {
            try {
                this.warFile.close();
            } catch (IOException e) {
            }
        }
    }

    @Override // org.apache.tomcat.util.scan.AbstractInputStreamJar
    protected NonClosingJarInputStream createJarInputStream() throws IOException {
        return new NonClosingJarInputStream(this.warFile.getInputStream(this.jarEntry));
    }
}