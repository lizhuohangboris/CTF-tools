package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.sift.AbstractAppenderFactoryUsingJoran;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/sift/AppenderFactoryUsingJoran.class */
public class AppenderFactoryUsingJoran extends AbstractAppenderFactoryUsingJoran<ILoggingEvent> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
        super(eventList, key, parentPropertyMap);
    }

    @Override // ch.qos.logback.core.sift.AbstractAppenderFactoryUsingJoran
    public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(String discriminatingValue) {
        return new SiftingJoranConfigurator(this.key, discriminatingValue, this.parentPropertyMap);
    }
}