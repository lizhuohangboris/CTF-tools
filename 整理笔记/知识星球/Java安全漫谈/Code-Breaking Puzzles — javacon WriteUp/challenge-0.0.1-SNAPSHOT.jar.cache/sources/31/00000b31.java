package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@PerformanceSensitive({"allocation"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ReusableMessage.class */
public interface ReusableMessage extends Message, StringBuilderFormattable {
    Object[] swapParameters(Object[] objArr);

    short getParameterCount();

    Message memento();
}