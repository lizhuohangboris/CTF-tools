package org.springframework.core.type.filter;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/filter/AbstractTypeHierarchyTraversingFilter.class */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {
    protected final Log logger = LogFactory.getLog(getClass());
    private final boolean considerInherited;
    private final boolean considerInterfaces;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }

    @Override // org.springframework.core.type.filter.TypeFilter
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String[] interfaceNames;
        String superClassName;
        if (matchSelf(metadataReader)) {
            return true;
        }
        ClassMetadata metadata = metadataReader.getClassMetadata();
        if (matchClassName(metadata.getClassName())) {
            return true;
        }
        if (this.considerInherited && (superClassName = metadata.getSuperClassName()) != null) {
            Boolean superClassMatch = matchSuperClass(superClassName);
            if (superClassMatch != null) {
                if (superClassMatch.booleanValue()) {
                    return true;
                }
            } else {
                try {
                    if (match(metadata.getSuperClassName(), metadataReaderFactory)) {
                        return true;
                    }
                } catch (IOException e) {
                    this.logger.debug("Could not read super class [" + metadata.getSuperClassName() + "] of type-filtered class [" + metadata.getClassName() + "]");
                }
            }
        }
        if (this.considerInterfaces) {
            for (String ifc : metadata.getInterfaceNames()) {
                Boolean interfaceMatch = matchInterface(ifc);
                if (interfaceMatch != null) {
                    if (interfaceMatch.booleanValue()) {
                        return true;
                    }
                } else {
                    try {
                        if (match(ifc, metadataReaderFactory)) {
                            return true;
                        }
                    } catch (IOException e2) {
                        this.logger.debug("Could not read interface [" + ifc + "] for type-filtered class [" + metadata.getClassName() + "]");
                    }
                }
            }
            return false;
        }
        return false;
    }

    private boolean match(String className, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
    }

    protected boolean matchSelf(MetadataReader metadataReader) {
        return false;
    }

    protected boolean matchClassName(String className) {
        return false;
    }

    @Nullable
    protected Boolean matchSuperClass(String superClassName) {
        return null;
    }

    @Nullable
    protected Boolean matchInterface(String interfaceName) {
        return null;
    }
}