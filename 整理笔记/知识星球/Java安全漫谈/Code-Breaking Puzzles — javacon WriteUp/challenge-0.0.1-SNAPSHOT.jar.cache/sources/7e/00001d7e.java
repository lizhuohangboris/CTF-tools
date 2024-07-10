package org.springframework.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/AttributeAccessorSupport.class */
public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable {
    private final Map<String, Object> attributes = new LinkedHashMap();

    @Override // org.springframework.core.AttributeAccessor
    public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }

    @Override // org.springframework.core.AttributeAccessor
    @Nullable
    public Object getAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.get(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    @Nullable
    public Object removeAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.remove(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    public boolean hasAttribute(String name) {
        Assert.notNull(name, "Name must not be null");
        return this.attributes.containsKey(name);
    }

    @Override // org.springframework.core.AttributeAccessor
    public String[] attributeNames() {
        return StringUtils.toStringArray(this.attributes.keySet());
    }

    public void copyAttributesFrom(AttributeAccessor source) {
        Assert.notNull(source, "Source must not be null");
        String[] attributeNames = source.attributeNames();
        for (String attributeName : attributeNames) {
            setAttribute(attributeName, source.getAttribute(attributeName));
        }
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof AttributeAccessorSupport) && this.attributes.equals(((AttributeAccessorSupport) other).attributes));
    }

    public int hashCode() {
        return this.attributes.hashCode();
    }
}