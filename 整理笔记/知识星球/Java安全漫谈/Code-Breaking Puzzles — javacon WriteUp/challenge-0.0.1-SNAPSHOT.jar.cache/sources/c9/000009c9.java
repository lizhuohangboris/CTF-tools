package org.apache.catalina.webresources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/CachedResource.class */
public class CachedResource implements WebResource {
    private static final long CACHE_ENTRY_SIZE = 500;
    private final Cache cache;
    private final StandardRoot root;
    private final String webAppPath;
    private final long ttl;
    private final int objectMaxSizeBytes;
    private final boolean usesClassLoaderResources;
    private volatile WebResource webResource;
    private volatile WebResource[] webResources;
    private volatile long nextCheck;
    private volatile Long cachedLastModified = null;
    private volatile String cachedLastModifiedHttp = null;
    private volatile byte[] cachedContent = null;
    private volatile Boolean cachedIsFile = null;
    private volatile Boolean cachedIsDirectory = null;
    private volatile Boolean cachedExists = null;
    private volatile Boolean cachedIsVirtual = null;
    private volatile Long cachedContentLength = null;

    public CachedResource(Cache cache, StandardRoot root, String path, long ttl, int objectMaxSizeBytes, boolean usesClassLoaderResources) {
        this.cache = cache;
        this.root = root;
        this.webAppPath = path;
        this.ttl = ttl;
        this.objectMaxSizeBytes = objectMaxSizeBytes;
        this.usesClassLoaderResources = usesClassLoaderResources;
    }

    public boolean validateResource(boolean useClassLoaderResources) {
        if (this.usesClassLoaderResources != useClassLoaderResources) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.webResource == null) {
            synchronized (this) {
                if (this.webResource == null) {
                    this.webResource = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
                    getLastModified();
                    getContentLength();
                    this.nextCheck = this.ttl + now;
                    if (this.webResource instanceof EmptyResource) {
                        this.cachedExists = Boolean.FALSE;
                    } else {
                        this.cachedExists = Boolean.TRUE;
                    }
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (!this.root.isPackedWarFile()) {
            WebResource webResourceInternal = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
            if ((!this.webResource.exists() && webResourceInternal.exists()) || this.webResource.getLastModified() != getLastModified() || this.webResource.getContentLength() != getContentLength() || this.webResource.getLastModified() != webResourceInternal.getLastModified() || this.webResource.getContentLength() != webResourceInternal.getContentLength()) {
                return false;
            }
        }
        this.nextCheck = this.ttl + now;
        return true;
    }

    public boolean validateResources(boolean useClassLoaderResources) {
        long now = System.currentTimeMillis();
        if (this.webResources == null) {
            synchronized (this) {
                if (this.webResources == null) {
                    this.webResources = this.root.getResourcesInternal(this.webAppPath, useClassLoaderResources);
                    this.nextCheck = this.ttl + now;
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (this.root.isPackedWarFile()) {
            this.nextCheck = this.ttl + now;
            return true;
        }
        return false;
    }

    public long getNextCheck() {
        return this.nextCheck;
    }

    @Override // org.apache.catalina.WebResource
    public long getLastModified() {
        Long cachedLastModified = this.cachedLastModified;
        if (cachedLastModified == null) {
            cachedLastModified = Long.valueOf(this.webResource.getLastModified());
            this.cachedLastModified = cachedLastModified;
        }
        return cachedLastModified.longValue();
    }

    @Override // org.apache.catalina.WebResource
    public String getLastModifiedHttp() {
        String cachedLastModifiedHttp = this.cachedLastModifiedHttp;
        if (cachedLastModifiedHttp == null) {
            cachedLastModifiedHttp = this.webResource.getLastModifiedHttp();
            this.cachedLastModifiedHttp = cachedLastModifiedHttp;
        }
        return cachedLastModifiedHttp;
    }

    @Override // org.apache.catalina.WebResource
    public boolean exists() {
        Boolean cachedExists = this.cachedExists;
        if (cachedExists == null) {
            cachedExists = Boolean.valueOf(this.webResource.exists());
            this.cachedExists = cachedExists;
        }
        return cachedExists.booleanValue();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isVirtual() {
        Boolean cachedIsVirtual = this.cachedIsVirtual;
        if (cachedIsVirtual == null) {
            cachedIsVirtual = Boolean.valueOf(this.webResource.isVirtual());
            this.cachedIsVirtual = cachedIsVirtual;
        }
        return cachedIsVirtual.booleanValue();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isDirectory() {
        Boolean cachedIsDirectory = this.cachedIsDirectory;
        if (cachedIsDirectory == null) {
            cachedIsDirectory = Boolean.valueOf(this.webResource.isDirectory());
            this.cachedIsDirectory = cachedIsDirectory;
        }
        return cachedIsDirectory.booleanValue();
    }

    @Override // org.apache.catalina.WebResource
    public boolean isFile() {
        Boolean cachedIsFile = this.cachedIsFile;
        if (cachedIsFile == null) {
            cachedIsFile = Boolean.valueOf(this.webResource.isFile());
            this.cachedIsFile = cachedIsFile;
        }
        return cachedIsFile.booleanValue();
    }

    @Override // org.apache.catalina.WebResource
    public boolean delete() {
        boolean deleteResult = this.webResource.delete();
        if (deleteResult) {
            this.cache.removeCacheEntry(this.webAppPath);
        }
        return deleteResult;
    }

    @Override // org.apache.catalina.WebResource
    public String getName() {
        return this.webResource.getName();
    }

    @Override // org.apache.catalina.WebResource
    public long getContentLength() {
        Long cachedContentLength = this.cachedContentLength;
        if (cachedContentLength == null) {
            long result = 0;
            if (this.webResource != null) {
                result = this.webResource.getContentLength();
                this.cachedContentLength = Long.valueOf(result);
            }
            return result;
        }
        return cachedContentLength.longValue();
    }

    @Override // org.apache.catalina.WebResource
    public String getCanonicalPath() {
        return this.webResource.getCanonicalPath();
    }

    @Override // org.apache.catalina.WebResource
    public boolean canRead() {
        return this.webResource.canRead();
    }

    @Override // org.apache.catalina.WebResource
    public String getWebappPath() {
        return this.webAppPath;
    }

    @Override // org.apache.catalina.WebResource
    public String getETag() {
        return this.webResource.getETag();
    }

    @Override // org.apache.catalina.WebResource
    public void setMimeType(String mimeType) {
        this.webResource.setMimeType(mimeType);
    }

    @Override // org.apache.catalina.WebResource
    public String getMimeType() {
        return this.webResource.getMimeType();
    }

    @Override // org.apache.catalina.WebResource
    public InputStream getInputStream() {
        byte[] content = getContent();
        if (content == null) {
            return this.webResource.getInputStream();
        }
        return new ByteArrayInputStream(content);
    }

    @Override // org.apache.catalina.WebResource
    public byte[] getContent() {
        byte[] cachedContent = this.cachedContent;
        if (cachedContent == null) {
            if (getContentLength() > this.objectMaxSizeBytes) {
                return null;
            }
            cachedContent = this.webResource.getContent();
            this.cachedContent = cachedContent;
        }
        return cachedContent;
    }

    @Override // org.apache.catalina.WebResource
    public long getCreation() {
        return this.webResource.getCreation();
    }

    @Override // org.apache.catalina.WebResource
    public URL getURL() {
        return this.webResource.getURL();
    }

    @Override // org.apache.catalina.WebResource
    public URL getCodeBase() {
        return this.webResource.getCodeBase();
    }

    @Override // org.apache.catalina.WebResource
    public Certificate[] getCertificates() {
        return this.webResource.getCertificates();
    }

    @Override // org.apache.catalina.WebResource
    public Manifest getManifest() {
        return this.webResource.getManifest();
    }

    @Override // org.apache.catalina.WebResource
    public WebResourceRoot getWebResourceRoot() {
        return this.webResource.getWebResourceRoot();
    }

    WebResource getWebResource() {
        return this.webResource;
    }

    public WebResource[] getWebResources() {
        return this.webResources;
    }

    public long getSize() {
        long result = 500;
        if (getContentLength() <= this.objectMaxSizeBytes) {
            result = CACHE_ENTRY_SIZE + getContentLength();
        }
        return result;
    }
}