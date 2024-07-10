package ch.qos.logback.classic;

import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/BasicConfigurator.class */
public class BasicConfigurator extends ContextAwareBase implements Configurator {
    @Override // ch.qos.logback.classic.spi.Configurator
    public void configure(LoggerContext lc) {
        addInfo("Setting up default configuration.");
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(lc);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(lc);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(lc);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        Logger rootLogger = lc.getLogger("ROOT");
        rootLogger.addAppender(ca);
    }
}