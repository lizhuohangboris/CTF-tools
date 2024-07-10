package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/events/SequenceEndEvent.class */
public final class SequenceEndEvent extends CollectionEndEvent {
    public SequenceEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    @Override // org.yaml.snakeyaml.events.Event
    public boolean is(Event.ID id) {
        return Event.ID.SequenceEnd == id;
    }
}