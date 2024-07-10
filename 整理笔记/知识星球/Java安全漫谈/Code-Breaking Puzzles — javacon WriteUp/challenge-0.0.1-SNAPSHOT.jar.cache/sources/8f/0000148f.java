package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/MethodOverride.class */
public abstract class MethodOverride implements BeanMetadataElement {
    private final String methodName;
    private boolean overloaded = true;
    @Nullable
    private Object source;

    public abstract boolean matches(Method method);

    /* JADX INFO: Access modifiers changed from: protected */
    public MethodOverride(String methodName) {
        Assert.notNull(methodName, "Method name must not be null");
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isOverloaded() {
        return this.overloaded;
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
        if (!(other instanceof MethodOverride)) {
            return false;
        }
        MethodOverride that = (MethodOverride) other;
        return ObjectUtils.nullSafeEquals(this.methodName, that.methodName) && ObjectUtils.nullSafeEquals(this.source, that.source);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.methodName);
        return (29 * hashCode) + ObjectUtils.nullSafeHashCode(this.source);
    }
}