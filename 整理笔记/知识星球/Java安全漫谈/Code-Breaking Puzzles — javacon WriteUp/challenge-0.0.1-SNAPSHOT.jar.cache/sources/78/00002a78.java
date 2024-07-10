package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/Construct.class */
public interface Construct {
    Object construct(Node node);

    void construct2ndStep(Node node, Object obj);
}