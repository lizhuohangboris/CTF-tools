package org.springframework.remoting.support;

import java.util.HashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocationUtils.class */
public abstract class RemoteInvocationUtils {
    public static void fillInClientStackTraceIfPossible(Throwable ex) {
        if (ex != null) {
            StackTraceElement[] clientStack = new Throwable().getStackTrace();
            Set<Throwable> visitedExceptions = new HashSet<>();
            Throwable th = ex;
            while (true) {
                Throwable exToUpdate = th;
                if (exToUpdate != null && !visitedExceptions.contains(exToUpdate)) {
                    StackTraceElement[] serverStack = exToUpdate.getStackTrace();
                    StackTraceElement[] combinedStack = new StackTraceElement[serverStack.length + clientStack.length];
                    System.arraycopy(serverStack, 0, combinedStack, 0, serverStack.length);
                    System.arraycopy(clientStack, 0, combinedStack, serverStack.length, clientStack.length);
                    exToUpdate.setStackTrace(combinedStack);
                    visitedExceptions.add(exToUpdate);
                    th = exToUpdate.getCause();
                } else {
                    return;
                }
            }
        }
    }
}