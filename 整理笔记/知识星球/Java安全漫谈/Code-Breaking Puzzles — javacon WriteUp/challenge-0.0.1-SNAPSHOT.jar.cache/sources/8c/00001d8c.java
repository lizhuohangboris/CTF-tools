package org.springframework.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/GraalDetector.class */
abstract class GraalDetector {
    private static final boolean imageCode;

    GraalDetector() {
    }

    static {
        imageCode = System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }

    public static boolean inImageCode() {
        return imageCode;
    }
}