package org.jboss.logging;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/LoggerProvider.class */
public interface LoggerProvider {
    Logger getLogger(String str);

    void clearMdc();

    Object putMdc(String str, Object obj);

    Object getMdc(String str);

    void removeMdc(String str);

    Map<String, Object> getMdcMap();

    void clearNdc();

    String getNdc();

    int getNdcDepth();

    String popNdc();

    String peekNdc();

    void pushNdc(String str);

    void setNdcMaxDepth(int i);
}