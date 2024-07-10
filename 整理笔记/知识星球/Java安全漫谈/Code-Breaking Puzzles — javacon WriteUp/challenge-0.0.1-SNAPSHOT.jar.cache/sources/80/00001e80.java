package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.springframework.core.NestedIOException;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/VfsResource.class */
public class VfsResource extends AbstractResource {
    private final Object resource;

    public VfsResource(Object resource) {
        Assert.notNull(resource, "VirtualFile must not be null");
        this.resource = resource;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        return VfsUtils.getInputStream(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return VfsUtils.exists(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        return VfsUtils.isReadable(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        try {
            return VfsUtils.getURL(this.resource);
        } catch (Exception ex) {
            throw new NestedIOException("Failed to obtain URL for file " + this.resource, ex);
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        try {
            return VfsUtils.getURI(this.resource);
        } catch (Exception ex) {
            throw new NestedIOException("Failed to obtain URI for " + this.resource, ex);
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        return VfsUtils.getFile(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        return VfsUtils.getSize(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        return VfsUtils.getLastModified(this.resource);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws IOException {
        if (!relativePath.startsWith(".") && relativePath.contains("/")) {
            try {
                return new VfsResource(VfsUtils.getChild(this.resource, relativePath));
            } catch (IOException e) {
            }
        }
        return new VfsResource(VfsUtils.getRelative(new URL(getURL(), relativePath)));
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return VfsUtils.getName(this.resource);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "VFS resource [" + this.resource + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof VfsResource) && this.resource.equals(((VfsResource) other).resource));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.resource.hashCode();
    }
}