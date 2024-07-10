package org.springframework.beans.factory.support;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/AutowireCandidateQualifier.class */
public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor {
    public static final String VALUE_KEY = "value";
    private final String typeName;

    public AutowireCandidateQualifier(Class<?> type) {
        this(type.getName());
    }

    public AutowireCandidateQualifier(String typeName) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
    }

    public AutowireCandidateQualifier(Class<?> type, Object value) {
        this(type.getName(), value);
    }

    public AutowireCandidateQualifier(String typeName, Object value) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
        setAttribute("value", value);
    }

    public String getTypeName() {
        return this.typeName;
    }
}