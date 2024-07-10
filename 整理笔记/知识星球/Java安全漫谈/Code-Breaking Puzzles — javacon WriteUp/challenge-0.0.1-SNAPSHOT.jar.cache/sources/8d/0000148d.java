package org.springframework.beans.factory.support;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/ManagedSet.class */
public class ManagedSet<E> extends LinkedHashSet<E> implements Mergeable, BeanMetadataElement {
    @Nullable
    private Object source;
    @Nullable
    private String elementTypeName;
    private boolean mergeEnabled;

    public ManagedSet() {
    }

    public ManagedSet(int initialCapacity) {
        super(initialCapacity);
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void setElementTypeName(@Nullable String elementTypeName) {
        this.elementTypeName = elementTypeName;
    }

    @Nullable
    public String getElementTypeName() {
        return this.elementTypeName;
    }

    public void setMergeEnabled(boolean mergeEnabled) {
        this.mergeEnabled = mergeEnabled;
    }

    @Override // org.springframework.beans.Mergeable
    public boolean isMergeEnabled() {
        return this.mergeEnabled;
    }

    @Override // org.springframework.beans.Mergeable
    public Set<E> merge(@Nullable Object parent) {
        if (!this.mergeEnabled) {
            throw new IllegalStateException("Not allowed to merge when the 'mergeEnabled' property is set to 'false'");
        }
        if (parent == null) {
            return this;
        }
        if (!(parent instanceof Set)) {
            throw new IllegalArgumentException("Cannot merge with object of type [" + parent.getClass() + "]");
        }
        Set<E> merged = new ManagedSet<>();
        merged.addAll((Set) parent);
        merged.addAll(this);
        return merged;
    }
}