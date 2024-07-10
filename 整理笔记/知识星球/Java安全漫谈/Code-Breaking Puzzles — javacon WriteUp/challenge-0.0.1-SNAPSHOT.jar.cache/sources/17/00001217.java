package org.jboss.logging;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/SerializedLogger.class */
final class SerializedLogger implements Serializable {
    private static final long serialVersionUID = 508779982439435831L;
    private final String name;

    public SerializedLogger(String name) {
        this.name = name;
    }

    protected Object readResolve() {
        return Logger.getLogger(this.name);
    }
}