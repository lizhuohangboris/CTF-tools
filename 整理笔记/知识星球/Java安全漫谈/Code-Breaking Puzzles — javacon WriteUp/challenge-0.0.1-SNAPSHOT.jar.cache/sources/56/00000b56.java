package org.apache.logging.log4j.spi;

import java.io.Closeable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerAdapter.class */
public interface LoggerAdapter<L> extends Closeable {
    L getLogger(String str);
}