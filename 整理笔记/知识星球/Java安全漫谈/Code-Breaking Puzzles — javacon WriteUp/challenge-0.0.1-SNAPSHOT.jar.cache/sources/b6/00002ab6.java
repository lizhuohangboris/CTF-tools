package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/events/ScalarEvent.class */
public final class ScalarEvent extends NodeEvent {
    private final String tag;
    private final DumperOptions.ScalarStyle style;
    private final String value;
    private final ImplicitTuple implicit;

    public ScalarEvent(String anchor, String tag, ImplicitTuple implicit, String value, Mark startMark, Mark endMark, DumperOptions.ScalarStyle style) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        if (value == null) {
            throw new NullPointerException("Value must be provided.");
        }
        this.value = value;
        if (style == null) {
            throw new NullPointerException("Style must be provided.");
        }
        this.style = style;
    }

    @Deprecated
    public ScalarEvent(String anchor, String tag, ImplicitTuple implicit, String value, Mark startMark, Mark endMark, Character style) {
        this(anchor, tag, implicit, value, startMark, endMark, DumperOptions.ScalarStyle.createStyle(style));
    }

    public String getTag() {
        return this.tag;
    }

    public DumperOptions.ScalarStyle getScalarStyle() {
        return this.style;
    }

    @Deprecated
    public Character getStyle() {
        return this.style.getChar();
    }

    public String getValue() {
        return this.value;
    }

    public ImplicitTuple getImplicit() {
        return this.implicit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.yaml.snakeyaml.events.NodeEvent, org.yaml.snakeyaml.events.Event
    public String getArguments() {
        return super.getArguments() + ", tag=" + this.tag + ", " + this.implicit + ", value=" + this.value;
    }

    @Override // org.yaml.snakeyaml.events.Event
    public boolean is(Event.ID id) {
        return Event.ID.Scalar == id;
    }

    public boolean isPlain() {
        return this.style == DumperOptions.ScalarStyle.PLAIN;
    }
}