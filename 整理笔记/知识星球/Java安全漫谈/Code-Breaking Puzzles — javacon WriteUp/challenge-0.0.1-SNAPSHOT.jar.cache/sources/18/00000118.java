package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/event/stax/StaxEvent.class */
public class StaxEvent {
    final String name;
    final Location location;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaxEvent(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location;
    }
}