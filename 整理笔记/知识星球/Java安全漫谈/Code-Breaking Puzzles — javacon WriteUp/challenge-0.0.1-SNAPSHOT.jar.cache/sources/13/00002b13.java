package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.nodes.Node;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/serializer/AnchorGenerator.class */
public interface AnchorGenerator {
    String nextAnchor(Node node);
}