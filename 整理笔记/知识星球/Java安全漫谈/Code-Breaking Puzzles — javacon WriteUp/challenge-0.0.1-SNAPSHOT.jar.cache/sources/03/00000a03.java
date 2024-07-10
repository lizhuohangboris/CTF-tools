package org.apache.coyote;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/ContainerThreadMarker.class */
public class ContainerThreadMarker {
    private static final ThreadLocal<Boolean> marker = new ThreadLocal<>();

    public static boolean isContainerThread() {
        Boolean flag = marker.get();
        if (flag == null) {
            return false;
        }
        return flag.booleanValue();
    }

    public static void set() {
        marker.set(Boolean.TRUE);
    }

    public static void clear() {
        marker.set(Boolean.FALSE);
    }
}