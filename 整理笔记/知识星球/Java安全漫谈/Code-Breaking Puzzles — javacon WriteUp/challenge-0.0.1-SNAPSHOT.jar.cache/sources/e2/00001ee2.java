package org.springframework.core.type.classreading;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.asm.ClassReader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/SimpleMetadataReader.class */
public final class SimpleMetadataReader implements MetadataReader {
    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
        InputStream is = new BufferedInputStream(resource.getInputStream());
        try {
            try {
                ClassReader classReader = new ClassReader(is);
                is.close();
                AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
                classReader.accept(visitor, 2);
                this.annotationMetadata = visitor;
                this.classMetadata = visitor;
                this.resource = resource;
            } catch (IllegalArgumentException ex) {
                throw new NestedIOException("ASM ClassReader failed to parse class file - probably due to a new Java class file version that isn't supported yet: " + resource, ex);
            }
        } catch (Throwable th) {
            is.close();
            throw th;
        }
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public Resource getResource() {
        return this.resource;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public ClassMetadata getClassMetadata() {
        return this.classMetadata;
    }

    @Override // org.springframework.core.type.classreading.MetadataReader
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}