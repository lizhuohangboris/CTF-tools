package org.apache.tomcat.util.descriptor.tld;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.scan.JarFactory;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldResourcePath.class */
public class TldResourcePath {
    private final URL url;
    private final String webappPath;
    private final String entryName;

    public TldResourcePath(URL url, String webappPath) {
        this(url, webappPath, null);
    }

    public TldResourcePath(URL url, String webappPath, String entryName) {
        this.url = url;
        this.webappPath = webappPath;
        this.entryName = entryName;
    }

    public URL getUrl() {
        return this.url;
    }

    public String getWebappPath() {
        return this.webappPath;
    }

    public String getEntryName() {
        return this.entryName;
    }

    public String toExternalForm() {
        if (this.entryName == null) {
            return this.url.toExternalForm();
        }
        return ResourceUtils.JAR_URL_PREFIX + this.url.toExternalForm() + ResourceUtils.JAR_URL_SEPARATOR + this.entryName;
    }

    public InputStream openStream() throws IOException {
        if (this.entryName == null) {
            return this.url.openStream();
        }
        URL entryUrl = JarFactory.getJarEntryURL(this.url, this.entryName);
        return entryUrl.openStream();
    }

    public Jar openJar() throws IOException {
        if (this.entryName == null) {
            return null;
        }
        return JarFactory.newInstance(this.url);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TldResourcePath other = (TldResourcePath) o;
        return this.url.equals(other.url) && Objects.equals(this.webappPath, other.webappPath) && Objects.equals(this.entryName, other.entryName);
    }

    public int hashCode() {
        int result = this.url.hashCode();
        return (((result * 31) + Objects.hashCode(this.webappPath)) * 31) + Objects.hashCode(this.entryName);
    }
}