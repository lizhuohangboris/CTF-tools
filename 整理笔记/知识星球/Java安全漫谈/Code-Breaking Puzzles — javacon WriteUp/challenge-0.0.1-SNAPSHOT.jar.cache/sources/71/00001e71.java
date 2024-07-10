package org.springframework.core.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/DefaultResourceLoader.class */
public class DefaultResourceLoader implements ResourceLoader {
    @Nullable
    private ClassLoader classLoader;
    private final Set<ProtocolResolver> protocolResolvers;
    private final Map<Class<?>, Map<Resource, ?>> resourceCaches;

    public DefaultResourceLoader() {
        this.protocolResolvers = new LinkedHashSet(4);
        this.resourceCaches = new ConcurrentHashMap(4);
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
        this.protocolResolvers = new LinkedHashSet(4);
        this.resourceCaches = new ConcurrentHashMap(4);
        this.classLoader = classLoader;
    }

    public void setClassLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.core.io.ResourceLoader
    @Nullable
    public ClassLoader getClassLoader() {
        return this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader();
    }

    public void addProtocolResolver(ProtocolResolver resolver) {
        Assert.notNull(resolver, "ProtocolResolver must not be null");
        this.protocolResolvers.add(resolver);
    }

    public Collection<ProtocolResolver> getProtocolResolvers() {
        return this.protocolResolvers;
    }

    public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
        return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> {
            return new ConcurrentHashMap();
        });
    }

    public void clearResourceCaches() {
        this.resourceCaches.clear();
    }

    @Override // org.springframework.core.io.ResourceLoader
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        for (ProtocolResolver protocolResolver : this.protocolResolvers) {
            Resource resource = protocolResolver.resolve(location, this);
            if (resource != null) {
                return resource;
            }
        }
        if (location.startsWith("/")) {
            return getResourceByPath(location);
        }
        if (location.startsWith("classpath:")) {
            return new ClassPathResource(location.substring("classpath:".length()), getClassLoader());
        }
        try {
            URL url = new URL(location);
            return ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url);
        } catch (MalformedURLException e) {
            return getResourceByPath(location);
        }
    }

    protected Resource getResourceByPath(String path) {
        return new ClassPathContextResource(path, getClassLoader());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/DefaultResourceLoader$ClassPathContextResource.class */
    public static class ClassPathContextResource extends ClassPathResource implements ContextResource {
        public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
            super(path, classLoader);
        }

        @Override // org.springframework.core.io.ContextResource
        public String getPathWithinContext() {
            return getPath();
        }

        @Override // org.springframework.core.io.ClassPathResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public Resource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassPathContextResource(pathToUse, getClassLoader());
        }
    }
}