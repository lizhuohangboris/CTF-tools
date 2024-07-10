package org.apache.logging.log4j.message;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory.class */
public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
    private static final String EXIT_DEFAULT_PREFIX = "Exit";
    private static final String ENTRY_DEFAULT_PREFIX = "Enter";
    private static final long serialVersionUID = 8578655591131397576L;
    private final String entryText;
    private final String exitText;

    public DefaultFlowMessageFactory() {
        this(ENTRY_DEFAULT_PREFIX, EXIT_DEFAULT_PREFIX);
    }

    public DefaultFlowMessageFactory(String entryText, String exitText) {
        this.entryText = entryText;
        this.exitText = exitText;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$AbstractFlowMessage.class */
    private static class AbstractFlowMessage implements FlowMessage {
        private static final long serialVersionUID = 1;
        private final Message message;
        private final String text;

        AbstractFlowMessage(String text, Message message) {
            this.message = message;
            this.text = text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormattedMessage() {
            if (this.message != null) {
                return this.text + " " + this.message.getFormattedMessage();
            }
            return this.text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormat() {
            if (this.message != null) {
                return this.text + ": " + this.message.getFormat();
            }
            return this.text;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Object[] getParameters() {
            if (this.message != null) {
                return this.message.getParameters();
            }
            return null;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Throwable getThrowable() {
            if (this.message != null) {
                return this.message.getThrowable();
            }
            return null;
        }

        @Override // org.apache.logging.log4j.message.FlowMessage
        public Message getMessage() {
            return this.message;
        }

        @Override // org.apache.logging.log4j.message.FlowMessage
        public String getText() {
            return this.text;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$SimpleEntryMessage.class */
    private static final class SimpleEntryMessage extends AbstractFlowMessage implements EntryMessage {
        private static final long serialVersionUID = 1;

        SimpleEntryMessage(String entryText, Message message) {
            super(entryText, message);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/DefaultFlowMessageFactory$SimpleExitMessage.class */
    private static final class SimpleExitMessage extends AbstractFlowMessage implements ExitMessage {
        private static final long serialVersionUID = 1;
        private final Object result;
        private final boolean isVoid;

        SimpleExitMessage(String exitText, EntryMessage message) {
            super(exitText, message.getMessage());
            this.result = null;
            this.isVoid = true;
        }

        SimpleExitMessage(String exitText, Object result, EntryMessage message) {
            super(exitText, message.getMessage());
            this.result = result;
            this.isVoid = false;
        }

        SimpleExitMessage(String exitText, Object result, Message message) {
            super(exitText, message);
            this.result = result;
            this.isVoid = false;
        }

        @Override // org.apache.logging.log4j.message.DefaultFlowMessageFactory.AbstractFlowMessage, org.apache.logging.log4j.message.Message
        public String getFormattedMessage() {
            String formattedMessage = super.getFormattedMessage();
            if (this.isVoid) {
                return formattedMessage;
            }
            return formattedMessage + ": " + this.result;
        }
    }

    public String getEntryText() {
        return this.entryText;
    }

    public String getExitText() {
        return this.exitText;
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public EntryMessage newEntryMessage(Message message) {
        return new SimpleEntryMessage(this.entryText, makeImmutable(message));
    }

    private Message makeImmutable(Message message) {
        if (!(message instanceof ReusableMessage)) {
            return message;
        }
        return new SimpleMessage(message.getFormattedMessage());
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(EntryMessage message) {
        return new SimpleExitMessage(this.exitText, message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(Object result, EntryMessage message) {
        return new SimpleExitMessage(this.exitText, result, message);
    }

    @Override // org.apache.logging.log4j.message.FlowMessageFactory
    public ExitMessage newExitMessage(Object result, Message message) {
        return new SimpleExitMessage(this.exitText, result, message);
    }
}