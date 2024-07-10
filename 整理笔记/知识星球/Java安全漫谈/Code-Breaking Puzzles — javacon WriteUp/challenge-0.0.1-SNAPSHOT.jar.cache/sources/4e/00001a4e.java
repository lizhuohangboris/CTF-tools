package org.springframework.boot.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/system/JavaVersion.class */
public enum JavaVersion {
    EIGHT("1.8", "java.util.function.Function"),
    NINE("1.9", "java.security.cert.URICertStoreParameters");
    
    private final String name;
    private final boolean available;

    JavaVersion(String name, String className) {
        this.name = name;
        this.available = ClassUtils.isPresent(className, getClass().getClassLoader());
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }

    public static JavaVersion getJavaVersion() {
        List<JavaVersion> candidates = Arrays.asList(values());
        Collections.reverse(candidates);
        for (JavaVersion candidate : candidates) {
            if (candidate.available) {
                return candidate;
            }
        }
        return EIGHT;
    }

    public boolean isEqualOrNewerThan(JavaVersion version) {
        return compareTo(version) >= 0;
    }

    public boolean isOlderThan(JavaVersion version) {
        return compareTo(version) < 0;
    }
}