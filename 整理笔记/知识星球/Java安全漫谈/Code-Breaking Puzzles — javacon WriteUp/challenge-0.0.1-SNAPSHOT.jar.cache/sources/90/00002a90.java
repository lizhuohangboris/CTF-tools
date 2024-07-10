package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import org.yaml.snakeyaml.events.Event;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/emitter/Emitable.class */
public interface Emitable {
    void emit(Event event) throws IOException;
}