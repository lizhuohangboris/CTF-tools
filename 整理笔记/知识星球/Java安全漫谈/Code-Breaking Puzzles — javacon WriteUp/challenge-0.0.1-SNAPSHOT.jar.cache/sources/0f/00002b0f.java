package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/scanner/ScannerException.class */
public class ScannerException extends MarkedYAMLException {
    private static final long serialVersionUID = 4782293188600445954L;

    public ScannerException(String context, Mark contextMark, String problem, Mark problemMark, String note) {
        super(context, contextMark, problem, problemMark, note);
    }

    public ScannerException(String context, Mark contextMark, String problem, Mark problemMark) {
        this(context, contextMark, problem, problemMark, null);
    }
}