package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/event/stax/EndEvent.class */
public class EndEvent extends StaxEvent {
    public EndEvent(String name, Location location) {
        super(name, location);
    }

    public String toString() {
        return "EndEvent(" + getName() + ")  [" + this.location.getLineNumber() + "," + this.location.getColumnNumber() + "]";
    }
}