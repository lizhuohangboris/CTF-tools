package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/FlowMessageFactory.class */
public interface FlowMessageFactory {
    EntryMessage newEntryMessage(Message message);

    ExitMessage newExitMessage(Object obj, Message message);

    ExitMessage newExitMessage(EntryMessage entryMessage);

    ExitMessage newExitMessage(Object obj, EntryMessage entryMessage);
}