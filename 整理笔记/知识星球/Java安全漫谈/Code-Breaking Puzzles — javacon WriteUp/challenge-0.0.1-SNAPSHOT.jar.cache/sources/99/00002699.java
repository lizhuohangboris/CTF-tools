package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/VersionStrategy.class */
public interface VersionStrategy extends VersionPathStrategy {
    String getResourceVersion(Resource resource);
}