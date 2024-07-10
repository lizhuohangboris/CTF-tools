package org.apache.logging.log4j.message;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/message/SimpleMessageFactory.class */
public final class SimpleMessageFactory extends AbstractMessageFactory {
    public static final SimpleMessageFactory INSTANCE = new SimpleMessageFactory();
    private static final long serialVersionUID = 4418995198790088516L;

    @Override // org.apache.logging.log4j.message.MessageFactory
    public Message newMessage(String message, Object... params) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return new SimpleMessage(message);
    }

    @Override // org.apache.logging.log4j.message.AbstractMessageFactory, org.apache.logging.log4j.message.MessageFactory2
    public Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return new SimpleMessage(message);
    }
}