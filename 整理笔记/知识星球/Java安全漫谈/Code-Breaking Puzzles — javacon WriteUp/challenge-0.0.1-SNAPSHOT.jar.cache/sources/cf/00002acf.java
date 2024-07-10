package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/nodes/CollectionNode.class */
public abstract class CollectionNode<T> extends Node {
    private DumperOptions.FlowStyle flowStyle;

    public abstract List<T> getValue();

    public CollectionNode(Tag tag, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(tag, startMark, endMark);
        setFlowStyle(flowStyle);
    }

    @Deprecated
    public CollectionNode(Tag tag, Mark startMark, Mark endMark, Boolean flowStyle) {
        this(tag, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(flowStyle));
    }

    public DumperOptions.FlowStyle getFlowStyle() {
        return this.flowStyle;
    }

    public void setFlowStyle(DumperOptions.FlowStyle flowStyle) {
        if (flowStyle == null) {
            throw new NullPointerException("Flow style must be provided.");
        }
        this.flowStyle = flowStyle;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}