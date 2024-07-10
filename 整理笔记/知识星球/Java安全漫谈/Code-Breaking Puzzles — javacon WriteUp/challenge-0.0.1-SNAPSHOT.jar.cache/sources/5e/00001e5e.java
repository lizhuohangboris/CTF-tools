package org.springframework.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertySource.class */
public abstract class PropertySource<T> {
    protected final Log logger;
    protected final String name;
    protected final T source;

    @Nullable
    public abstract Object getProperty(String str);

    public PropertySource(String name, T source) {
        this.logger = LogFactory.getLog(getClass());
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }

    public PropertySource(String name) {
        this(name, new Object());
    }

    public String getName() {
        return this.name;
    }

    public T getSource() {
        return this.source;
    }

    public boolean containsProperty(String name) {
        return getProperty(name) != null;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof PropertySource) && ObjectUtils.nullSafeEquals(this.name, ((PropertySource) other).name));
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.name);
    }

    public String toString() {
        if (this.logger.isDebugEnabled()) {
            return getClass().getSimpleName() + "@" + System.identityHashCode(this) + " {name='" + this.name + "', properties=" + this.source + "}";
        }
        return getClass().getSimpleName() + " {name='" + this.name + "'}";
    }

    public static PropertySource<?> named(String name) {
        return new ComparisonPropertySource(name);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertySource$StubPropertySource.class */
    public static class StubPropertySource extends PropertySource<Object> {
        public StubPropertySource(String name) {
            super(name, new Object());
        }

        @Override // org.springframework.core.env.PropertySource
        @Nullable
        public String getProperty(String name) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertySource$ComparisonPropertySource.class */
    public static class ComparisonPropertySource extends StubPropertySource {
        private static final String USAGE_ERROR = "ComparisonPropertySource instances are for use with collection comparison only";

        public ComparisonPropertySource(String name) {
            super(name);
        }

        @Override // org.springframework.core.env.PropertySource
        public Object getSource() {
            throw new UnsupportedOperationException(USAGE_ERROR);
        }

        @Override // org.springframework.core.env.PropertySource
        public boolean containsProperty(String name) {
            throw new UnsupportedOperationException(USAGE_ERROR);
        }

        @Override // org.springframework.core.env.PropertySource.StubPropertySource, org.springframework.core.env.PropertySource
        @Nullable
        public String getProperty(String name) {
            throw new UnsupportedOperationException(USAGE_ERROR);
        }
    }
}