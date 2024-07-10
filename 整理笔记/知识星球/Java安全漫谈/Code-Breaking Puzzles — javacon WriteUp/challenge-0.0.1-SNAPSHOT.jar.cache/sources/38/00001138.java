package org.hibernate.validator.internal.metadata.raw;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/BeanConfiguration.class */
public class BeanConfiguration<T> {
    private final ConfigurationSource source;
    private final Class<T> beanClass;
    private final Set<ConstrainedElement> constrainedElements;
    private final List<Class<?>> defaultGroupSequence;
    private final DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider;

    public BeanConfiguration(ConfigurationSource source, Class<T> beanClass, Set<? extends ConstrainedElement> constrainedElements, List<Class<?>> defaultGroupSequence, DefaultGroupSequenceProvider<? super T> defaultGroupSequenceProvider) {
        this.source = source;
        this.beanClass = beanClass;
        this.constrainedElements = CollectionHelper.newHashSet((Collection) constrainedElements);
        this.defaultGroupSequence = defaultGroupSequence;
        this.defaultGroupSequenceProvider = defaultGroupSequenceProvider;
    }

    public ConfigurationSource getSource() {
        return this.source;
    }

    public Class<T> getBeanClass() {
        return this.beanClass;
    }

    public Set<ConstrainedElement> getConstrainedElements() {
        return this.constrainedElements;
    }

    public List<Class<?>> getDefaultGroupSequence() {
        return this.defaultGroupSequence;
    }

    public DefaultGroupSequenceProvider<? super T> getDefaultGroupSequenceProvider() {
        return this.defaultGroupSequenceProvider;
    }

    public String toString() {
        return "BeanConfiguration [beanClass=" + this.beanClass.getSimpleName() + ", source=" + this.source + ", constrainedElements=" + this.constrainedElements + ", defaultGroupSequence=" + this.defaultGroupSequence + ", defaultGroupSequenceProvider=" + this.defaultGroupSequenceProvider + "]";
    }

    public int hashCode() {
        int result = (31 * 1) + (this.beanClass == null ? 0 : this.beanClass.hashCode());
        return (31 * result) + (this.source == null ? 0 : this.source.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BeanConfiguration<?> other = (BeanConfiguration) obj;
        if (this.beanClass == null) {
            if (other.beanClass != null) {
                return false;
            }
        } else if (!this.beanClass.equals(other.beanClass)) {
            return false;
        }
        if (this.source != other.source) {
            return false;
        }
        return true;
    }
}