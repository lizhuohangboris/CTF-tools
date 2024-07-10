package org.apache.tomcat.util;

import java.lang.reflect.InvocationTargetException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/ExceptionUtils.class */
public class ExceptionUtils {
    public static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (!(t instanceof StackOverflowError) && (t instanceof VirtualMachineError)) {
            throw ((VirtualMachineError) t);
        }
    }

    public static Throwable unwrapInvocationTargetException(Throwable t) {
        if ((t instanceof InvocationTargetException) && t.getCause() != null) {
            return t.getCause();
        }
        return t;
    }

    public static void preload() {
    }
}