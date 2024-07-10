package ch.qos.logback.classic.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/encoder/PatternLayoutEncoder.class */
public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
    @Override // ch.qos.logback.core.encoder.LayoutWrappingEncoder, ch.qos.logback.core.encoder.EncoderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
}