package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/sift/AbstractAppenderFactoryUsingJoran.class */
public abstract class AbstractAppenderFactoryUsingJoran<E> implements AppenderFactory<E> {
    final List<SaxEvent> eventList;
    protected String key;
    protected Map<String, String> parentPropertyMap;

    public abstract SiftingJoranConfiguratorBase<E> getSiftingJoranConfigurator(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
        this.eventList = removeSiftElement(eventList);
        this.key = key;
        this.parentPropertyMap = parentPropertyMap;
    }

    List<SaxEvent> removeSiftElement(List<SaxEvent> eventList) {
        return eventList.subList(1, eventList.size() - 1);
    }

    @Override // ch.qos.logback.core.sift.AppenderFactory
    public Appender<E> buildAppender(Context context, String discriminatingValue) throws JoranException {
        SiftingJoranConfiguratorBase<E> sjc = getSiftingJoranConfigurator(discriminatingValue);
        sjc.setContext(context);
        sjc.doConfigure(this.eventList);
        return sjc.getAppender();
    }

    public List<SaxEvent> getEventList() {
        return this.eventList;
    }
}