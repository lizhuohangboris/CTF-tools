package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/selector/ContextSelector.class */
public interface ContextSelector {
    LoggerContext getLoggerContext();

    LoggerContext getLoggerContext(String str);

    LoggerContext getDefaultLoggerContext();

    LoggerContext detachLoggerContext(String str);

    List<String> getContextNames();
}