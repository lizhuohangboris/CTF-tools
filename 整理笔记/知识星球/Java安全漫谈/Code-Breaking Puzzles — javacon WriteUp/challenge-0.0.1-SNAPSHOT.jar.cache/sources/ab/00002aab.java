package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/events/AliasEvent.class */
public final class AliasEvent extends NodeEvent {
    public AliasEvent(String anchor, Mark startMark, Mark endMark) {
        super(anchor, startMark, endMark);
        if (anchor == null) {
            throw new NullPointerException();
        }
    }

    @Override // org.yaml.snakeyaml.events.Event
    public boolean is(Event.ID id) {
        return Event.ID.Alias == id;
    }
}