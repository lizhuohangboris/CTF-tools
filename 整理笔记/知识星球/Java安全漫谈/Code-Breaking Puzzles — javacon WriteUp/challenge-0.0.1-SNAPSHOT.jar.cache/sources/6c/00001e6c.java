package org.springframework.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ByteArrayResource.class */
public class ByteArrayResource extends AbstractResource {
    private final byte[] byteArray;
    private final String description;

    public ByteArrayResource(byte[] byteArray) {
        this(byteArray, "resource loaded from byte array");
    }

    public ByteArrayResource(byte[] byteArray, @Nullable String description) {
        Assert.notNull(byteArray, "Byte array must not be null");
        this.byteArray = byteArray;
        this.description = description != null ? description : "";
    }

    public final byte[] getByteArray() {
        return this.byteArray;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() {
        return this.byteArray.length;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.byteArray);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "Byte array resource [" + this.description + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof ByteArrayResource) && Arrays.equals(((ByteArrayResource) other).byteArray, this.byteArray));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return byte[].class.hashCode() * 29 * this.byteArray.length;
    }
}