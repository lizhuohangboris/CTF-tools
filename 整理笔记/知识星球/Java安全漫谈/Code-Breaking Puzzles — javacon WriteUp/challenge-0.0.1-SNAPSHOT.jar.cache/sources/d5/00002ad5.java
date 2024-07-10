package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/nodes/SequenceNode.class */
public class SequenceNode extends CollectionNode<Node> {
    private final List<Node> value;

    public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, DumperOptions.FlowStyle flowStyle) {
        super(tag, startMark, endMark, flowStyle);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.resolved = resolved;
    }

    public SequenceNode(Tag tag, List<Node> value, DumperOptions.FlowStyle flowStyle) {
        this(tag, true, value, (Mark) null, (Mark) null, flowStyle);
    }

    @Deprecated
    public SequenceNode(Tag tag, List<Node> value, Boolean style) {
        this(tag, value, DumperOptions.FlowStyle.fromBoolean(style));
    }

    @Deprecated
    public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, Boolean style) {
        this(tag, resolved, value, startMark, endMark, DumperOptions.FlowStyle.fromBoolean(style));
    }

    @Override // org.yaml.snakeyaml.nodes.Node
    public NodeId getNodeId() {
        return NodeId.sequence;
    }

    @Override // org.yaml.snakeyaml.nodes.CollectionNode
    public List<Node> getValue() {
        return this.value;
    }

    public void setListType(Class<? extends Object> listType) {
        for (Node node : this.value) {
            node.setType(listType);
        }
    }

    public String toString() {
        return "<" + getClass().getName() + " (tag=" + getTag() + ", value=" + getValue() + ")>";
    }
}