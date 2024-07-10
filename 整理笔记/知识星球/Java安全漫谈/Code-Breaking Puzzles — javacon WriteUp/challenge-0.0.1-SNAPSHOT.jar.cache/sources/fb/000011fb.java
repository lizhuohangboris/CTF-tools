package org.jboss.logging;

import java.util.logging.Level;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/JDKLevel.class */
final class JDKLevel extends Level {
    private static final long serialVersionUID = 1;
    public static final JDKLevel FATAL = new JDKLevel("FATAL", 1100);
    public static final JDKLevel ERROR = new JDKLevel("ERROR", 1000);
    public static final JDKLevel WARN = new JDKLevel("WARN", 900);
    public static final JDKLevel INFO = new JDKLevel("INFO", 800);
    public static final JDKLevel DEBUG = new JDKLevel("DEBUG", 500);
    public static final JDKLevel TRACE = new JDKLevel("TRACE", 400);

    protected JDKLevel(String name, int value) {
        super(name, value);
    }

    protected JDKLevel(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }
}