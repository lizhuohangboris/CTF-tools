package org.springframework.beans;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/BeanMetadataAttribute.class */
public class BeanMetadataAttribute implements BeanMetadataElement {
    private final String name;
    @Nullable
    private final Object value;
    @Nullable
    private Object source;

    public BeanMetadataAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanMetadataAttribute)) {
            return false;
        }
        BeanMetadataAttribute otherMa = (BeanMetadataAttribute) other;
        return this.name.equals(otherMa.name) && ObjectUtils.nullSafeEquals(this.value, otherMa.value) && ObjectUtils.nullSafeEquals(this.source, otherMa.source);
    }

    public int hashCode() {
        return (this.name.hashCode() * 29) + ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return "metadata attribute '" + this.name + "'";
    }
}