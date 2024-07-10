package org.apache.catalina.webresources.war;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import org.apache.tomcat.util.buf.UriUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/war/WarURLConnection.class */
public class WarURLConnection extends URLConnection {
    private final URLConnection wrappedJarUrlConnection;
    private boolean connected;

    /* JADX INFO: Access modifiers changed from: protected */
    public WarURLConnection(URL url) throws IOException {
        super(url);
        URL innerJarUrl = UriUtil.warToJar(url);
        this.wrappedJarUrlConnection = innerJarUrl.openConnection();
    }

    @Override // java.net.URLConnection
    public void connect() throws IOException {
        if (!this.connected) {
            this.wrappedJarUrlConnection.connect();
            this.connected = true;
        }
    }

    @Override // java.net.URLConnection
    public InputStream getInputStream() throws IOException {
        connect();
        return this.wrappedJarUrlConnection.getInputStream();
    }

    @Override // java.net.URLConnection
    public Permission getPermission() throws IOException {
        return this.wrappedJarUrlConnection.getPermission();
    }

    @Override // java.net.URLConnection
    public long getLastModified() {
        return this.wrappedJarUrlConnection.getLastModified();
    }
}