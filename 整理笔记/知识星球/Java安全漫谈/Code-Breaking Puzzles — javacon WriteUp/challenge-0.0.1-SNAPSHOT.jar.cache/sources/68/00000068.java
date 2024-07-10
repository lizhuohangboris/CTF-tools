package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/NamedConverter.class */
public abstract class NamedConverter extends ClassicConverter {
    Abbreviator abbreviator = null;

    protected abstract String getFullyQualifiedName(ILoggingEvent iLoggingEvent);

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String optStr = getFirstOption();
        if (optStr != null) {
            try {
                int targetLen = Integer.parseInt(optStr);
                if (targetLen == 0) {
                    this.abbreviator = new ClassNameOnlyAbbreviator();
                } else if (targetLen > 0) {
                    this.abbreviator = new TargetLengthBasedClassNameAbbreviator(targetLen);
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        String fqn = getFullyQualifiedName(event);
        if (this.abbreviator == null) {
            return fqn;
        }
        return this.abbreviator.abbreviate(fqn);
    }
}