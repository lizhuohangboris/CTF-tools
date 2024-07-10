package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/UrlResource.class */
public class UrlResource extends AbstractFileResolvingResource {
    @Nullable
    private final URI uri;
    private final URL url;
    private final URL cleanedUrl;

    public UrlResource(URI uri) throws MalformedURLException {
        Assert.notNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
    }

    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }

    public UrlResource(String path) throws MalformedURLException {
        Assert.notNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
    }

    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
        } catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        String cleanedPath = StringUtils.cleanPath(originalPath);
        if (!cleanedPath.equals(originalPath)) {
            try {
                return new URL(cleanedPath);
            } catch (MalformedURLException e) {
            }
        }
        return originalUrl;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() {
        return this.url;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        if (this.uri != null) {
            return this.uri;
        }
        return super.getURI();
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        if (this.uri != null) {
            return super.isFile(this.uri);
        }
        return super.isFile();
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        if (this.uri != null) {
            return super.getFile(this.uri);
        }
        return super.getFile();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return new UrlResource(new URL(this.url, relativePath));
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return StringUtils.getFilename(this.cleanedUrl.getPath());
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "URL [" + this.url + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof UrlResource) && this.cleanedUrl.equals(((UrlResource) other).cleanedUrl));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.cleanedUrl.hashCode();
    }
}