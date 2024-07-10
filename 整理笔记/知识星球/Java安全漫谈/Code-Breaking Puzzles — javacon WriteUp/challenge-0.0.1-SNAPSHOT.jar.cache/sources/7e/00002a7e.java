package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/constructor/ConstructorException.class */
public class ConstructorException extends MarkedYAMLException {
    private static final long serialVersionUID = -8816339931365239910L;

    /* JADX INFO: Access modifiers changed from: protected */
    public ConstructorException(String context, Mark contextMark, String problem, Mark problemMark, Throwable cause) {
        super(context, contextMark, problem, problemMark, cause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConstructorException(String context, Mark contextMark, String problem, Mark problemMark) {
        this(context, contextMark, problem, problemMark, null);
    }
}