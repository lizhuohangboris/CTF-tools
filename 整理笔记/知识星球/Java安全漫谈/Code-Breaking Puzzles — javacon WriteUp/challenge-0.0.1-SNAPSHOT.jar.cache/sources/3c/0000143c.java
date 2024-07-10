package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/Location.class */
public class Location {
    private final Resource resource;
    @Nullable
    private final Object source;

    public Location(Resource resource) {
        this(resource, null);
    }

    public Location(Resource resource, @Nullable Object source) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.source = source;
    }

    public Resource getResource() {
        return this.resource;
    }

    @Nullable
    public Object getSource() {
        return this.source;
    }
}