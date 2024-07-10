package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/conditional/ElseAction.class */
public class ElseAction extends ThenOrElseActionBase {
    @Override // ch.qos.logback.core.joran.conditional.ThenOrElseActionBase
    void registerEventList(IfAction ifAction, List<SaxEvent> eventList) {
        ifAction.setElseSaxEventList(eventList);
    }
}