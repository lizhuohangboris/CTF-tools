package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/DescriptiveResource.class */
public class DescriptiveResource extends AbstractResource {
    private final String description;

    public DescriptiveResource(@Nullable String description) {
        this.description = description != null ? description : "";
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return false;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        return false;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be opened because it does not point to a readable resource");
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return this.description;
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof DescriptiveResource) && ((DescriptiveResource) other).description.equals(this.description));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.description.hashCode();
    }
}