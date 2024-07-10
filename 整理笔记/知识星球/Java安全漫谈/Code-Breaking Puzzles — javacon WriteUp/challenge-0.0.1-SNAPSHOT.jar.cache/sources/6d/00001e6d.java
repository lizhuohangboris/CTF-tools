package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ClassPathResource.class */
public class ClassPathResource extends AbstractFileResolvingResource {
    private final String path;
    @Nullable
    private ClassLoader classLoader;
    @Nullable
    private Class<?> clazz;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
        Assert.notNull(path, "Path must not be null");
        String pathToUse = StringUtils.cleanPath(path);
        this.path = pathToUse.startsWith("/") ? pathToUse.substring(1) : pathToUse;
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    public ClassPathResource(String path, @Nullable Class<?> clazz) {
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.clazz = clazz;
    }

    @Deprecated
    protected ClassPathResource(String path, @Nullable ClassLoader classLoader, @Nullable Class<?> clazz) {
        this.path = StringUtils.cleanPath(path);
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    public final String getPath() {
        return this.path;
    }

    @Nullable
    public final ClassLoader getClassLoader() {
        return this.clazz != null ? this.clazz.getClassLoader() : this.classLoader;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return resolveURL() != null;
    }

    @Nullable
    protected URL resolveURL() {
        if (this.clazz != null) {
            return this.clazz.getResource(this.path);
        }
        if (this.classLoader != null) {
            return this.classLoader.getResource(this.path);
        }
        return ClassLoader.getSystemResource(this.path);
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.path);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.path);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return this.clazz != null ? new ClassPathResource(pathToUse, this.clazz) : new ClassPathResource(pathToUse, this.classLoader);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        String pathToUse = this.path;
        if (this.clazz != null && !pathToUse.startsWith("/")) {
            builder.append(ClassUtils.classPackageAsResourcePath(this.clazz));
            builder.append('/');
        }
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        builder.append(pathToUse);
        builder.append(']');
        return builder.toString();
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClassPathResource)) {
            return false;
        }
        ClassPathResource otherRes = (ClassPathResource) other;
        return this.path.equals(otherRes.path) && ObjectUtils.nullSafeEquals(this.classLoader, otherRes.classLoader) && ObjectUtils.nullSafeEquals(this.clazz, otherRes.clazz);
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.path.hashCode();
    }
}