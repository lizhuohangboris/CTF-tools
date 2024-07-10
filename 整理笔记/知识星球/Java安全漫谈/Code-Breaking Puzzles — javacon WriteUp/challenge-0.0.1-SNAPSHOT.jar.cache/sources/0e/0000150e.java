package org.springframework.boot;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringBootVersion.class */
public final class SpringBootVersion {
    private SpringBootVersion() {
    }

    public static String getVersion() {
        Package pkg = SpringBootVersion.class.getPackage();
        if (pkg != null) {
            return pkg.getImplementationVersion();
        }
        return null;
    }
}