package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

/* compiled from: IfAction.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/conditional/IfState.class */
class IfState {
    Boolean boolResult;
    List<SaxEvent> thenSaxEventList;
    List<SaxEvent> elseSaxEventList;
    boolean active;
}