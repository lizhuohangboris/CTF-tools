package org.springframework.core.type.classreading;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/SimpleMetadataReaderFactory.class */
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {
    private final ResourceLoader resourceLoader;

    public SimpleMetadataReaderFactory() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public SimpleMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
    }

    public SimpleMetadataReaderFactory(@Nullable ClassLoader classLoader) {
        this.resourceLoader = classLoader != null ? new DefaultResourceLoader(classLoader) : new DefaultResourceLoader();
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    @Override // org.springframework.core.type.classreading.MetadataReaderFactory
    public MetadataReader getMetadataReader(String className) throws IOException {
        try {
            String resourcePath = "classpath:" + ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
            Resource resource = this.resourceLoader.getResource(resourcePath);
            return getMetadataReader(resource);
        } catch (FileNotFoundException ex) {
            int lastDotIndex = className.lastIndexOf(46);
            if (lastDotIndex != -1) {
                String innerClassName = className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1);
                String innerClassResourcePath = "classpath:" + ClassUtils.convertClassNameToResourcePath(innerClassName) + ClassUtils.CLASS_FILE_SUFFIX;
                Resource innerClassResource = this.resourceLoader.getResource(innerClassResourcePath);
                if (innerClassResource.exists()) {
                    return getMetadataReader(innerClassResource);
                }
            }
            throw ex;
        }
    }

    @Override // org.springframework.core.type.classreading.MetadataReaderFactory
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }
}