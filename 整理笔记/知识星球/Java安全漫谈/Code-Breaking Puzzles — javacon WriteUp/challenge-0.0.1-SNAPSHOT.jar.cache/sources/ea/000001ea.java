package ch.qos.logback.core.status;

import java.io.PrintStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/status/OnErrorConsoleStatusListener.class */
public class OnErrorConsoleStatusListener extends OnPrintStreamStatusListenerBase {
    @Override // ch.qos.logback.core.status.OnPrintStreamStatusListenerBase
    protected PrintStream getPrintStream() {
        return System.err;
    }
}