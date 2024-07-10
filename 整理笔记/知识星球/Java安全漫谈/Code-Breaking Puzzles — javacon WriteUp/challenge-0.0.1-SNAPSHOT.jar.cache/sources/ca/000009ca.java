package org.apache.catalina.webresources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/ClasspathURLStreamHandler.class */
public class ClasspathURLStreamHandler extends URLStreamHandler {
    private static final StringManager sm = StringManager.getManager(ClasspathURLStreamHandler.class);

    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL u) throws IOException {
        String path = u.getPath();
        URL classpathUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (classpathUrl == null) {
            classpathUrl = ClasspathURLStreamHandler.class.getResource(path);
        }
        if (classpathUrl == null) {
            throw new FileNotFoundException(sm.getString("classpathUrlStreamHandler.notFound", u));
        }
        return classpathUrl.openConnection();
    }
}