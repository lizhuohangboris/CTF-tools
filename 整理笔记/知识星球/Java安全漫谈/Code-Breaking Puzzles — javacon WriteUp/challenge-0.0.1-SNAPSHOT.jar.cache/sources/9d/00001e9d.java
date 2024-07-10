package org.springframework.core.io.support;

import java.io.IOException;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/support/PropertySourceFactory.class */
public interface PropertySourceFactory {
    PropertySource<?> createPropertySource(@Nullable String str, EncodedResource encodedResource) throws IOException;
}