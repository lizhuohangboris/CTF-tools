package org.springframework.web.servlet.resource;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/TransformedResource.class */
public class TransformedResource extends ByteArrayResource {
    @Nullable
    private final String filename;
    private final long lastModified;

    public TransformedResource(Resource original, byte[] transformedContent) {
        super(transformedContent);
        this.filename = original.getFilename();
        try {
            this.lastModified = original.lastModified();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return this.filename;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        return this.lastModified;
    }
}