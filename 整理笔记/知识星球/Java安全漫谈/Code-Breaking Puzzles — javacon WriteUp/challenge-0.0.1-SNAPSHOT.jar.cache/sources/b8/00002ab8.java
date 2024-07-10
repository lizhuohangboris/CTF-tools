package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/events/SequenceStartEvent.class */
public final class SequenceStartEvent extends CollectionStartEvent {
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Deprecated
    public SequenceStartEvent(String anchor, String tag, boolean implicit, Mark startMark, Mark endMark, Boolean flowStyle) {
        this(anchor, tag, implicit, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(flowStyle));
    }

    @Override // org.yaml.snakeyaml.events.Event
    public boolean is(Event.ID id) {
        return Event.ID.SequenceStart == id;
    }
}