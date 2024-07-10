package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.Arrays;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/selector/DefaultContextSelector.class */
public class DefaultContextSelector implements ContextSelector {
    private LoggerContext defaultLoggerContext;

    public DefaultContextSelector(LoggerContext context) {
        this.defaultLoggerContext = context;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getLoggerContext() {
        return getDefaultLoggerContext();
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultLoggerContext;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext detachLoggerContext(String loggerContextName) {
        return this.defaultLoggerContext;
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public List<String> getContextNames() {
        return Arrays.asList(this.defaultLoggerContext.getName());
    }

    @Override // ch.qos.logback.classic.selector.ContextSelector
    public LoggerContext getLoggerContext(String name) {
        if (this.defaultLoggerContext.getName().equals(name)) {
            return this.defaultLoggerContext;
        }
        return null;
    }
}