package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.io.ResourceLoader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/ResourceLoaderAware.class */
public interface ResourceLoaderAware extends Aware {
    void setResourceLoader(ResourceLoader resourceLoader);
}