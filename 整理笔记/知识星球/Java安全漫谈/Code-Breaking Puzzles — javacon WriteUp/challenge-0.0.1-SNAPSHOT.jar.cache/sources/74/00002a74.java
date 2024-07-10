package org.yaml.snakeyaml.composer;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/composer/ComposerException.class */
public class ComposerException extends MarkedYAMLException {
    private static final long serialVersionUID = 2146314636913113935L;

    /* JADX INFO: Access modifiers changed from: protected */
    public ComposerException(String context, Mark contextMark, String problem, Mark problemMark) {
        super(context, contextMark, problem, problemMark);
    }
}