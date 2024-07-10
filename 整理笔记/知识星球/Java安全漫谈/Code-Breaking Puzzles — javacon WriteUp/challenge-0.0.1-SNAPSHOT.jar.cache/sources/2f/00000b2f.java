package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ParameterizedNoReferenceMessageFactory.class */
public final class ParameterizedNoReferenceMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = 5027639245636870500L;
    public static final ParameterizedNoReferenceMessageFactory INSTANCE = new ParameterizedNoReferenceMessageFactory();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/ParameterizedNoReferenceMessageFactory$StatusMessage.class */
    static class StatusMessage implements Message {
        private static final long serialVersionUID = 4199272162767841280L;
        private final String formattedMessage;
        private final Throwable throwable;

        public StatusMessage(String formattedMessage, Throwable throwable) {
            this.formattedMessage = formattedMessage;
            this.throwable = throwable;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormattedMessage() {
            return this.formattedMessage;
        }

        @Override // org.apache.logging.log4j.message.Message
        public String getFormat() {
            return this.formattedMessage;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Object[] getParameters() {
            return null;
        }

        @Override // org.apache.logging.log4j.message.Message
        public Throwable getThrowable() {
            return this.throwable;
        }
    }

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(String message, Object... params) {
        if (params == null) {
            return new SimpleMessage(message);
        }
        ParameterizedMessage msg = new ParameterizedMessage(message, params);
        return new StatusMessage(msg.getFormattedMessage(), msg.getThrowable());
    }
}