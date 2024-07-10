package ch.qos.logback.core.joran.spi;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/NoAutoStartUtil.class */
public class NoAutoStartUtil {
    public static boolean notMarkedWithNoAutoStart(Object o) {
        if (o == null) {
            return false;
        }
        Class<?> clazz = o.getClass();
        NoAutoStart a = (NoAutoStart) clazz.getAnnotation(NoAutoStart.class);
        return a == null;
    }
}