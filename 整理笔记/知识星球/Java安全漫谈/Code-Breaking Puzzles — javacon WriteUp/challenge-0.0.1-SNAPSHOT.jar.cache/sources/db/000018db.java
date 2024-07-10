package org.springframework.boot.context;

import java.io.IOException;
import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/TypeExcludeFilter.class */
public class TypeExcludeFilter implements TypeFilter, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.core.type.filter.TypeFilter
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        if ((this.beanFactory instanceof ListableBeanFactory) && getClass() == TypeExcludeFilter.class) {
            Collection<TypeExcludeFilter> delegates = ((ListableBeanFactory) this.beanFactory).getBeansOfType(TypeExcludeFilter.class).values();
            for (TypeExcludeFilter delegate : delegates) {
                if (delegate.match(metadataReader, metadataReaderFactory)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean equals(Object obj) {
        throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented equals");
    }

    public int hashCode() {
        throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented hashCode");
    }
}