package org.springframework.context.annotation;

import java.util.Objects;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/DeferredImportSelector.class */
public interface DeferredImportSelector extends ImportSelector {
    @Nullable
    default Class<? extends Group> getImportGroup() {
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/DeferredImportSelector$Group.class */
    public interface Group {
        void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector);

        Iterable<Entry> selectImports();

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/DeferredImportSelector$Group$Entry.class */
        public static class Entry {
            private final AnnotationMetadata metadata;
            private final String importClassName;

            public Entry(AnnotationMetadata metadata, String importClassName) {
                this.metadata = metadata;
                this.importClassName = importClassName;
            }

            public AnnotationMetadata getMetadata() {
                return this.metadata;
            }

            public String getImportClassName() {
                return this.importClassName;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Entry entry = (Entry) o;
                return Objects.equals(this.metadata, entry.metadata) && Objects.equals(this.importClassName, entry.importClassName);
            }

            public int hashCode() {
                return Objects.hash(this.metadata, this.importClassName);
            }
        }
    }
}