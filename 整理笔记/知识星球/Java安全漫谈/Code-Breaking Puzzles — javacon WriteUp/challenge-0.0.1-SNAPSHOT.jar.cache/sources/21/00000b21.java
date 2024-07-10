package org.apache.logging.log4j.message;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/Message.class */
public interface Message extends Serializable {
    String getFormattedMessage();

    String getFormat();

    Object[] getParameters();

    Throwable getThrowable();
}