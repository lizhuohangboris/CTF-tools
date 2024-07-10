package org.springframework.boot.system;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/system/SystemProperties.class */
public final class SystemProperties {
    private SystemProperties() {
    }

    public static String get(String... properties) {
        String override;
        for (String property : properties) {
            try {
                String override2 = System.getProperty(property);
                override = override2 != null ? override2 : System.getenv(property);
            } catch (Throwable ex) {
                System.err.println("Could not resolve '" + property + "' as system property: " + ex);
            }
            if (override != null) {
                return override;
            }
        }
        return null;
    }
}