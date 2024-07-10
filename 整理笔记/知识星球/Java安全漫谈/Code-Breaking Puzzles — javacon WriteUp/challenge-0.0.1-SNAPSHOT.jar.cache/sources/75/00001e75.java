package org.springframework.core.io;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/FileSystemResourceLoader.class */
public class FileSystemResourceLoader extends DefaultResourceLoader {
    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemContextResource(path);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/FileSystemResourceLoader$FileSystemContextResource.class */
    private static class FileSystemContextResource extends FileSystemResource implements ContextResource {
        public FileSystemContextResource(String path) {
            super(path);
        }

        @Override // org.springframework.core.io.ContextResource
        public String getPathWithinContext() {
            return getPath();
        }
    }
}