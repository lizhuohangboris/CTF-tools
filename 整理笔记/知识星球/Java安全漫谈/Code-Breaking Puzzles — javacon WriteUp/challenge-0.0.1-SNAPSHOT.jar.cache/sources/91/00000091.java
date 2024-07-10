package ch.qos.logback.classic.spi;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/spi/PlatformInfo.class */
public class PlatformInfo {
    private static final int UNINITIALIZED = -1;
    private static int hasJMXObjectName = -1;

    public static boolean hasJMXObjectName() {
        if (hasJMXObjectName == -1) {
            try {
                Class.forName("javax.management.ObjectName");
                hasJMXObjectName = 1;
            } catch (Throwable th) {
                hasJMXObjectName = 0;
            }
        }
        return hasJMXObjectName == 1;
    }
}