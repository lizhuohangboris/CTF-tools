package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/SpringVersion.class */
public final class SpringVersion {
    private SpringVersion() {
    }

    @Nullable
    public static String getVersion() {
        Package pkg = SpringVersion.class.getPackage();
        if (pkg != null) {
            return pkg.getImplementationVersion();
        }
        return null;
    }
}