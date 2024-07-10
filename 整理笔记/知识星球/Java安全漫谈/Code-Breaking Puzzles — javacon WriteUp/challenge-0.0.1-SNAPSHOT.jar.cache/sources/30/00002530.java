package org.springframework.web.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/MultipartFileResource.class */
class MultipartFileResource extends AbstractResource {
    private final MultipartFile multipartFile;

    public MultipartFileResource(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "MultipartFile must not be null");
        this.multipartFile = multipartFile;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isOpen() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() {
        return this.multipartFile.getSize();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return this.multipartFile.getOriginalFilename();
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException, IllegalStateException {
        return this.multipartFile.getInputStream();
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "MultipartFile resource [" + this.multipartFile.getName() + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof MultipartFileResource) && ((MultipartFileResource) other).multipartFile.equals(this.multipartFile));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.multipartFile.hashCode();
    }
}