package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.ArrayList;
import java.util.List;

/* compiled from: ThenOrElseActionBase.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/conditional/ThenActionState.class */
class ThenActionState implements InPlayListener {
    List<SaxEvent> eventList = new ArrayList();
    boolean isRegistered = false;

    @Override // ch.qos.logback.core.joran.event.InPlayListener
    public void inPlay(SaxEvent event) {
        this.eventList.add(event);
    }
}