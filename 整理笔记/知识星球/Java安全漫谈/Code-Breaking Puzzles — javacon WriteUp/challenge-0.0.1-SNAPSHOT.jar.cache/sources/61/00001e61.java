package org.springframework.core.env;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertySources.class */
public interface PropertySources extends Iterable<PropertySource<?>> {
    boolean contains(String str);

    @Nullable
    PropertySource<?> get(String str);

    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}