package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/composer/Composer.class */
public class Composer {
    protected final Parser parser;
    private final Resolver resolver;
    private final Map<String, Node> anchors = new HashMap();
    private final Set<Node> recursiveNodes = new HashSet();

    public Composer(Parser parser, Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
    }

    public boolean checkNode() {
        if (this.parser.checkEvent(Event.ID.StreamStart)) {
            this.parser.getEvent();
        }
        return !this.parser.checkEvent(Event.ID.StreamEnd);
    }

    public Node getNode() {
        this.parser.getEvent();
        Node node = composeNode(null);
        this.parser.getEvent();
        this.anchors.clear();
        this.recursiveNodes.clear();
        return node;
    }

    public Node getSingleNode() {
        this.parser.getEvent();
        Node document = null;
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            document = getNode();
        }
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            Event event = this.parser.getEvent();
            throw new ComposerException("expected a single document in the stream", document.getStartMark(), "but found another document", event.getStartMark());
        }
        this.parser.getEvent();
        return document;
    }

    private Node composeNode(Node parent) {
        Node node;
        if (parent != null) {
            this.recursiveNodes.add(parent);
        }
        if (this.parser.checkEvent(Event.ID.Alias)) {
            AliasEvent event = (AliasEvent) this.parser.getEvent();
            String anchor = event.getAnchor();
            if (!this.anchors.containsKey(anchor)) {
                throw new ComposerException(null, null, "found undefined alias " + anchor, event.getStartMark());
            }
            node = this.anchors.get(anchor);
            if (this.recursiveNodes.remove(node)) {
                node.setTwoStepsConstruction(true);
            }
        } else {
            String anchor2 = ((NodeEvent) this.parser.peekEvent()).getAnchor();
            node = this.parser.checkEvent(Event.ID.Scalar) ? composeScalarNode(anchor2) : this.parser.checkEvent(Event.ID.SequenceStart) ? composeSequenceNode(anchor2) : composeMappingNode(anchor2);
        }
        this.recursiveNodes.remove(parent);
        return node;
    }

    protected Node composeScalarNode(String anchor) {
        Tag nodeTag;
        ScalarEvent ev = (ScalarEvent) this.parser.getEvent();
        String tag = ev.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.scalar, ev.getValue(), ev.getImplicit().canOmitTagInPlainScalar());
            resolved = true;
        } else {
            nodeTag = new Tag(tag);
        }
        Node node = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev.getScalarStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        return node;
    }

    protected Node composeSequenceNode(String anchor) {
        Tag nodeTag;
        SequenceStartEvent startEvent = (SequenceStartEvent) this.parser.getEvent();
        String tag = startEvent.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.sequence, null, startEvent.getImplicit());
            resolved = true;
        } else {
            nodeTag = new Tag(tag);
        }
        ArrayList<Node> children = new ArrayList<>();
        SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getStartMark(), (Mark) null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
            children.add(composeNode(node));
        }
        Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }

    protected Node composeMappingNode(String anchor) {
        Tag nodeTag;
        MappingStartEvent startEvent = (MappingStartEvent) this.parser.getEvent();
        String tag = startEvent.getTag();
        boolean resolved = false;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.mapping, null, startEvent.getImplicit());
            resolved = true;
        } else {
            nodeTag = new Tag(tag);
        }
        List<NodeTuple> children = new ArrayList<>();
        MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getStartMark(), (Mark) null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
            composeMappingChildren(children, node);
        }
        Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }

    protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
        Node itemKey = composeKeyNode(node);
        if (itemKey.getTag().equals(Tag.MERGE)) {
            node.setMerged(true);
        }
        Node itemValue = composeValueNode(node);
        children.add(new NodeTuple(itemKey, itemValue));
    }

    protected Node composeKeyNode(MappingNode node) {
        return composeNode(node);
    }

    protected Node composeValueNode(MappingNode node) {
        return composeNode(node);
    }
}