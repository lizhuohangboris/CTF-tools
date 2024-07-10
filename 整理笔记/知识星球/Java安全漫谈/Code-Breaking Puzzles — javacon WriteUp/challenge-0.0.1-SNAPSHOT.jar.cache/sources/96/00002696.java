package org.springframework.web.servlet.resource;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/VersionPathStrategy.class */
public interface VersionPathStrategy {
    @Nullable
    String extractVersion(String str);

    String removeVersion(String str, String str2);

    String addVersion(String str, String str2);
}